package xyz.xisyz.application.implementation.note;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.xisyz.application.dto.request.note.CreateNoteREQ;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.note.NoteRepository;
import xyz.xisyz.domain.note.NoteService;
import xyz.xisyz.domain.tag.Tag;
import xyz.xisyz.domain.tag.TagRepository;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final NoteRepository repository;

    private void validateAccess(UUID idFromToken, Note note) {
        if (!Objects.equals(idFromToken, note.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    private void validateUser(UUID idFromToken, UUID idFromRequested) {
        if (!Objects.equals(idFromToken, idFromRequested)) {
            throw new AccessDeniedException("Usuário sem permissão");
        }
    }

    private void validateBidirectionalFollowAccess(User requesting, User requested) {
        if (!Objects.equals(requesting.getUsername(), requested.getUsername())
                && !requested.getFollowing().contains(requesting)
                && !requesting.getFollowers().contains(requested)
        ) {
            throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        }
    }

    private void deleteNoteAndFlush(Note note) {
        repository.delete(note);
        repository.flush();
    }

    private void removeOrphanTags(List<String> names) {
        List<Tag> tags = tagRepository.findAllByNameIn(names);
        tags.stream().filter(tag -> tag.getNotes().isEmpty()).forEach(tagRepository::delete);
    }

    private List<Tag> findOrCreateTags(List<String> tags) {
        if (tags == null) return null;
        return tags.stream()
                .map(String::toLowerCase).distinct()
                .map(tag -> tagRepository.findByName(tag).orElseGet(() -> new Tag(tag.toLowerCase())))
                .toList();
    }

    private void changeField(UUID idFromToken, UUID idFromPath, Consumer<Note> setter) {
        Note note = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, note);
        setter.accept(note);
        note.setModifiedAt(Instant.now());
        note.setModified(true);
        repository.saveAndFlush(note);
    }

    @Override
    public Note mapToNote(UUID idFromToken, CreateNoteREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        List<Tag> tags = findOrCreateTags(req.tags());
        return new Note(user, req.title(), req.description(), req.markdown(), req.closed(), req.hidden(), tags);
    }

    @Override
    public Note create(Note note) {
        return repository.save(note);
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String title, String description, List<String> tags, boolean closed, boolean hidden) {
        changeField(idFromToken, idFromPath, note -> {
            List<String> oldTags = note.getTags().stream().map(Tag::getName).toList();
            note.setTitle(title);
            note.setDescription(description);
            note.setTags(findOrCreateTags(tags));
            note.setClosed(closed);
            note.setHidden(hidden);
            removeOrphanTags(oldTags);
        });
    }

    @Override
    public void changeTitle(UUID idFromToken, UUID idFromPath, String title) {
        changeField(idFromToken, idFromPath, note -> note.setTitle(title));
    }

    @Override
    public void changeDescription(UUID idFromToken, UUID idFromPath, String description) {
        changeField(idFromToken, idFromPath, note -> note.setDescription(description));
    }

    @Override
    public void changeMarkdown(UUID idFromToken, UUID idFromPath, String markdown) {
        changeField(idFromToken, idFromPath, note -> note.setMarkdown(markdown));
    }

    @Override
    public void changeClosed(UUID idFromToken, UUID idFromPath) {
        changeField(idFromToken, idFromPath, note -> note.setClosed(!note.isClosed()));
    }

    @Override
    public void changeHidden(UUID idFromToken, UUID idFromPath) {
        changeField(idFromToken, idFromPath, note -> note.setHidden(!note.isHidden()));
    }

    @Override
    public void changeTags(UUID idFromToken, UUID idFromPath, List<String> tags) {
        changeField(idFromToken, idFromPath, note -> {
            List<String> oldTagNames = note.getTags().stream().map(Tag::getName).toList();
            note.setTags(findOrCreateTags(tags));
            removeOrphanTags(oldTagNames);
        });
    }

    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        Note note = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, note);
        List<String> oldTagNames = note.getTags().stream().map(Tag::getName).toList();
        deleteNoteAndFlush(note);
        removeOrphanTags(oldTagNames);
    }

    @Override
    public List<String> getAllTags() {
        return tagRepository.findAll().stream().map(Tag::getName).toList();
    }

    @Override
    public List<String> getAllPublicUserTags(UUID idFromToken, String username) {
        User requesting = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        User requested = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if (requested.isProfilePrivate()) validateBidirectionalFollowAccess(requesting, requested);
        return tagRepository.findAllByNotesUserUsernameAndNotesHiddenFalseOrderByNameAsc(username).stream().map(Tag::getName).toList();
    }

    @Override
    public List<String> getAllPrivateUserTags(UUID idFromToken) {
        return tagRepository.findAllByNotesUserId(idFromToken).stream().map(Tag::getName).toList();
    }

    @Override
    public Page<Note> findPublicNotes(Pageable pageable, String q) {
        return repository.searchPublicNotesByTitleOrTag(pageable, q);
    }

    @Override
    public Page<Note> findPrivateNotes(Pageable pageable, UUID idFromToken, String q) {
        return repository.searchPrivateNotesByTitleOrTag(pageable, idFromToken, q);
    }

    @Override
    public Page<Note> findPublicNotesByTag(Pageable pageable, String tag) {
        return repository.searchPublicNotesByTag(pageable, tag);
    }

    @Override
    public Page<Note> findPrivateNotesByTag(Pageable pageable, UUID idFromToken, String tag) {
        return repository.searchPrivateNotesByTag(pageable, idFromToken, tag);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Note> findUserNotesBySpecs(UUID idFromToken, Pageable pageable, String username, String q, String tag, String type) {
        User requesting = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        User requested = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if (requested.isProfilePrivate()) validateBidirectionalFollowAccess(requesting, requested);
        if (Objects.equals(type, "hidden")) validateUser(idFromToken, requested.getId());
        return repository.searchUserNotesBySpecs(pageable, username, q, tag, type);
    }

    @Override
    public Note getPublicNote(UUID idFromPath) {
        return repository.findByIdAndHiddenFalseAndUserProfilePrivateFalse(idFromPath).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Note getPrivateNote(UUID idFromToken, UUID idFromPath) {
        Note note = repository.findByIdWithUserAndTags(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, note);
        return note;
    }

    @Override
    public Note getPrivateFollowingUserNote(UUID idFromToken, UUID idFromPath) {
        Note note = repository.findByIdAndHiddenFalseWithUserAndTags(idFromPath).orElseThrow(EntityNotFoundException::new);
        User requesting = userRepository.findByIdWithFollowersAndFollowing(idFromToken).orElseThrow(EntityNotFoundException::new);
        User requested = userRepository.findByIdWithFollowersAndFollowing(note.getUser().getId()).orElseThrow(EntityNotFoundException::new);
        validateBidirectionalFollowAccess(requesting, requested);
        return note;
    }

    @Override
    public Page<Note> getAllUserNotesByUsername(Pageable pageable, String username) {
        return repository.findAllByUserProfilePrivateFalseAndUserUsernameAndHiddenFalse(pageable, username.toLowerCase());
    }

    @Override
    public Page<Note> getAllUserNotesById(Pageable pageable, UUID idFromToken) {
        return repository.findAllByUserId(pageable, idFromToken);
    }

    @Override
    public Page<Note> getAllFollowedUsersNotes(Pageable pageable, UUID idFromToken) {
        User user = userRepository.findByIdWithFollowersAndFollowing(idFromToken).orElseThrow(EntityNotFoundException::new);
        Set<UUID> following = user.getFollowing().stream().map(User::getId).collect(Collectors.toSet());
        Set<UUID> followers = user.getFollowers().stream().map(User::getId).collect(Collectors.toSet());
        Set<UUID> mutuals = new HashSet<>(following);
        mutuals.retainAll(followers);
        return repository.findAllForUserFeed(pageable, following, mutuals);
    }

    @Override
    public Page<Note> getAllFollowedUserNotes(Pageable pageable, UUID idFromToken, String username) {
        User requesting = userRepository.findByIdWithFollowersAndFollowing(idFromToken).orElseThrow(EntityNotFoundException::new);
        User requested = userRepository.findByUsernameWithFollowersAndFollowing(username).orElseThrow(EntityNotFoundException::new);
        validateBidirectionalFollowAccess(requesting, requested);
        return repository.findAllByUserUsernameAndHiddenFalse(pageable, username);
    }

}