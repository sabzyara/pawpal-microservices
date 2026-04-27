package com.example.pawpalapp.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dmlt3vuzv",
                "api_key", "487573298287372",
                "api_secret", "GLIddye4-D3nAXlTxEy10iXlVcU"
        ));
    }

    public String upload(MultipartFile file) {
        try {
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            return result.get("url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }
    }
}