package com.adm.lucas.microblog.domain.note;

import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.flame.Flame;
import com.adm.lucas.microblog.domain.tag.Tag;
import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "notes")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"user", "tags", "comments", "flames"})
@ToString(exclude = {"user", "tags", "comments", "flames"})
@EqualsAndHashCode(exclude = {"user", "tags", "comments", "flames"})
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    private Instant createdAt = Instant.now();

    private String title;

    @Column(columnDefinition = "TEXT")
    private String markdown;

    private boolean modified = false;

    private Instant modifiedAt;

    private boolean closed;

    private boolean hidden;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "note", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();
    private int commentsCount = 0;

    @OneToMany(mappedBy = "note", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Flame> flames = new HashSet<>();
    private int flamesCount = 0;

    public Note(User user, String title, String markdown, boolean closed, boolean hidden, List<Tag> tags) {
        this.user = user;
        this.title = title;
        this.markdown = markdown;
        this.closed = closed;
        this.hidden = hidden;
        if (tags != null) this.tags = tags;
    }

}