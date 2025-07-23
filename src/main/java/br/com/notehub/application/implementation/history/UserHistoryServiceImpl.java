package br.com.notehub.application.implementation.history;

import br.com.notehub.domain.history.UserHistory;
import br.com.notehub.domain.history.UserHistoryRepository;
import br.com.notehub.domain.history.UserHistoryService;
import br.com.notehub.domain.user.User;
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