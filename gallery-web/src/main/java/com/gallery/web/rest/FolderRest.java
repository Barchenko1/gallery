package com.gallery.web.rest;

import com.gallery.core.service.IFolderService;
import com.gallery.web.dto.CreateRenameFolderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FolderRest {
    private IFolderService folderService;

    @Autowired
    public FolderRest(IFolderService folderService) {
        this.folderService = folderService;
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public void getPictures(@RequestBody CreateRenameFolderDto createRenameFolderDto) {
        folderService.addFolder(createRenameFolderDto.getFolderName());
    }
}
