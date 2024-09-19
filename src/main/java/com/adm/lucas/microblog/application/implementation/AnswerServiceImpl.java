package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.request.answer.CreateAnswerREQ;
import com.adm.lucas.microblog.domain.answer.Answer;
import com.adm.lucas.microblog.domain.answer.AnswerRepository;
import com.adm.lucas.microblog.domain.answer.AnswerService;
import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.comment.CommentRepository;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AnswerRepository repository;

    private void validateAccess(UUID idFromToken, Answer answer) {
        if (!Objects.equals(idFromToken, answer.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    @Override
    public Answer mapToAnswer(UUID idFromToken, UUID commentIdFromPath, CreateAnswerREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Comment comment = commentRepository.findById(commentIdFromPath).orElseThrow(EntityNotFoundException::new);
        return new Answer(user, comment, req.text());
    }

    @Override
    public Answer create(Answer answer) {
        return repository.save(answer);
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Answer answer = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, answer);
        answer.setText(text);
        answer.setModifiedAt(Instant.now());
        answer.setModified(true);
        repository.save(answer);
    }

    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        Answer answer = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, answer);
        repository.delete(answer);
    }

    @Override
    public Page<Answer> getAnswers(Pageable pageable, UUID commentIdFromPath) {
        return repository.findAllByCommentId(pageable, commentIdFromPath);
    }

}