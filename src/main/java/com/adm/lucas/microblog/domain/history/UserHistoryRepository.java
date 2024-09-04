package com.adm.lucas.microblog.domain.history;

import com.adm.lucas.microblog.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {

    @Query("SELECT uh.oldValue FROM UserHistory uh WHERE uh.field = 'display_name' AND uh.user = :user ORDER BY uh.dateTime DESC")
    List<String> getLastFiveDisplayName(Pageable pageable, @Param("user") User user);

}