package org.example.mapper;

import org.example.dto.FileDto;
import org.example.model.FileEntity;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {
    public FileEntity fileToEntity(FileDto fileDto) {
        return new FileEntity(fileDto.getId(), fileDto.getFileName(), fileDto.getLocation(), fileDto.getStatus());
    }

    public FileDto entityToFile(FileEntity fileEntity) {
        return new FileDto(fileEntity.getId(), fileEntity.getFileName(), fileEntity.getLocation(), fileEntity.getStatus());
    }

}
