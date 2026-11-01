package com.gestion.eventos.api.domain;


import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
@Table(name="speakers")
public class Speaker {

    @Id @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (length = 255, name = "bio")
    private String bio;

    @ManyToMany (mappedBy = "speakers")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore 
    private Set<Event> events = new HashSet<>();
}
