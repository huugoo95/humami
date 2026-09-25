package com.hugo.humami.controller;

import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.service.AboutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    private final AboutService aboutService;

    public AboutController(AboutService aboutService) {
        this.aboutService = aboutService;
    }

    @GetMapping
    public ResponseEntity<AboutResponse> getAbout() {
        return ResponseEntity.ok(aboutService.getAbout());
    }

    @PatchMapping
    public ResponseEntity<AboutResponse> updateAbout(@RequestBody AboutRequest request) {
        return ResponseEntity.ok(aboutService.updateAbout(request));
    }

    @PutMapping("/image")
    public ResponseEntity<AboutResponse> updateAboutImage(@RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(aboutService.updateAboutImage(image));
    }
}
