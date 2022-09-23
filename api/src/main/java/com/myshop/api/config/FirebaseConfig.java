package com.myshop.api.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Builder
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "firebase")
public class FirebaseConfig{

    public String bucketName;

    public String imageUrl;

    public  String prefixImageUrl;

    public  String suffixImageUrl;

}
