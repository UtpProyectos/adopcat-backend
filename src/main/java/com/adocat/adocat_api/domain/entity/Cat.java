package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cats")
@Data
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String age;
    private String gender;
    private String imageUrl;
}
