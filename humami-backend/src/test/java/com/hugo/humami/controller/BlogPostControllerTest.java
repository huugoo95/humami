package com.hugo.humami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import com.hugo.humami.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlogPostController.class)
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=test-secret")
class BlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogPostService blogPostService;

    @Test
    void shouldListPublishedPosts() throws Exception {
        BlogPostResponse post = new BlogPostResponse();
        post.setSlug("post-1");
        when(blogPostService.listPublished()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/blog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("post-1"));

        verify(blogPostService).listPublished();
    }

    @Test
    void shouldGetPublishedBySlug() throws Exception {
        BlogPostResponse post = new BlogPostResponse();
        post.setTitle("Hello");
        when(blogPostService.getPublishedBySlug("hello")).thenReturn(post);

        mockMvc.perform(get("/api/blog/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hello"));

        verify(blogPostService).getPublishedBySlug("hello");
    }

    @Test
    void shouldRejectCreateWithoutSecretHeader() throws Exception {
        mockMvc.perform(post("/api/blog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BlogPostRequest())))
                .andExpect(status().isUnauthorized());

        verify(blogPostService, never()).create(any(BlogPostRequest.class));
    }

    @Test
    void shouldCreateWithValidSecretHeader() throws Exception {
        BlogPostResponse created = new BlogPostResponse();
        created.setId("blog-1");
        when(blogPostService.create(any(BlogPostRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/blog")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BlogPostRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("blog-1"));

        verify(blogPostService).create(any(BlogPostRequest.class));
    }

    @Test
    void shouldUpdateWithValidSecretHeader() throws Exception {
        BlogPostResponse updated = new BlogPostResponse();
        updated.setTitle("Updated post");
        when(blogPostService.update(eq("blog-1"), any(BlogPostRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/blog/blog-1")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BlogPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated post"));

        verify(blogPostService).update(eq("blog-1"), any(BlogPostRequest.class));
    }

    @Test
    void shouldSetCoverWithValidSecretHeader() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "image-data".getBytes());

        mockMvc.perform(multipart("/api/blog/{id}/cover", "blog-1")
                        .file(image)
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(blogPostService).setCoverImage(eq("blog-1"), any(org.springframework.web.multipart.MultipartFile.class));
    }
}
