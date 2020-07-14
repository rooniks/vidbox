package com.rooniks.vidbox.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.BadRequestException;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoEnqueueService {
    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoDownloadService videoDownloadService;

    @Autowired
    VideoUploadService videoUploadService;

    @Autowired
    VideoCleanupService videoCleanupService;

    public Map<String, String> enqueueVideoForDownload(String url, OAuth2AuthorizedClient authorizedClient) {
        if(url == null) {
            throw new BadRequestException("Mandatory parameter url not provided");
        }
        Video alreadyExists = videoRepository.findOneByUrl(url);
        if(alreadyExists != null) {
            throw new BadRequestException("Video with duplicate url is already in state: " + alreadyExists.getStatus());
        }
        Video video = Video.builder()
                .url(url)
                .status(VideoStates.SCHEDULED)
                .scheduledTime(new Date())
                .clientRegistration(authorizedClient.getClientRegistration().getRegistrationId())
                .principalName(authorizedClient.getPrincipalName())
                .build();
        Video savedVideo = videoRepository.save(video);
        videoDownloadService.downloadVideo(savedVideo.getId());

        Map<String, String> map = new HashMap<>();
        map.put("status", "Enqueued with id " + savedVideo.getId());

        return map;
    }

    public void enqueueVideoForUpload(JsonNode body) {
        JsonNode idField = body.get("id");
        if(idField == null) {
            throw new BadRequestException("Please supply id field in request body");
        }
        videoUploadService.uploadVideo(idField.asInt());
    }

    public void enqueueVideoForCleanup(JsonNode body) {
        JsonNode idField = body.get("id");
        if(idField == null) {
            throw new BadRequestException("Please supply id field in request body");
        }
        videoCleanupService.cleanupVideo(idField.asInt());
    }
}
