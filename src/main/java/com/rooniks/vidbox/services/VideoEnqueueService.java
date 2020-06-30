package com.rooniks.vidbox.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.BadRequestException;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VideoEnqueueService {
    @Autowired
    VideoRepository videoRepository;

    public Map<String, String> enqueueVideo(JsonNode body) {
        JsonNode jsonUrl = body.get("url");
        if(jsonUrl == null) {
            throw new BadRequestException("Mandatory parameter url not provided");
        }
        Video video = Video.builder().url(jsonUrl.asText()).status(VideoStates.SCHEDULED).build();
        Video savedVideo = videoRepository.save(video);

        Map<String, String> map = new HashMap<>();
        map.put("status", "Enqueued with id " + savedVideo.getId());
        return map;
    }
}
