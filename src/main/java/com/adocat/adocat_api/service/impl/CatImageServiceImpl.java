package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.cat.CatPhotoResponse;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CatImageServiceImpl {

    private static final String ROOT_COLLECTION = "cats";

    public List<CatPhotoResponse> getPhotosByCatId(UUID catId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference photosCollection = db.collection(ROOT_COLLECTION)
                .document(catId.toString())
                .collection("photos");

        ApiFuture<QuerySnapshot> future = photosCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> CatPhotoResponse.builder()
                        .id(doc.getId())
                        .url(doc.getString("url"))
                        .createdBy(doc.getString("createdBy"))
                        .createdAt(doc.getString("createdAt"))
                        .build())
                .collect(Collectors.toList());
    }

}
