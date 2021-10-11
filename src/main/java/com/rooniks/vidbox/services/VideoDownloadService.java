package com.rooniks.vidbox.services;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.DownloadException;
import com.rooniks.vidbox.repositories.VideoRepository;
import java.io.File;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


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

        VideoInfo youtubeVideo = null;

        Response<VideoInfo> videoInfoResponse = downloader.getVideoInfo(new RequestVideoInfo(videoUrl));
        youtubeVideo = videoInfoResponse.data();

//            video.setNotes(ex.getMessage());
//            video.setStatus(VideoStates.ABORTED);
//            videoRepository.save(video);
//            ex.printStackTrace();
//            throw new DownloadException(ex.getMessage());


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
        VideoFormat selectedFormat;

        selectedFormat = youtubeVideo.bestVideoWithAudioFormat();
        RequestVideoFileDownload request = new RequestVideoFileDownload(selectedFormat).saveTo(outDir);
        Response<File> response = downloader.downloadVideoFile(request);
        File data = response.data();

//            video.setNotes(ex.getMessage());
//            video.setStatus(VideoStates.ABORTED);
//            videoRepository.save(video);
//            throw new DownloadException(ex.getMessage());

        video.setFilePath(data.getAbsolutePath());
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
