package com.adocat.adocat_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.env}")
    private String firebaseEnvKey;

    @PostConstruct
    public void init() {
        try {
            String encodedConfig = System.getenv(firebaseEnvKey);

            if (encodedConfig == null || encodedConfig.isEmpty()) {
                throw new IllegalStateException("Variable de entorno '" + firebaseEnvKey + "' no está definida.");
            }

            byte[] decodedBytes = Base64.getDecoder().decode(encodedConfig);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(decodedBytes)))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ FirebaseApp inicializado correctamente.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar Firebase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar Firebase", e);
        }
    }
}
