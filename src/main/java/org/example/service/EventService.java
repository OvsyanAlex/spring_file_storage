package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.Event;
import org.example.mapper.EventMapper;
import org.example.model.Status;
import org.example.repository.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final FileService fileService;
    private final EventMapper eventMapper;

    public Mono<Event> save(Integer userId, Integer fileId, Status status) {
        return Mono.fromCallable(() ->
                        eventRepository.save(eventMapper.eventToEntity(userId, fileId, status)))
                .map(eventMapper::entityToEvent)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
