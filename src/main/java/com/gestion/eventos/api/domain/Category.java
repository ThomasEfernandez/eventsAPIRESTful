package com.gestion.eventos.api.domain;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
@Table(name="categories")
public class Category {
    @Id @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column (name = "name", nullable = false, unique = true)
    private String name;
    @Column (length = 255, name = "description")
    private String description;

}
