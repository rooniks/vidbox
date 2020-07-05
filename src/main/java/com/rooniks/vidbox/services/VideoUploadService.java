package com.rooniks.vidbox.services;

// Upload code imported from https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/data/UploadVideo.java

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;
import com.rooniks.vidbox.constants.VideoStates;
import com.rooniks.vidbox.entities.Video;
import com.rooniks.vidbox.exceptions.UploadException;
import com.rooniks.vidbox.repositories.VideoRepository;
import com.rooniks.vidbox.utils.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class VideoUploadService {
    @Autowired
    VideoRepository videoRepository;

    private static YouTube youtube;
    private static final String VIDEO_FILE_FORMAT = "video/*";
    private static final Logger logger = LoggerFactory.getLogger(VideoUploadService.class);

    @Async
    public void uploadVideo(Integer id) {
        Video video = videoRepository.findOneById(id);
        if(video == null) {
            throw new UploadException("Video with id: " + id + " not present in DB.");
        }
        if(!video.getStatus().equals(VideoStates.DOWNLOAD_DONE)) {
            throw new UploadException("Video not in DOWNLOAD_DONE state.");
        }
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");

        try {
            Credential credential = Auth.authorize(scopes, "uploadvideo");
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                    "vidbox").build();

            logger.info("Uploading: {} from location: {}", video.getTitle(), video.getFilePath());

            com.google.api.services.youtube.model.Video videoObjectDefiningMetadata =
                    new com.google.api.services.youtube.model.Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private");
            videoObjectDefiningMetadata.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(video.getTitle());
            snippet.setDescription(video.getDescription());
            videoObjectDefiningMetadata.setSnippet(snippet);

            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
                    new FileInputStream(new File(video.getFilePath())));
            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,status", videoObjectDefiningMetadata, mediaContent);

            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                @Override
                public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
                    logger.info("State: " + uploader.getUploadState());
                    if (uploader.getUploadState() == MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS) {
                        logger.info("Bytes uploaded: " + uploader.getNumBytesUploaded());
                    }
                }
            };
            uploader.setProgressListener(progressListener);

            logger.info("Setting the upload start time of the video.");
            video.setUploadStartTime(new Date());
            video.setStatus(VideoStates.UPLOAD_STARTED);
            videoRepository.save(video);

            // The loc that uploads!
            com.google.api.services.youtube.model.Video returnedVideo = videoInsert.execute();

            logger.info("Upload successful. ID: {}", returnedVideo.getId());
            video.setUploadUrl(returnedVideo.getId());
            video.setUploadCompletedTime(new Date());
            video.setStatus(VideoStates.UPLOAD_DONE);
            videoRepository.save(video);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
