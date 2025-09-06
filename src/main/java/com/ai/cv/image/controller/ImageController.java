package com.ai.cv.image.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*") // allow all origins for testing
public class ImageController {

	private static final String UPLOAD_DIR = "uploads";
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	// In-memory storage for image metadata
	private final Map<Integer, Map<String, Object>> imageStore = new HashMap<>();

	public ImageController() {
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
	}

	// ---------------- Upload Image ----------------
	@PostMapping
	public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		if (file.isEmpty()) {
			response.put("error", "No file selected");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			int id = ID_GENERATOR.getAndIncrement();
			String filename = file.getOriginalFilename();
			String path = UPLOAD_DIR + "/" + filename;

			File dest = new File(path);
			file.transferTo(dest);

			Map<String, Object> imageData = new HashMap<>();
			imageData.put("id", id);
			imageData.put("name", filename);
			imageData.put("path", "/" + path);

			imageStore.put(id, imageData);

			return ResponseEntity.ok(imageData);

		} catch (IOException e) {
			response.put("error", "Failed to upload image: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// ---------------- Get All Images ----------------
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllImages() {
		List<Map<String, Object>> images = new ArrayList<>(imageStore.values());
		return ResponseEntity.ok(images);
	}

	// ---------------- Delete Image ----------------
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable int id) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> removed = imageStore.remove(id);

		if (removed != null) {
			String path = (String) removed.get("path");
			File file = new File("." + path); // "./uploads/filename"
			if (file.exists())
				file.delete();

			response.put("message", "Image deleted successfully");
			return ResponseEntity.ok(response);
		} else {
			response.put("error", "Image not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}
