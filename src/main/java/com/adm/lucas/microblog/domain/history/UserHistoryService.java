package com.adm.lucas.microblog.domain.history;

import com.adm.lucas.microblog.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public interface UserHistoryService {

    void setHistory(User user, String field, String oldValue, String newValue);

}