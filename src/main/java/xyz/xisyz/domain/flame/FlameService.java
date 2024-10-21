package xyz.xisyz.domain.flame;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface FlameService {

    void inflame(UUID userIdFromToken, UUID noteIdFromPath);

    void deflame(UUID userIdFromToken, UUID noteIdFromPath);

    List<UUID> getUserInflamedNotes(UUID userIdFromToken);

}