package com.rooniks.vidbox.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kiulian.downloader.YoutubeException;
import com.google.api.client.json.Json;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.services.VideoEnqueueService;
import com.rooniks.vidbox.services.VideoService;
import com.rooniks.vidbox.services.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/")
public class VideoController {
    @Autowired
    VideoEnqueueService enqueueService;

    @Autowired
    VideoService videoService;

    @Autowired
    VideoUploadService videoUploadService;

    @PostMapping("video/download")
    public Map<String, String> downloadVideo(@RequestBody JsonNode body) throws IOException, YoutubeException {
        return enqueueService.enqueueVideoForDownload(body);
    }

    @GetMapping("videos")
    public List<Video> getAllVideos() {
        return videoService.getallVideos();
    }

    @PostMapping("video/upload")
    public String uploadVideo(@RequestBody JsonNode body) {
        enqueueService.enqueueVideoForUpload(body);
        return "Upload started. Please check logs.";
    }
}
