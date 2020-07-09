package com.rooniks.vidbox.services;

import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.CleanupException;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Service
public class VideoCleanupService {
    @Autowired
    VideoRepository videoRepository;

    @Async
    public void cleanupVideo(Integer id) {
        Video video = videoRepository.findOneById(id);
        if(video == null) {
            throw new CleanupException("Video with id: " + id + " not present in DB.");
        }
        if(!video.getStatus().equals(VideoStates.UPLOAD_DONE)) {
            throw new CleanupException("Video not in DOWNLOAD_DONE state.");
        }
        File videoFile = new File(video.getFilePath());
        if(videoFile.delete()) {
            video.setStatus(VideoStates.CLEANED);
            video.setCleanupTime(new Date());
        } else {
            video.setNotes("Couldn't delete the downloaded file.");
        }
        videoRepository.save(video);
    }
}