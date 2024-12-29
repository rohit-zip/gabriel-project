package com.gn128.validator.implementation;

import com.gn128.exception.payloads.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
public class ImagesListValidator {

    public void validate(List<MultipartFile> images) {
        if (Objects.nonNull(images)) {
            if (images.size() > 9) throw new BadRequestException("Images Limit is exceeded", HttpStatus.BAD_REQUEST);
            checkDuplicate(images);
            images.forEach(this::validateImage);
        }
    }

    private static void checkDuplicate(List<MultipartFile> images) {
        HashSet<String> files = new HashSet<>();
        for (MultipartFile file : images) {
            String uniqueIdentifier = file.getOriginalFilename() + "-" + file.getSize();
            if (!files.add(uniqueIdentifier)) {
                throw new BadRequestException("Duplicate file", HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void validateImage(MultipartFile multipartFile) {
        if (!isImageByExtension(multipartFile)) throw new BadRequestException("Invalid image extension", HttpStatus.BAD_REQUEST);
        DataSize dataSize = DataSize.ofMegabytes(2);
        if (multipartFile.getSize() > dataSize.toBytes()) {
            throw new BadRequestException("Image size exceeded", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean isImageByExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (Objects.isNull(originalFilename)) {
            throw new BadRequestException("Invalid Image name", HttpStatus.BAD_REQUEST);
        }
        String fileExtension = getFileExtension(originalFilename);
        return isImageExtension(fileExtension);
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private static boolean isImageExtension(String extension) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "bmp", "svg"};
        for (String a : imageExtensions) {
            if (extension.equals(a)) {
                return true;
            }
        }
        return false;
    }
}
