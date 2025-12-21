package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Status;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Event {
    private Integer id;
    private Integer userId;
    private Integer fileId;
    private Status status;
    private LocalDateTime timeStamp;

    public Event(Integer userId, Integer fileId, Status status) {
        this.userId = userId;
        this.fileId = fileId;
        this.status = status;
    }

    public Event(Integer id, Integer userId, Integer fileId, Status status, LocalDateTime timeStamp) {
        this.id = id;
        this.userId = userId;
        this.fileId = fileId;
        this.status = status;
        this.timeStamp = timeStamp;
    }
}
