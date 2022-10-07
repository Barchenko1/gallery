package com.gallery.core.service;

import com.gallery.core.modal.PictureModal;

import java.io.File;
import java.util.List;

public interface IPictureService {
    List<PictureModal> getPicturesByFolderName(String name);

    List<PictureModal> getAllPictures();

    List<PictureModal> getPicturePull(int start, int end);

    void addPictures(List<File> pictures);

    void deletePicture(PictureModal picture);
}
