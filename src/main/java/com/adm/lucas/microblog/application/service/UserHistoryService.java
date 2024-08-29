package com.adm.lucas.microblog.application.service;

import com.adm.lucas.microblog.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserHistoryService {

    void setHistory(User user, String field, String oldValue, String newValue);

}