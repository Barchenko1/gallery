package com.gallery.web.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.core.modal.PictureModal;
import com.gallery.layer.service.IS3MultipleBucketService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("classpath:application.yaml")
public class PictureService implements IPictureService {

    private final IS3MultipleBucketService s3MultipleBucketService;

    public PictureService(IS3MultipleBucketService s3MultipleBucketService) {
        this.s3MultipleBucketService = s3MultipleBucketService;
    }

    @Override
    public String uploadPictures(String folderPath, List<MultipartFile> multipartFileList) {
        s3MultipleBucketService.setUploadFilesSize(getFilesSizeSum(multipartFileList));
        multipartFileList.forEach(multipartFile -> {
            s3MultipleBucketService.uploadMultipartFile(folderPath, multipartFile);
        });

        return String.format("files success upload to %s", folderPath);
    }

    @Override
    public String uploadPicturesAsync(String folderPath, List<MultipartFile> multipartFileList) {
        s3MultipleBucketService.setUploadFilesSize(getFilesSizeSum(multipartFileList));
        multipartFileList.forEach(multipartFile -> {
            s3MultipleBucketService.uploadMultipartFileAsync(folderPath, multipartFile);
        });

        return String.format("files success upload to %s", folderPath);
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

    @Override
    public void deleteFolder(String objectKey) {

    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {

    }

    @Override
    public void renameFolder(String folderPath, String newFolderPath) {

    }

    private PictureModal setupPictureModal(String objectKey, String url, Date lastModified) {
        return PictureModal.builder()
                .objectKey(objectKey)
                .url(url)
                .lastModified(lastModified)
                .build();
    }

    private long getFilesSizeSum(List<MultipartFile> multipartFileList) {
        return multipartFileList.stream()
                .map(MultipartFile::getSize)
                .reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException(""));
    }

}
