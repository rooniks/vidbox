package com.rooniks.vidbox.controllers;

import com.rooniks.vidbox.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
    @Autowired
    VideoService videoService;

    @GetMapping("/")
    public String getAllVideos(Model model) {
        model.addAttribute("videos", videoService.getRecentVideos());
        return "index";
    }
}
