package com.adm.lucas.microblog.application.dto.notification;

import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Map;

public record MessageNotification(
        String info
) {

    @SneakyThrows
    private static String createFollowerNotification(User fromUser) {
        String target = fromUser.getUsername();
        Map<String, String> data = Map.of(
                "target", target,
                "message", String.format("@%s começou a te seguir.", target)
        );
        return new ObjectMapper().writeValueAsString(data);
    }

    public static MessageNotification of(User fromUser) {
        return new MessageNotification(createFollowerNotification(fromUser));
    }

}