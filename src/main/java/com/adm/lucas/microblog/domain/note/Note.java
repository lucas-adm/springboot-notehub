package com.adm.lucas.microblog.domain.note;

import com.adm.lucas.microblog.domain.tag.Tag;
import com.adm.lucas.microblog.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notes")
@NoArgsConstructor
@Data
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Instant createdAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String markdown;

    private boolean modified = false;

    private boolean closed;

    private boolean visible;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    public Note(User user, String markdown, boolean closed, boolean visible, List<String> tags) {
        this.user = user;
        this.markdown = markdown;
        this.closed = closed;
        this.visible = visible;
        if (tags != null) this.tags = tags.stream().map(Tag::new).toList();
    }

}