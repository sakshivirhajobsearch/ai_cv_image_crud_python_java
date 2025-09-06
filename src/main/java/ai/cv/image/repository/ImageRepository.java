package ai.cv.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ai.cv.image.model.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}