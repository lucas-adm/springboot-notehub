package com.adm.lucas.microblog.application.service.impl;

import com.adm.lucas.microblog.application.service.UserHistoryService;
import com.adm.lucas.microblog.domain.model.User;
import com.adm.lucas.microblog.domain.model.UserHistory;
import com.adm.lucas.microblog.domain.repository.UserHistoryRepository;
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