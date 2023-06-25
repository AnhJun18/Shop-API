package com.myshop.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Data
@Builder
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "firebase")

public class FirebaseSingleton {
    public String bucketName;

    public String imageUrl;

    public  String prefixImageUrl;

    public  String suffixImageUrl;

    private static FirebaseSingleton instanceFirebase;

    private FirebaseSingleton() {
        try {

            ClassPathResource serviceAccount = new ClassPathResource("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(this.bucketName)
                    .build();

            return FirebaseApp.initializeApp(options);

        } catch (Exception ex) {
            return null;
        }
    }

    public static FirebaseApp getInstance() {
        if (instanceFirebase == null) {
            instanceFirebase = new in();
        }
        return instanceFirebase;
    }

}
