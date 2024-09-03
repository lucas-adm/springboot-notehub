package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.domain.history.UserHistoryService;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.history.UserHistory;
import com.adm.lucas.microblog.domain.history.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHistoryServiceImpl implements UserHistoryService {

    private final UserHistoryRepository repository;

    @Override
    public void setHistory(User user, String field, String oldValue, String newValue) {
        repository.save(new UserHistory(user, field, oldValue, newValue));
    }

}