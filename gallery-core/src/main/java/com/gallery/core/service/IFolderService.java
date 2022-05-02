package com.gallery.core.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface IFolderService {
    Optional<File> getFolderByName(String name);

    List<File> getFolderList();

    void addFolder(String folderName);

    void updateFolder(String folderName);

    void deleteFolder(String folderName);
}
