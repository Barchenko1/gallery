package com.gallery.web.service;

import com.gallery.core.modal.PictureModal;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IPictureService {
    String uploadPictures(String folderPath, List<MultipartFile> multipartFile);
    String uploadPicturesAsync(String folderPath, List<MultipartFile> multipartFile);
    PictureModal getPicture(String objectKey);
    Mono<PictureModal> getMonoPicture(String objectKey);
    List<PictureModal> getPictureList(String folderPath);
    Flux<PictureModal> getFluxPictureList(String folderPath);
    List<PictureModal> getPictureList(String folderPath, int limit);
    byte[] downloadPicture(String objectKey);
    String deletePicture(String objectKey);
    String deleteFolder(String objectKey);
    void copyFolderAndRemove(String folderPath, String destinationPath);
}
