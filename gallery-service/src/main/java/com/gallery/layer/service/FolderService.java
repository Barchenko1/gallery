package com.gallery.layer.service;

import com.gallery.core.service.IFolderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService<T> implements IFolderService<T> {

    @Override
    public T getFolderByName(String name) {
        return null;
    }

    @Override
    public List<T> getFolderList() {
        return null;
    }

    @Override
    public void addFolder(T folder) {

    }

    @Override
    public void updateFolder(T folder) {

    }

    @Override
    public void deleteFolder(T folder) {

    }
}
