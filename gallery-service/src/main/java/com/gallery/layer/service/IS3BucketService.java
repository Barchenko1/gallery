package com.gallery.layer.service;

import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.gallery.core.modal.PictureModal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IS3BucketService {

    void uploadFile(String bucket, MultipartFile multipartFile);
    byte[] downloadFile(String bucket, String objectKey);
    String getFileUrl(String bucketName, String objectKey);
    void deleteFile(String bucket, String objectKey);
    void addFileToFolder(String bucket, String prefix, String objectKey);
    void addFolder(String bucket, String folderName);
//    List<PictureModal> getFolderPictureList(String folderName);
//    List<PictureModal> getAllPictures();
//    List<PictureModal> getPicturesChunk(int chunk);
//    Optional<PictureModal> getPictureByName(String name);
//    void deletePictureByName(String name);
//    void deleteFolderByName(String folderName);

}
