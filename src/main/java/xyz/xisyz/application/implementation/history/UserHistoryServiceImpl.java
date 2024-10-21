package xyz.xisyz.application.implementation.history;

import xyz.xisyz.domain.history.UserHistoryService;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.history.UserHistory;
import xyz.xisyz.domain.history.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHistoryServiceImpl implements UserHistoryService {

    private final UserHistoryRepository repository;

    @Override
    public void setHistory(User user, String field, String oldValue, String newValue) {
        repository.save(new UserHistory(user, field, oldValue, newValue));
    }

    @Override
    public List<String> getLastFiveUserDisplayName(User user) {
        PageRequest request = PageRequest.of(0, 5);
        return repository.getLastFiveDisplayName(request, user);
    }

}