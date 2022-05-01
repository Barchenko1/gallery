package com.gallery.core.dao;

import java.util.List;
import java.util.Optional;

public interface IPictureDao<T> {

    Optional<T> getPictureByName(String name);

    List<T> getAllPictures();

    void addPicture(T picture);

    void updatePicture(T picture);

    void deletePicture(T picture);
}
