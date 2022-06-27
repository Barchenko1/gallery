package com.gallery.core.service;

import com.gallery.core.modal.PictureModal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IS3BucketService {

    void addPictureToFolder(PictureModal pictureModal, String folderName);
    void addFolder(String name);
    List<PictureModal> getFolderPictureList(String folderName);
    List<PictureModal> getAllPictures();
    Optional<PictureModal> getPictureByName(String name);
    void deletePictureByName(String name);
    void deleteFolderByName(String folderName);

}
