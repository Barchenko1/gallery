package com.gallery.layer.service;

import com.gallery.core.modal.PictureModal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IS3BucketService {

    String uploadPicture(MultipartFile multipartFile);
    byte[] downloadPicture(String fileName);
    String deletePicture(String fileName);
    void addPictureToFolder(PictureModal pictureModal, String folderName);
    void addFolder(String name);
    List<PictureModal> getFolderPictureList(String folderName);
    List<PictureModal> getAllPictures();
    List<PictureModal> getPicturesChunk(int chunk);
    Optional<PictureModal> getPictureByName(String name);
    void deletePictureByName(String name);
    void deleteFolderByName(String folderName);

}
