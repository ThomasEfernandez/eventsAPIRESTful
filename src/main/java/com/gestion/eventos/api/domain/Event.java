package com.gestion.eventos.api.domain;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Table(name = "events")
@Entity
public class Event {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    @Column (name = "name", nullable = false)
    private String name;
    @Column (length = 255, name = "description")
    private String description;
    @Column (name = "date", nullable = false)
    private LocalDate date;
    @Column (length = 255, name = "location", nullable = false)
    private String location;
    

    @ManyToMany (fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable (
        name= "event_speakers",
        joinColumns = @JoinColumn (name = "event_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn (name = "speaker_id", referencedColumnName = "id") 
    )
    //precaución y seguridad para evitar bucles infinitos en la serialización
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Speaker> speakers = new HashSet<>();




     @ManyToOne (fetch = FetchType.LAZY)
    @JoinTable (
        name= "category_id",
        joinColumns = @JoinColumn (name = "event_id", referencedColumnName = "id",nullable = false)
    )
    private Category category;




    @ManyToMany (mappedBy="attendedEvents",fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> attendedUsers = new HashSet<>();
public void addSpeakers (Speaker speaker){
    this.speakers.add(speaker);
    speaker.getEvents().add(this);
}
   public void removeSpeaker (Speaker speaker){
    this.speakers.remove(speaker);
    speaker.getEvents().remove(this);
   }

}
