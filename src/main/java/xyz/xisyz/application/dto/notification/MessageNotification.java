package xyz.xisyz.application.dto.notification;

import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.flame.Flame;
import xyz.xisyz.domain.reply.Reply;
import xyz.xisyz.domain.user.User;
import lombok.SneakyThrows;

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
    private static Map<String, Object> createInfo(Type type, String target, String message) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("type", type.toString());
        info.put("target", target);
        info.put("message", message);
        return info;
    }

    private static Map<String, Object> createFollowerNotification(User follower) {
        String target = follower.getUsername();
        return createInfo(
                Type.FOLLOWER,
                target,
                String.format("@%s está seguindo você.", target)
        );
    }

    private static Map<String, Object> createFlameNotification(Flame flame) {
        String target = flame.getNote().getId().toString();
        String username = flame.getUser().getUsername();
        String title = flame.getNote().getTitle();
        return createInfo(
                Type.FLAME,
                target,
                String.format("@%s inflamou sua nota: %s", username, title)
        );
    }

    private static Map<String, Object> createCommentNotification(Comment comment) {
        String target = comment.getNote().getId().toString();
        String username = comment.getUser().getUsername();
        String title = comment.getNote().getTitle();
        return createInfo(
                Type.COMMENT,
                target,
                String.format("@%s comentou em sua nota: %s", username, title)
        );
    }

    private static Map<String, Object> createReplyNotification(Reply reply) {
        String target = reply.getComment().getNote().getId().toString();
        String username = reply.getUser().getUsername();
        String text = reply.getText();
        if (reply.getToUser() == null) {
            return createInfo(
                    Type.REPLY,
                    target,
                    String.format("@%s respondeu seu comentário: %s", username, text)
            );
        }
        return createInfo(
                Type.REPLY,
                target,
                String.format("@%s respondeu você: %s", username, text)
        );
    }

    public static MessageNotification of(User user) {
        return new MessageNotification(createFollowerNotification(user));
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