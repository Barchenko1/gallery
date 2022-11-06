package com.gallery.web.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gallery.core.modal.PictureModal;
import com.gallery.layer.service.IS3MultipleBucketService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<PictureModal> getMonoPicture(String objectKey) {
        Mono<S3ObjectSummary> s3ObjectSummaryMono = Mono.just(s3MultipleBucketService.getS3ObjectSummary(objectKey));
        Mono<String> getMonoUrl = Mono.just(s3MultipleBucketService.getFileUrl(objectKey));

        return s3ObjectSummaryMono
                .zipWith(getMonoUrl)
                .map(tuple -> setupPictureModal(tuple.getT1().getKey(),
                                                    tuple.getT2(),
                                                    tuple.getT1().getLastModified()));

    }

    @Override
    public List<PictureModal> getPictureList(String folderPath) {
        return null;
    }

    @Override
    public Flux<PictureModal> getFluxPictureList(String folderPath) {
        return null;
    }

    @Override
    public List<PictureModal> getPictureList(String folderPath, int limit) {
        return null;
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
    public String deleteFolder(String objectKey) {
        s3MultipleBucketService.deleteFolder(objectKey);
        return String.format("objectKey %s success deleted", objectKey);
    }

    @Override
    public void copyFolderAndRemove(String folderPath, String destinationPath) {

    }

    private PictureModal setupPictureModal(String objectKey, String url, Date lastModified) {
        return PictureModal.builder()
                .tag(getTag(objectKey))
                .objectKey(objectKey)
                .url(url)
                .lastModified(lastModified)
                .build();
    }

    private String getTag(String objectKey) {
        return "";
    }

    private long getFilesSizeSum(List<MultipartFile> multipartFileList) {
        return multipartFileList.stream()
                .map(MultipartFile::getSize)
                .reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException(""));
    }

}
