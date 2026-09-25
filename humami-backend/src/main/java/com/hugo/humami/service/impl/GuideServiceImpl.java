package com.hugo.humami.service.impl;

import com.hugo.humami.domain.guides.GuideAccessTokenEntity;
import com.hugo.humami.domain.guides.GuideEntity;
import com.hugo.humami.domain.guides.GuideLeadEntity;
import com.hugo.humami.dto.request.GuideAccessRequest;
import com.hugo.humami.dto.request.GuideRequest;
import com.hugo.humami.dto.response.GuideDocumentResponse;
import com.hugo.humami.dto.response.GuideResponse;
import com.hugo.humami.mapper.GuideMapper;
import com.hugo.humami.repository.GuideAccessTokenRepository;
import com.hugo.humami.repository.GuideLeadRepository;
import com.hugo.humami.repository.GuideRepository;
import com.hugo.humami.service.GuideAccessEmailSender;
import com.hugo.humami.service.GuideService;
import com.hugo.humami.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.*;

@Service
public class GuideServiceImpl implements GuideService {
    private static final String PUBLISHED = "published";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final GuideRepository guideRepository;
    private final GuideLeadRepository guideLeadRepository;
    private final GuideAccessTokenRepository tokenRepository;
    private final GuideMapper guideMapper;
    private final S3Service s3Service;
    private final GuideAccessRateLimiter rateLimiter;
    private final GuideAccessEmailSender emailSender;
    private final String publicApiUrl;
    private final Duration accessDuration;

    public GuideServiceImpl(GuideRepository guideRepository, GuideLeadRepository guideLeadRepository,
                            GuideAccessTokenRepository tokenRepository, GuideMapper guideMapper, S3Service s3Service,
                            GuideAccessRateLimiter rateLimiter, GuideAccessEmailSender emailSender,
                            @Value("${humami.guides.public-api-url:https://humami.es/api}") String publicApiUrl,
                            @Value("${humami.guides.access-days:30}") long accessDays) {
        this.guideRepository = guideRepository;
        this.guideLeadRepository = guideLeadRepository;
        this.tokenRepository = tokenRepository;
        this.guideMapper = guideMapper;
        this.s3Service = s3Service;
        this.rateLimiter = rateLimiter;
        this.emailSender = emailSender;
        this.publicApiUrl = publicApiUrl.replaceAll("/$", "");
        this.accessDuration = Duration.ofDays(accessDays);
    }

    @Override
    public List<GuideResponse> listPublished() {
        return guideRepository.findByStatusOrderByPublishedAtDesc(PUBLISHED).stream().map(guideMapper::toResponse).toList();
    }

    @Override
    public GuideResponse getPublishedBySlug(String slug) throws IOException {
        GuideEntity guide = findPublished(slug);
        GuideResponse response = guideMapper.toResponse(guide);
        if (guide.getCoverImage() != null && !guide.getCoverImage().isBlank()) response.setCoverImage(s3Service.getTempUrl(guide.getCoverImage()));
        return response;
    }

    @Override
    public GuideResponse create(GuideRequest request) {
        requireSlug(request.getSlug());
        if (guideRepository.existsBySlug(request.getSlug())) throw new ResponseStatusException(CONFLICT, "slug already exists");
        GuideEntity guide = guideMapper.toEntity(request);
        Instant now = Instant.now();
        guide.setCreatedAt(now);
        guide.setUpdatedAt(now);
        return guideMapper.toResponse(guideRepository.save(guide));
    }

    @Override
    public GuideResponse update(String id, GuideRequest request) {
        GuideEntity guide = guideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Guide not found"));
        if (request.getSlug() != null && !request.getSlug().equals(guide.getSlug())) {
            requireSlug(request.getSlug());
            if (guideRepository.existsBySlug(request.getSlug())) throw new ResponseStatusException(CONFLICT, "slug already exists");
        }
        guideMapper.updateFromRequest(request, guide);
        guide.setUpdatedAt(Instant.now());
        return guideMapper.toResponse(guideRepository.save(guide));
    }

    @Override
    public void setCoverImage(String id, MultipartFile image) throws IOException {
        GuideEntity guide = guideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Guide not found"));
        if (image == null || image.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "cover image is required");
        guide.setCoverImage(s3Service.uploadImage(image, "guide-" + guide.getSlug()));
        guide.setUpdatedAt(Instant.now());
        guideRepository.save(guide);
    }

    @Override
    public void setDocument(String id, MultipartFile document) throws IOException {
        GuideEntity guide = guideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Guide not found"));
        guide.setDocumentKey(s3Service.uploadGuideDocument(document, "guide-" + guide.getSlug()));
        guide.setUpdatedAt(Instant.now());
        guideRepository.save(guide);
    }

    @Override
    public String requestAccess(String slug, GuideAccessRequest request, String clientIp) {
        GuideEntity guide = findPublished(slug);
        String email = normalizeEmail(request.getEmail());
        if (!request.isPrivacyAccepted() || request.getPrivacyNoticeVersion() == null || request.getPrivacyNoticeVersion().isBlank()) throw new ResponseStatusException(BAD_REQUEST, "privacy acknowledgement is required");
        if (request.getWebsite() != null && !request.getWebsite().isBlank()) throw new ResponseStatusException(BAD_REQUEST, "invalid request");
        rateLimiter.check(email, clientIp == null ? "unknown" : clientIp);

        Instant now = Instant.now();
        GuideLeadEntity lead = guideLeadRepository.findByGuideIdAndEmail(guide.getId(), email).orElseGet(GuideLeadEntity::new);
        if (lead.getId() == null) {
            lead.setGuideId(guide.getId());
            lead.setEmail(email);
            lead.setCreatedAt(now);
        }
        lead.setPrivacyNoticeVersion(request.getPrivacyNoticeVersion());
        lead.setDeliveryAcknowledgedAt(now);
        lead.setSource(request.getSource());
        if (request.isMarketingOptIn() && !lead.isMarketingOptIn()) lead.setMarketingOptInAt(now);
        lead.setMarketingOptIn(request.isMarketingOptIn());
        lead.setUpdatedAt(now);
        lead = guideLeadRepository.save(lead);

        String rawToken = createToken(guide, lead, now);
        String emailUrl = publicApiUrl + "/guides/" + guide.getSlug() + "/redeem?token=" + rawToken;
        emailSender.sendAccessEmail(email, guide.getTitle(), emailUrl);
        return rawToken;
    }

    @Override
    public String redeem(String slug, String token) {
        GuideAccessTokenEntity access = validateTokenForGuide(slug, token);
        access.setRedeemedAt(Instant.now());
        tokenRepository.save(access);
        return token;
    }

    @Override
    public GuideDocumentResponse getDocument(String slug, String token) throws IOException {
        GuideAccessTokenEntity access = validateTokenForGuide(slug, token);
        GuideEntity guide = findPublished(slug);
        if (guide.getDocumentKey() == null || guide.getDocumentKey().isBlank()) throw new ResponseStatusException(NOT_FOUND, "Guide document unavailable");
        return new GuideDocumentResponse(s3Service.getTempUrl(guide.getDocumentKey()));
    }

    private GuideEntity findPublished(String slug) {
        return guideRepository.findBySlugAndStatus(slug, PUBLISHED).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Guide not found"));
    }

    private GuideAccessTokenEntity validateTokenForGuide(String slug, String token) {
        if (token == null || token.isBlank()) throw new ResponseStatusException(UNAUTHORIZED, "Guide access is required");
        GuideEntity guide = findPublished(slug);
        GuideAccessTokenEntity access = tokenRepository.findByTokenHash(hash(token)).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Guide access is required"));
        if (!access.getGuideId().equals(guide.getId()) || access.getExpiresAt().isBefore(Instant.now()) || access.getRevokedAt() != null) throw new ResponseStatusException(UNAUTHORIZED, "Guide access is required");
        return access;
    }

    private String createToken(GuideEntity guide, GuideLeadEntity lead, Instant now) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        GuideAccessTokenEntity access = new GuideAccessTokenEntity();
        access.setTokenHash(hash(rawToken));
        access.setGuideId(guide.getId());
        access.setLeadId(lead.getId());
        access.setCreatedAt(now);
        access.setExpiresAt(now.plus(accessDuration));
        tokenRepository.save(access);
        return rawToken;
    }

    private static void requireSlug(String slug) { if (slug == null || slug.isBlank()) throw new ResponseStatusException(BAD_REQUEST, "slug is required"); }
    private static String normalizeEmail(String email) {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new ResponseStatusException(BAD_REQUEST, "valid email is required");
        return email.trim().toLowerCase(Locale.ROOT);
    }
    private static String hash(String value) {
        try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException exception) { throw new IllegalStateException("SHA-256 unavailable", exception); }
    }
}
