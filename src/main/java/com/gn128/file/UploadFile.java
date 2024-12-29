package com.gn128.file;

import com.gn128.utils.RandomGenerators;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@Component
@Slf4j
public class UploadFile {

    @SneakyThrows(value = {IOException.class})
    public String process(MultipartFile image, String path, String userId) {
        String month = LocalDate.now().getMonth().toString();
        String originalFilename = image.getOriginalFilename();
        String randomName = RandomGenerators.generateRandomImageName(userId);
        assert originalFilename != null;
        String imageName = randomName.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        String imagePath = path + File.separator + month + File.separator + imageName;
        File file = new File(path + File.separator + month);
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            log.warn("Path Created for Image : {}", mkdir);
        }
        Files.copy(image.getInputStream(), Paths.get(imagePath));
        return imageName;
    }
}
