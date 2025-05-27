package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.cat.CatFeatureRequest;
import com.adocat.adocat_api.api.dto.cat.CatFeatureResponse;
import com.adocat.adocat_api.service.interfaces.ICatFeatureService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatFeatureServiceImpl implements ICatFeatureService {

    private static final String ROOT_COLLECTION = "cats";

    @Override
    public void saveFeaturesForCat(UUID catId, List<CatFeatureRequest> features, String createdBy) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        for (CatFeatureRequest feature : features) {
            DocumentReference docRef = db.collection(ROOT_COLLECTION)
                    .document(catId.toString())
                    .collection("features")
                    .document(feature.getId());

            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", feature.getName());
                data.put("createdBy", createdBy);
                data.put("createdAt", Instant.now().toString());
                docRef.set(data);
            }
        }
    }

    @Override
    public List<CatFeatureResponse> getFeaturesByCatId(UUID catId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference featureCollection = db.collection(ROOT_COLLECTION)
                .document(catId.toString())
                .collection("features");

        ApiFuture<QuerySnapshot> future = featureCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> CatFeatureResponse.builder()
                        .id(doc.getId())
                        .name(doc.getString("name"))
                        .createdBy(doc.getString("createdBy"))
                        .createdAt(doc.getString("createdAt"))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CatFeatureResponse> getAllFeatures() throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference featuresRef = db.collection("cats_features");

        ApiFuture<QuerySnapshot> future = featuresRef.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> CatFeatureResponse.builder()
                        .id(doc.getId())
                        .name(doc.getString("name"))
                        .createdBy(doc.getString("createdBy"))
                        .createdAt(doc.getString("createdAt"))
                        .build())
                .collect(Collectors.toList());
    }

}
