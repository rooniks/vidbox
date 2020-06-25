package com.rooniks.vidbox.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kiulian.downloader.YoutubeException;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.services.VideoDownloadService;
import com.rooniks.vidbox.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/")
public class VideoController {
    @Autowired
    VideoDownloadService downloader;

    @Autowired
    VideoService videoService;

    @PostMapping("video/enqueue")
    public Map<String, String> downloadVideo(@RequestBody JsonNode body) throws IOException, YoutubeException {
        return downloader.enqueueVideo(body);
    }

    @GetMapping("videos")
    public List<Video> getAllVideos() {
        return videoService.getallVideos();
    }
}
