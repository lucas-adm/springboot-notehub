package xyz.xisyz.domain.tag;

import xyz.xisyz.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"id", "notes"})
@ToString(exclude = {"id", "notes"})
@EqualsAndHashCode(exclude = {"id", "notes"})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Note> notes = new ArrayList<>();

    private String name;

    public Tag(String name) {
        this.name = name;
    }

}