package com.gallery.web.controller;

import com.gallery.core.service.IPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PictureController {

    private final IPictureService pictureService;

    @Autowired
    public PictureController(IPictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(value = "/picturesRoute", method = RequestMethod.GET)
    public String getPictures(Model model) {
        model.addAttribute("pictures", pictureService.getAllPictures());
        return "pictures";
    }

}
