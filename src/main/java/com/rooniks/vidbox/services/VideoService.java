package com.rooniks.vidbox.services;

import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    VideoRepository videoRepository;

    public List<Video> getRecentVideos() {
        return videoRepository.listRecentVideos();
    }
}
