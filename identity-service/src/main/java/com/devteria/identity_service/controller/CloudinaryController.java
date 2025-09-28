package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class CloudinaryController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ApiResponse.<String>builder()
                    .data(imageUrl)
                    .code(1000)
                    .message("Image uploaded successfully")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.<String>builder()
                    .message("Image upload failed: " + e.getMessage())
                    .build();
        }
    }
}
