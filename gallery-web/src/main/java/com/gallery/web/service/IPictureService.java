package com.gallery.web.service;

import com.gallery.core.modal.PictureModal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPictureService {
    String uploadPictures(String folderPath, List<MultipartFile> multipartFile);
    String uploadPicturesAsync(String folderPath, List<MultipartFile> multipartFile);
    PictureModal getPicture(String objectKey);
    byte[] downloadPicture(String objectKey);
    String deletePicture(String objectKey);
    void deleteFolder(String objectKey);
    void copyFolderAndRemove(String folderPath, String destinationPath);
    void renameFolder(String folderPath, String newFolderPath);
}
