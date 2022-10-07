package com.gallery.layer.service;

import com.gallery.core.service.IFileStoreFolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class FileStoreFolderService implements IFileStoreFolderService {

    @Value("${file.store.path}")
    private String fileStorePath;

    @Override
    public Optional<File> getFolderByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<String> getFolderList() {
        File fileStore = new File(fileStorePath);
        File[] files = fileStore.listFiles();
        if (files == null) {
            throw new RuntimeException();
        }
        return Arrays.stream(files)
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void addFolder(String folderName) {
        Path path = Paths.get(fileStorePath + "/" + folderName);
        if (!Files.exists(path)) {
            try {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
                FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
                Files.createDirectories(path, fileAttributes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("folder is already exist");
        }
    }

    @Override
    public void updateFolder(String folderName, String newFolderName) {
        Path source = Paths.get(fileStorePath + "/" + folderName);
        try {
            Files.move(source,
                       source.resolveSibling(fileStorePath + "/" + newFolderName),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFolder(String folderName) {
        Path path = Paths.get(fileStorePath + "/" + folderName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
