package org.example.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Status;

@Setter
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // для отображения snake_case в JSON
public class FileDto {
    private Integer id;
    private String fileName;
    String location;
    private Status status;

    public FileDto(String fileName, String location, Status status) {
        this.fileName = fileName;
        this.location = location;
        this.status = status;
    }

    public FileDto(Integer id, String fileName, String location, Status status) {
        this.id = id;
        this.fileName = fileName;
        this.location = location;
        this.status = status;
    }
}
