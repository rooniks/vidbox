package com.rooniks.vidbox.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String url;
    private String uploadUrl;
    private String title;
    private String description;
    private String filePath;
    private Long vidViews;
    private Integer lengthSec;
    private String qualityLabel;
    private String status;
    private Date scheduledTime;
    private Date downloadStartTime;
    private Date downloadCompletedTime;
    private Date uploadStartTime;
    private Date uploadCompletedTime;
    private Date cleanupTime;
    private String notes;
    private String clientRegistration;
    private String principalName;
}
