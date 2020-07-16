package com.rooniks.vidbox.controllers;

import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.services.VideoEnqueueService;
import com.rooniks.vidbox.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UIController {
    @Autowired
    VideoService videoService;

    @Autowired
    VideoEnqueueService videoEnqueueService;

    @GetMapping("/")
    public String getAllVideos(Model model,
                               @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) {
        model.addAttribute("videos", videoService.getRecentVideos(authorizedClient));
        return "index";
    }

    @GetMapping("createvideo")
    public String createVideo(Video video) {
        return "createvideo";
    }

    @PostMapping("createvideo")
    public String downloadVideo(Video video,
                                BindingResult result,
                                Model model,
                                @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) {
        videoEnqueueService.enqueueVideoForDownload(video.getUrl(), authorizedClient);
        return "redirect:/";
    }
}
