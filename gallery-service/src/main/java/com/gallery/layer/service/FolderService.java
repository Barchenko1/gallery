package com.gallery.layer.service;

import com.gallery.core.service.IFolderService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService implements IFolderService {

    @Override
    public Optional<File> getFolderByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<File> getFolderList() {
        return null;
    }

    @Override
    public void addFolder(String folderName) {
        File theDir = new File(folderName);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
    }

    @Override
    public void updateFolder(String folderName) {

    }

    @Override
    public void deleteFolder(String folderName) {

    }
}
