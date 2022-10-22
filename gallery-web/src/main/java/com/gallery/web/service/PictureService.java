package com.gallery.web.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.core.modal.PictureModal;
import com.gallery.layer.service.IS3MultipleBucketService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@PropertySource("classpath:application.yaml")
public class PictureService implements IPictureService {

    private final IS3MultipleBucketService s3MultipleBucketService;

    public PictureService(IS3MultipleBucketService s3MultipleBucketService) {
        this.s3MultipleBucketService = s3MultipleBucketService;
    }

    @Override
    public String uploadPicture(MultipartFile multipartFile) {
        s3MultipleBucketService.uploadFile(multipartFile);
        return String.format("file %s success upload", multipartFile.getOriginalFilename());
    }

    @Override
    public PictureModal getPicture(String objectKey) {
        S3ObjectSummary s3ObjectSummary = s3MultipleBucketService.getS3ObjectSummary(objectKey);
        String url = s3MultipleBucketService.getFileUrl(objectKey);

        return setupPictureModal(s3ObjectSummary.getKey(), url, s3ObjectSummary.getLastModified());
    }

    @Override
    public byte[] downloadPicture(String fileName) {
        return s3MultipleBucketService.downloadFile(fileName);
    }

    @Override
    public String deletePicture(String fileName) {
        s3MultipleBucketService.deleteFile(fileName);
        return String.format("file %s success deleted", fileName);
    }

    private PictureModal setupPictureModal(String name, String url, Date lastModified) {
        return PictureModal.builder()
                .name(name)
                .url(url)
                .lastModified(lastModified)
                .build();
    }

    @Override
    public void addPictureToFolder(PictureModal pictureModal, String folderName) {

    }

    @Override
    public void addFolder(String name) {

    }

    @Override
    public List<PictureModal> getFolderPictureList(String folderName) {
        return null;
    }

    @Override
    public List<PictureModal> getAllPictures() {
        return null;
    }

    @Override
    public List<PictureModal> getPicturesChunk(int chunk) {
        return null;
    }

    @Override
    public Optional<PictureModal> getPictureByName(String name) {
        return Optional.empty();
    }

    @Override
    public void deletePictureByName(String name) {

    }

    @Override
    public void deleteFolderByName(String folderName) {

    }
}
