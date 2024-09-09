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

    private String title;

    @Column(columnDefinition = "TEXT")
    private String markdown;

    private boolean modified = false;

    private boolean closed;

    private boolean hidden;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    public Note(User user, String title, String markdown, boolean closed, boolean hidden, List<Tag> tags) {
        this.user = user;
        this.title = title;
        this.markdown = markdown;
        this.closed = closed;
        this.hidden = hidden;
        if (tags != null) this.tags = tags;
    }

}