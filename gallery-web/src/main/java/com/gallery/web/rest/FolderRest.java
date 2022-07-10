package com.gallery.web.rest;

import com.gallery.core.service.IFileStoreFolderService;
import com.gallery.web.dto.CreateRenameFolderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FolderRest {
    private final IFileStoreFolderService folderService;

    @Autowired
    public FolderRest(IFileStoreFolderService folderService) {
        this.folderService = folderService;
    }

    @RequestMapping(value = "/addFolder", method = RequestMethod.POST)
    public void getPictures(@RequestBody CreateRenameFolderDto createRenameFolderDto) {
        folderService.addFolder(createRenameFolderDto.getFolderName());
    }

    @RequestMapping(value = "/folders", method = RequestMethod.GET)
    public List<String> getPictures() {
        return folderService.getFolderList();
    }

    @RequestMapping(value = "/updateFolder", method = RequestMethod.PUT)
    public void updateFolder(@RequestBody CreateRenameFolderDto createRenameFolderDto) {
        folderService.updateFolder(createRenameFolderDto.getFolderName(),
                                   createRenameFolderDto.getNewFolderName());
    }

    @RequestMapping(value = "/deleteFolder", method = RequestMethod.DELETE)
    public void deleteFolder(@RequestBody CreateRenameFolderDto createRenameFolderDto) {
        folderService.deleteFolder(createRenameFolderDto.getFolderName());
    }
}
