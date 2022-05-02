package com.gallery.core.service;

import java.util.List;
import java.util.Optional;

public interface IPictureService<T> {
    Optional<T> getPictureByName(String name);

    List<T> getAllPictures();

    List<T> getPicturePull(int start, int end);

    void addPictures(List<T> pictures);

    void deletePicture(T picture);
}
