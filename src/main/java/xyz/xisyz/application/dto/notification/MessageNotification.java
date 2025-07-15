package xyz.xisyz.application.dto.notification;

import lombok.SneakyThrows;
import xyz.xisyz.application.dto.response.user.DetailUserRES;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.flame.Flame;
import xyz.xisyz.domain.reply.Reply;
import xyz.xisyz.domain.user.User;

import java.util.LinkedHashMap;
import java.util.Map;

public record MessageNotification(
        Map<String, Object> info
) {

    private enum Type {
        FOLLOWER,
        FLAME,
        COMMENT,
        REPLY
    }

    @SneakyThrows
    private static Map<String, Object> createInfo(User from, User to, User related, Type type, String target, String message) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("from", new DetailUserRES(from));
        info.put("to", new DetailUserRES(to));
        info.put("related", new DetailUserRES(related));
        info.put("type", type.toString());
        info.put("target", target);
        info.put("message", message);
        return info;
    }

    private static Map<String, Object> createFollowerNotification(User follower, User following) {
        String target = follower.getUsername();
        return createInfo(
                follower,
                following,
                follower,
                Type.FOLLOWER,
                target,
                String.format("@%s está seguindo você.", target)
        );
    }

    private static Map<String, Object> createFlameNotification(Flame flame) {
        User from = flame.getUser();
        User to = flame.getNote().getUser();
        String target = flame.getNote().getId().toString();
        String title = flame.getNote().getTitle();
        return createInfo(
                from,
                to,
                to,
                Type.FLAME,
                target,
                String.format("@%s inflamou sua nota: %s", from.getUsername(), title)
        );
    }

    private static Map<String, Object> createCommentNotification(Comment comment) {
        User from = comment.getUser();
        User to = comment.getNote().getUser();
        String target = comment.getNote().getId().toString();
        String title = comment.getNote().getTitle();
        return createInfo(
                from,
                to,
                to,
                Type.COMMENT,
                target,
                String.format("@%s comentou em sua nota: %s", from.getUsername(), title)
        );
    }

    private static Map<String, Object> createReplyNotification(Reply reply) {
        User from = reply.getUser();
        User to = reply.getToUser() != null ? reply.getToReply().getUser() : reply.getComment().getUser();
        User related = reply.getComment().getNote().getUser();
        String target = reply.getComment().getNote().getId().toString();
        String text = reply.getText();
        if (reply.getToUser() == null) {
            return createInfo(
                    from,
                    to,
                    related,
                    Type.REPLY,
                    target,
                    String.format("@%s respondeu seu comentário: %s", from.getUsername(), text)
            );
        }
        return createInfo(
                from,
                to,
                related,
                Type.REPLY,
                target,
                String.format("@%s respondeu você: %s", from.getUsername(), text)
        );
    }

    public static MessageNotification of(User follower, User following) {
        return new MessageNotification(createFollowerNotification(follower, following));
    }

    public static MessageNotification of(Flame flame) {
        return new MessageNotification(createFlameNotification(flame));
    }

    public static MessageNotification of(Comment comment) {
        return new MessageNotification(createCommentNotification(comment));
    }

    public static MessageNotification of(Reply reply) {
        return new MessageNotification(createReplyNotification(reply));
    }

}