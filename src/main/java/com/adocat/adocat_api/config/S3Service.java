package com.adocat.adocat_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3Service(
            @Value("${aws.s3.accessKey}") String accessKey,
            @Value("${aws.s3.secretKey}") String secretKey,
            @Value("${aws.s3.region}") String region) {

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    // 📌 Subir archivo a S3 y retornar URL pública
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;

        try {
            String extension = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String fileName = UUID.randomUUID().toString() + extension;
            String key = folder + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();


            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucket + ".s3.amazonaws.com/" + URLEncoder.encode(key, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("❌ Error al subir archivo a S3", e);
        }
    }

    // 📌 Eliminar archivo desde su URL
    public void deleteByUrl(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo eliminar el archivo de S3: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        // https://bucket-name.s3.amazonaws.com/folder/archivo.pdf
        int index = fileUrl.indexOf(".s3.amazonaws.com/") + ".s3.amazonaws.com/".length();
        return fileUrl.substring(index);
    }
}


//package com.adocat.adocat_api.config;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//public class S3Service {
//
//    private final Cloudinary cloudinary;
//
//    public S3Service(@Value("${spring.cloudinary.url}") String cloudinaryUrl) {
//        this.cloudinary = new Cloudinary(cloudinaryUrl);
//    }
//
//    // 📌 Subir archivo a carpeta específica (usuarios, gatos, documentos, etc.)
//    public String uploadFile(MultipartFile file, String folder) {
//        if (file == null || file.isEmpty()) {
//            return null;
//        }
//
//        try {
//            String fileName = UUID.randomUUID().toString();
//            Map<String, Object> params = ObjectUtils.asMap(
//                    "use_filename", true,
//                    "unique_filename", false,
//                    "overwrite", true,
//                    "folder", folder,
//                    "public_id", fileName,
//                    "resource_type", "auto" // 🔥 Esto permite subir imágenes Y PDFs
//            );
//
//
//            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
//            return uploadResult.get("secure_url").toString();
//        } catch (IOException e) {
//            throw new RuntimeException("❌ Error al subir archivo a Cloudinary", e);
//        }
//    }
//
//    // 📌 Eliminar archivo por URL (solo si se desea reemplazar imagen previa)
//    public void deleteByUrl(String fileUrl) {
//        try {
//            String publicId = extractPublicId(fileUrl);
//            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//        } catch (Exception e) {
//            System.err.println("⚠️ No se pudo eliminar el archivo: " + e.getMessage());
//        }
//    }
//
//    private String extractPublicId(String fileUrl) {
//        // Ejemplo: https://res.cloudinary.com/tu-cloud/image/upload/v1680000000/users/abc123.jpg
//        String[] parts = fileUrl.split("/");
//        String versionAndPath = fileUrl.substring(fileUrl.indexOf("/upload/") + 8);
//        return versionAndPath.replaceFirst("\\.[a-z]+$", ""); // quita extensión (.jpg, .png, etc.)
//    }
//}
