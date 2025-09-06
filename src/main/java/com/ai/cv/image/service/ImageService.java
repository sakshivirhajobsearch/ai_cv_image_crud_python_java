package com.ai.cv.image.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ai.cv.image.model.ImageEntity;
import com.ai.cv.image.repository.ImageRepository;

@Service
public class ImageService {

	@Autowired
	private ImageRepository repo;

	public ImageEntity save(MultipartFile file) throws IOException {
		String uploadsDir = "uploads/";
		Files.createDirectories(Paths.get(uploadsDir));
		String filePath = uploadsDir + file.getOriginalFilename();
		Files.write(Paths.get(filePath), file.getBytes());

		ImageEntity entity = new ImageEntity();
		entity.setName(file.getOriginalFilename());
		entity.setPath(filePath);
		return repo.save(entity);
	}

	public List<ImageEntity> getAll() {
		return repo.findAll();
	}

	public Optional<ImageEntity> get(Long id) {
		return repo.findById(id);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	public ImageEntity process(Long id, String method) {
		ImageEntity entity = repo.findById(id).orElseThrow();
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> payload = Map.of("path", entity.getPath(), "method", method);
		ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:5001/process", payload, Map.class);
		String processedPath = (String) response.getBody().get("processed_image");
		entity.setProcessedPath(processedPath);
		return repo.save(entity);
	}
}