package com.adocat.adocat_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${spring.cloudinary.url}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    // 📌 Subir archivo a carpeta específica (usuarios, gatos, documentos, etc.)
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String fileName = UUID.randomUUID().toString();
            Map<String, Object> params = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true,
                    "folder", folder,
                    "public_id", fileName,
                    "resource_type", "auto" // 🔥 Esto permite subir imágenes Y PDFs
            );


            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("❌ Error al subir archivo a Cloudinary", e);
        }
    }

    // 📌 Eliminar archivo por URL (solo si se desea reemplazar imagen previa)
    public void deleteByUrl(String fileUrl) {
        try {
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo eliminar el archivo: " + e.getMessage());
        }
    }

    private String extractPublicId(String fileUrl) {
        // Ejemplo: https://res.cloudinary.com/tu-cloud/image/upload/v1680000000/users/abc123.jpg
        String[] parts = fileUrl.split("/");
        String versionAndPath = fileUrl.substring(fileUrl.indexOf("/upload/") + 8);
        return versionAndPath.replaceFirst("\\.[a-z]+$", ""); // quita extensión (.jpg, .png, etc.)
    }
}
