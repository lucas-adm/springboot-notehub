package com.adm.lucas.microblog.domain.flame;

import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "flames", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id, note_id"})})
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user", "note"})
@ToString(exclude = {"user", "note"})
@EqualsAndHashCode(exclude = "id")
public class Flame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "note_id")
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Note note;

    public Flame(User user, Note note) {
        this.user = user;
        this.note = note;
    }

}