package com.adm.lucas.microblog.domain.flame;

import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "flames")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user", "note"})
@ToString(exclude = {"user", "note"})
public class Flame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn
    @ManyToOne
    private User user;

    @JoinColumn
    @ManyToOne
    private Note note;

    public Flame(User user, Note note) {
        this.user = user;
        this.note = note;
    }

}