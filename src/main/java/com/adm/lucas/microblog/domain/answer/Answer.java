package com.adm.lucas.microblog.domain.answer;

import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "answers")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"user", "comment"})
@ToString(exclude = {"user", "comment"})
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @JoinColumn(name = "comment_id")
    @ManyToOne
    private Comment comment;

    private Instant createdAt = Instant.now();

    private String text;

    private Instant modifiedAt;

    private boolean modified = false;

    public Answer(User user, Comment comment, String text) {
        this.user = user;
        this.comment = comment;
        this.text = text;
    }

}