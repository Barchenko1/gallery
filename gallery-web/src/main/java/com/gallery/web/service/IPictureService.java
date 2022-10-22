package com.gallery.web.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.core.modal.PictureModal;
import org.springframework.web.multipart.MultipartFile;

public interface IPictureService {
    String uploadPicture(MultipartFile multipartFile);
    void uploadEmptyFolder(String folderName);
    void uploadFolder(String folderName);
    void uploadPictureFolder(String folderName, String objectKey);
    PictureModal getPicture(String objectKey);
    byte[] downloadPicture(String objectKey);
    String deletePicture(String objectKey);
    void deleteFolder(String objectKey);
    void copyFolderAndRemove(String folderPath, String destinationPath);
    void renameFolder(String folderPath, String newFolderPath);
}
