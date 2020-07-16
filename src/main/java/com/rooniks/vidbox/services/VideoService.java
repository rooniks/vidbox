package com.rooniks.vidbox.services;

import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    VideoRepository videoRepository;

    public List<Video> getRecentVideos(OAuth2AuthorizedClient authorizedClient) {
        return videoRepository.listRecentVideos(authorizedClient.getPrincipalName());
    }
}
