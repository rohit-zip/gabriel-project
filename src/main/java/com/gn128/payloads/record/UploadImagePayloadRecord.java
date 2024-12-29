package com.gn128.payloads.record;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadImagePayloadRecord(
        String path,
        String userId,
        List<MultipartFile> files,
        String uploadFor
) {
}
