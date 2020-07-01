package com.rooniks.vidbox.services;

import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.UploadException;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VideoUploadService {
    @Autowired
    VideoRepository videoRepository;

    @Async
    public void uploadVideo(Integer id) {
        Video video = videoRepository.findOneById(id);
        if(video == null) {
            throw new UploadException("Video with id: " + id + " not present in DB.");
        }
        if(!video.getStatus().equals(VideoStates.DOWNLOAD_DONE)) {
            throw new UploadException("Video not in DOWNLOAD_DONE state.");
        }

    }
}
