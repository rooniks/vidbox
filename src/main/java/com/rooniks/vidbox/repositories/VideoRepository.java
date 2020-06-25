package com.rooniks.vidbox.repositories;


import com.rooniks.vidbox.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {

}
