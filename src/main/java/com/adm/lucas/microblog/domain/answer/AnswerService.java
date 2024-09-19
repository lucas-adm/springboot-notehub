package com.adm.lucas.microblog.domain.answer;

import com.adm.lucas.microblog.application.dto.request.answer.CreateAnswerREQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AnswerService {

    Answer mapToAnswer(UUID idFromToken, UUID commentIdFromPath, CreateAnswerREQ req);

    Answer create(Answer answer);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    Page<Answer> getAnswers(Pageable pageable, UUID commentIdFromPath);

}