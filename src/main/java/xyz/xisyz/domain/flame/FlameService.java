package xyz.xisyz.domain.flame;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface FlameService {

    Flame inflame(UUID userIdFromToken, UUID noteIdFromPath);

    void deflame(UUID userIdFromToken, UUID noteIdFromPath);

    Page<Flame> getUserFlames(UUID userIdFromToken, Pageable pageable, String username, String q);

}