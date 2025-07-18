package xyz.xisyz.domain.flame;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.response.flame.DetailFlameRES;
import xyz.xisyz.application.dto.response.page.PageRES;

import java.util.UUID;

@Service
public interface FlameService {

    DetailFlameRES inflame(UUID userIdFromToken, UUID noteIdFromPath);

    void deflame(UUID userIdFromToken, UUID noteIdFromPath);

    PageRES<DetailFlameRES> getUserFlames(UUID userIdFromToken, Pageable pageable, String username, String q);

}