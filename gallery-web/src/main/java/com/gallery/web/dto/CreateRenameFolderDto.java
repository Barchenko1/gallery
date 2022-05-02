package com.gallery.web.dto;

import lombok.Data;

@Data
public class CreateRenameFolderDto {
    private String folderName;
    private String newFolderName;
}
