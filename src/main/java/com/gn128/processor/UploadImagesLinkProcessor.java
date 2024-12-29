package com.gn128.processor;

import com.gn128.constants.EnvironmentConstants;
import com.gn128.entity.embeddable.ImageLinks;
import com.gn128.file.UploadFile;
import com.gn128.payloads.record.UploadImagePayloadRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadImagesLinkProcessor {

    private final UploadFile uploadFile;
    private final Environment environment;

    public List<ImageLinks> process(UploadImagePayloadRecord uploadImagePayloadRecord) {
        List<MultipartFile> images = uploadImagePayloadRecord.files();
        return images
                .stream()
                .map(image -> {
                    String imageName = uploadFile.process(image, uploadImagePayloadRecord.path(), uploadImagePayloadRecord.userId());
                    return ImageLinks
                            .builder()
                            .link(generateLink(imageName, uploadImagePayloadRecord.uploadFor()))
                            .size(String.valueOf(image.getSize()))
                            .type(imageName.substring(imageName.lastIndexOf(".")))
                            .name(imageName)
                            .build();
                }).toList();
    }

    private String generateLink(String imageName, String uploadFor) {
        String url = environment.getProperty(EnvironmentConstants.BLOB_STORAGE_BASE_URL);
        return url +
                "/" +
                uploadFor +
                "/" +
                LocalDate.now().getMonth().toString() +
                "/" +
                imageName;
    }
}
