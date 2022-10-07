package com.gallery.core.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface IFileStoreFolderService {
    Optional<File> getFolderByName(String name);

    List<String> getFolderList();

    void addFolder(String folderName);

    void updateFolder(String folderName, String newFolderName);

    void deleteFolder(String folderName);
}
