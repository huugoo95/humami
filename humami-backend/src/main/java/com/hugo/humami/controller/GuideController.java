package com.hugo.humami.controller;

import com.hugo.humami.dto.request.GuideAccessRequest;
import com.hugo.humami.dto.request.GuideRequest;
import com.hugo.humami.dto.response.GuideAccessResponse;
import com.hugo.humami.dto.response.GuideDocumentResponse;
import com.hugo.humami.dto.response.GuideResponse;
import com.hugo.humami.service.GuideService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/guides")
public class GuideController {
    private final GuideService guideService;

    public GuideController(GuideService guideService) { this.guideService = guideService; }

    @GetMapping("")
    public ResponseEntity<List<GuideResponse>> listPublished() { return ResponseEntity.ok(guideService.listPublished()); }

    @GetMapping("/{slug}")
    public ResponseEntity<GuideResponse> getPublished(@PathVariable String slug) throws IOException { return ResponseEntity.ok(guideService.getPublishedBySlug(slug)); }

    @PostMapping("")
    public ResponseEntity<GuideResponse> create(@RequestBody GuideRequest request) { return new ResponseEntity<>(guideService.create(request), HttpStatus.CREATED); }

    @PatchMapping("/{id}")
    public ResponseEntity<GuideResponse> update(@PathVariable String id, @RequestBody GuideRequest request) { return ResponseEntity.ok(guideService.update(id, request)); }

    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> setCover(@PathVariable String id, @RequestPart("image") MultipartFile image) throws IOException { guideService.setCoverImage(id, image); return ResponseEntity.ok().build(); }

    @PutMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> setDocument(@PathVariable String id, @RequestPart("document") MultipartFile document) throws IOException { guideService.setDocument(id, document); return ResponseEntity.ok().build(); }

    @PostMapping("/{slug}/access")
    public ResponseEntity<GuideAccessResponse> requestAccess(@PathVariable String slug, @RequestBody GuideAccessRequest request,
                                                              HttpServletRequest httpRequest, HttpServletResponse response) {
        String token = guideService.requestAccess(slug, request, clientIp(httpRequest));
        setAccessCookie(response, slug, token);
        return ResponseEntity.ok(new GuideAccessResponse("/guias/" + slug + "/leer"));
    }

    @GetMapping("/{slug}/redeem")
    public ResponseEntity<Void> redeem(@PathVariable String slug, @RequestParam String token, HttpServletResponse response) {
        setAccessCookie(response, slug, guideService.redeem(slug, token));
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/guias/" + slug + "/leer").build();
    }

    @GetMapping("/{slug}/document")
    public ResponseEntity<GuideDocumentResponse> document(@PathVariable String slug, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(guideService.getDocument(slug, readAccessCookie(request, slug)));
    }

    private String clientIp(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        return realIp != null && !realIp.isBlank() ? realIp.trim() : request.getRemoteAddr();
    }

    private void setAccessCookie(HttpServletResponse response, String slug, String token) {
        Cookie cookie = new Cookie(accessCookieName(slug), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
    }

    private String readAccessCookie(HttpServletRequest request, String slug) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> accessCookieName(slug).equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String accessCookieName(String slug) {
        return "humami_guide_" + slug.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
