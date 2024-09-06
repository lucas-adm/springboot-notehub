package com.adm.lucas.microblog.domain.tag;

import com.adm.lucas.microblog.domain.note.Note;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@Data
@ToString(exclude = {"id", "notes"})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(mappedBy = "tags")
    private List<Note> notes = new ArrayList<>();

    private String name;

    public Tag(String name) {
        this.name = name;
    }

}