package com.rooniks.vidbox.repositories;


import com.rooniks.vidbox.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    public Video findOneByUrl(String url);
    public Video findOneById(Integer id);

    @Query(nativeQuery = true, value = "select * from videos where principal_name = ?1 order by id desc limit 10")
    public List<Video> listRecentVideos(String principalName);
}
