package com.adocat.adocat_api;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FirebaseConnectionTest {

    @Test
    public void testFirestoreConnection() {
        try {
            // ✅ Verifica que Firebase ya esté inicializado por Spring
            assertTrue(FirebaseApp.getApps().size() > 0, "FirebaseApp no está inicializado");

            // ✅ Intenta acceder a Firestore
            Firestore firestore = FirestoreClient.getFirestore();
            assertNotNull(firestore, "Firestore no está disponible");

            // ✅ Acción simple para validar conexión
            firestore.listCollections();

        } catch (Exception e) {
            fail("Error al conectar con Firestore: " + e.getMessage());
        }
    }
}
