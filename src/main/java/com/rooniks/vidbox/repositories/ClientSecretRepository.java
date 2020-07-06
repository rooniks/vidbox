package com.rooniks.vidbox.repositories;


import com.rooniks.vidbox.entities.ClientSecret;
import com.rooniks.vidbox.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSecretRepository extends JpaRepository<ClientSecret, Integer> {
    public ClientSecret findOneByProject(String project);
}
