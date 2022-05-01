package com.init.core.dao;

import java.util.List;
import java.util.Optional;

public interface IPictureDao<T> {

    Optional<T> getPictureByName(String name);

    List<T> getAllPictures();




}
