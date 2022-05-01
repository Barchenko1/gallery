package com.gallery.core.service;

import java.util.List;
import java.util.Optional;

public interface IPictureService<T> {
    Optional<T> getPictureByName(String name);

    List<T> getAllPictures();

    void addPictures(List<T> pictures);

    void updatePicture(T picture);

    void deletePicture(T picture);
}
