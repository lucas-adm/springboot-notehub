package com.adm.lucas.microblog.domain.history;

import com.adm.lucas.microblog.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserHistoryService {

    void setHistory(User user, String field, String oldValue, String newValue);

    List<String> getLastFiveUserDisplayName(User user);

}