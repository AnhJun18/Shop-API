package com.myshop.api.service.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.myshop.api.config.FirebaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;


@Service
public class FirebaseImageServiceImpl implements IImageService {

    @Autowired
    FirebaseConfig properties;

    @EventListener
    public void init(ApplicationReadyEvent event) {

        // initialize Firebase
        try {
            properties.initApp();
        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    @Override
    public String getImageUrl(String name) {
        return String.format(properties.imageUrl, name);
    }

    @Override
    public String save(FilePart file) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = _generateRandomFileName();
        System.out.println(file.headers().getContentType().toString());
        bucket.create(name,convertFilePartToByteArray(file),file.headers().getContentType().toString());
        return properties.prefixImageUrl.concat(name).concat(properties.suffixImageUrl);
    }

    @Override
    public String saveBase64String(String base64String) throws IOException {
        String contentType=(base64String.split(";base64,")[0]).split(":")[1];
        byte[] bytes = Base64.getDecoder().decode(base64String.split(";base64,")[1]);
        Bucket bucket = StorageClient.getInstance().bucket();
        String name=_generateRandomFileName();
        bucket.create(name, bytes,contentType);
        return properties.prefixImageUrl.concat(name).concat(properties.suffixImageUrl);
    }

    @Override
    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {

        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));

        com.google.cloud.storage.Bucket bucket = StorageClient.getInstance().bucket();

        String name = _generateRandomFileName();

        bucket.create(name, bytes);

        return name;
    }

    @Override
    public void delete(String name) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        if (StringUtils.isEmpty(name)) {
            throw new IOException("invalid file name");
        }

        Blob blob = bucket.get(name);

        if (blob == null) {
            throw new IOException("file not found");
        }

        blob.delete();
    }


}
