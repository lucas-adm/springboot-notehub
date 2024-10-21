package xyz.xisyz.domain.history;

import xyz.xisyz.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserHistoryService {

    void setHistory(User user, String field, String oldValue, String newValue);

    List<String> getLastFiveUserDisplayName(User user);

}