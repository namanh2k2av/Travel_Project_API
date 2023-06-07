package com.example.travel.services.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.travel.daos.*;
import com.example.travel.exceptions.NotFoundException;
import com.example.travel.repositories.*;
import com.example.travel.services.IVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VideoServiceImp implements IVideoService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<Video> getAllVideo() {
        return videoRepository.findAll();
    }

    @Override
    public List<Video> getVideoById(Long id) {
        Optional<Video> videos = videoRepository.findById(id);
        if (videos.isEmpty()) {
            throw new NotFoundException("Video not found");
        }
        return (List<Video>) videos.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Video> createVideo(Long locationId, MultipartFile[] files) throws IOException {
        List<Video> videos = new ArrayList<>();
        Optional<Location> locations = locationRepository.findById(locationId);
        if (locations.isEmpty()) {
            throw new NotFoundException("Location not found");
        }
        Location location = locations.get();
        for (int i = 0; i < files.length; i++) {
            Map<?, ?> cloudinaryMap = cloudinary.uploader().upload(files[i].getBytes(), ObjectUtils.emptyMap());
            Video video = new Video();
            video.setLocation(location);
            video.setLinkVideo(cloudinaryMap.get("secure_url").toString());
            video.setPublicId(cloudinaryMap.get("public_id").toString());
            Video newVideo = videoRepository.save(video);
            videos.add(newVideo);
        }
        return videos;
    }

    @Override
    public Video editVideo(Long videoId, MultipartFile[] files) {
        return null;
    }

    @Override
    public void deleteVideo(Long videoId) {
        Optional<Video> videos = videoRepository.findById(videoId);
        if (videos.isEmpty()) {
            throw new NotFoundException("Video not found");
        }
        videoRepository.deleteById(videoId);
    }
}
