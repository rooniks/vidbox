package com.rooniks.vidbox.services;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.DownloadException;
import com.rooniks.vidbox.exceptions.NotSupportedException;
import com.rooniks.vidbox.repositories.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Service
public class VideoDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(VideoDownloadService.class);

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoUploadService videoUploadService;

    @Value("${fileDownloadPath}")
    String fileDownloadPath;

    @Async
    public void downloadVideo(Integer id) {
        Video video = videoRepository.findOneById(id);
        if(video == null) {
            throw new DownloadException("Video with id: " + id + " not present in DB.");
        }
        if(!video.getStatus().equals(VideoStates.SCHEDULED)) {
            throw new DownloadException("Video not in SCHEDULED state.");
        }

        YoutubeDownloader downloader = new YoutubeDownloader();
        String videoUrl = video.getUrl();
        YoutubeVideo youtubeVideo = null;
        try {
            downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            downloader.setParserRetryOnFailure(1);
            youtubeVideo = downloader.getVideo(videoUrl);
        } catch (YoutubeException | IOException ex) {
            video.setNotes(ex.getMessage());
            video.setStatus(VideoStates.ABORTED);
            videoRepository.save(video);
            ex.printStackTrace();
            throw new DownloadException(ex.getMessage());
        }

        logger.info("Starting download of video with id " + video.getId());
        video.setStatus(VideoStates.DOWNLOAD_STARTED);
        video.setVidViews(youtubeVideo.details().viewCount());
        video.setTitle(youtubeVideo.details().title());
        video.setLengthSec(youtubeVideo.details().lengthSeconds());
        video.setDescription("Author: " + youtubeVideo.details().author()
                + "\nKeywords: " + youtubeVideo.details().keywords()
                + "\nAverage rating: " + youtubeVideo.details().averageRating()
                + "\nDescription: " + youtubeVideo.details().description());
        video.setDownloadStartTime(new Date());
        videoRepository.save(video);

        File outDir = new File(fileDownloadPath);
        File downloadedFile;
        AudioVideoFormat selectedFormat;
        try {
            List<AudioVideoFormat> formats = youtubeVideo.videoWithAudioFormats();
            if(formats.size() == 0) {
                throw new NotSupportedException("Unsupported type. No AudioVideo format detected in the source video.");
            }
            selectedFormat = formats.get(formats.size() - 1);
            downloadedFile = youtubeVideo.download(selectedFormat, outDir);
        } catch (IOException | YoutubeException | NotSupportedException ex ) {
            video.setNotes(ex.getMessage());
            video.setStatus(VideoStates.ABORTED);
            videoRepository.save(video);
            throw new DownloadException(ex.getMessage());
        }
        video.setFilePath(downloadedFile.getAbsolutePath());
        video.setDownloadCompletedTime(new Date());
        video.setQualityLabel(selectedFormat.qualityLabel());
        video.setStatus(VideoStates.DOWNLOAD_DONE);
        videoRepository.save(video);

        logger.info("Download completed for video with id " + video.getId());
        videoUploadService.uploadVideo(video.getId());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
