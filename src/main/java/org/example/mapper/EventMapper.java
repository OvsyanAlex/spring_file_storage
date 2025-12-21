package org.example.mapper;


import org.example.dto.Event;
import org.example.model.EventEntity;
import org.example.model.FileEntity;
import org.example.model.Status;
import org.example.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event entityToEvent(EventEntity eventEntity) {
        Event event = null;
        if (eventEntity != null) {
            event = new Event(eventEntity.getId(),
                    eventEntity.getUserEntity().getId(),
                    eventEntity.getFileEntity().getId(),
                    eventEntity.getStatus(),
                    eventEntity.getTimeStamp());
        }
        return event;
    }

    public EventEntity eventToEntity(Integer userId, Integer fileId, Status status) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        EventEntity eventEntity = new EventEntity();
        eventEntity.setUserEntity(userEntity);
        eventEntity.setFileEntity(fileEntity);
        eventEntity.setStatus(status);
        return eventEntity;
    }
}
