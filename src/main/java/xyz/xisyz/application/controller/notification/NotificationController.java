package xyz.xisyz.application.controller.notification;

import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.xisyz.application.dto.response.notification.DetailNotificationRES;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.domain.notification.NotificationService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://xisyz.xyz"})
@RequestMapping("/api/v1/notifications")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Notification Controller", description = "Endpoints for managing user notifications, including retrieving and marking notifications as read.")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @Operation(
            summary = "Retrieve notifications",
            description = "Fetch a paginated list of user notifications, ordered by unread notifications first and sorted by creation date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Notifications retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping
    @Transactional
    public ResponseEntity<PageRES<DetailNotificationRES>> readNotification(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));
        PageRequest request = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        UUID idFromToken = UUID.fromString(JWT.decode(accessToken.replace("Bearer ", "")).getSubject());
        Page<DetailNotificationRES> page = service.getNotifications(request, idFromToken).map(DetailNotificationRES::new);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PageRES<>(page));
    }

}