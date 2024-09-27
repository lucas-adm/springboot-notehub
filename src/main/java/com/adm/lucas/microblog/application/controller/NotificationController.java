package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.response.notification.DetailNotificationRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.notification.NotificationService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    @Transactional
    public ResponseEntity<PageRES<DetailNotificationRES>> readNotification(
            @RequestHeader("Authorization") String accessToken,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));
        PageRequest request = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        UUID idFromToken = UUID.fromString(JWT.decode(accessToken.replace("Bearer ", "")).getSubject());
        Page<DetailNotificationRES> page = service.getNotifications(request, idFromToken).map(DetailNotificationRES::new);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PageRES<>(page));
    }

}