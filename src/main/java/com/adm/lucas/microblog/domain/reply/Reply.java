package com.adm.lucas.microblog.domain.reply;

import com.adm.lucas.microblog.domain.comment.Comment;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "replies")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"user", "comment", "toReply"})
@ToString(exclude = {"user", "comment", "toReply"})
@EqualsAndHashCode(exclude = {"replies"})
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "toReply")
    private List<Reply> replies = new ArrayList<>();

    @JoinColumn(name = "user_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @JoinColumn(name = "comment_id")
    @ManyToOne
    private Comment comment;

    @JoinColumn(name = "reply_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Reply toReply;

    private String toUser;

    private Instant createdAt = Instant.now();

    private String text;

    private Instant modifiedAt;

    private boolean modified = false;

    public Reply(User user, Comment comment, String text) {
        this.user = user;
        this.comment = comment;
        this.text = text;
    }

    public Reply(User user, Comment comment, Reply reply, String text) {
        this.user = user;
        this.comment = comment;
        this.toReply = reply;
        this.toUser = reply.getUser().getDisplayName();
        this.text = text;
    }

}