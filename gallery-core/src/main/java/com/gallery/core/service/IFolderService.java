package com.gallery.core.service;

import java.util.List;

public interface IFolderService<T> {

    T getFolderByName(String name);

    List<T> getFolderList();

    void addFolder(T folder);

    void updateFolder(T folder);

    void deleteFolder(T folder);
}
