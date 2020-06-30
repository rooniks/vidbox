package com.rooniks.vidbox.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VideoDownloadService {

    @Async
    public void downloadVideo(Integer id) {
        System.out.println("Starting the download");
        try {Thread.sleep(5000);} catch (Exception e) {}
        System.out.println("Download finished");
    }
}
