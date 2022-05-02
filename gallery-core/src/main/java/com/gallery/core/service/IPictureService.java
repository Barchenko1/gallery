package com.gallery.core.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface IPictureService {
    List<File> getPicturesByFolderName(String name);

    List<File> getAllPictures();

    List<File> getPicturePull(int start, int end);

    void addPictures(List<File> pictures);

    void deletePicture(File picture);
}
