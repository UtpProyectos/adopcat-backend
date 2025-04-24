package com.adocat.adocat_api.security;

import com.adocat.adocat_api.api.dto.auth.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                Utils.getDefaultTransport(),
                Utils.getDefaultJsonFactory()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleUser verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                var payload = idToken.getPayload();
                return new GoogleUser(
                        payload.getEmail(),
                        (String) payload.get("given_name"),
                        (String) payload.get("family_name")
                );
            }
            throw new RuntimeException("ID token inválido");
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar token de Google", e);
        }
    }
}
