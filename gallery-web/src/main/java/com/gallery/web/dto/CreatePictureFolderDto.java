package com.gallery.web.dto;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class CreatePictureFolderDto {
    private String folderName;
    private List<File> pictureList;
}
