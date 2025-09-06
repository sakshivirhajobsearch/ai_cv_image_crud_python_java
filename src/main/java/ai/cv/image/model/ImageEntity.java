package ai.cv.image.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class ImageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String path;
	private String processedPath;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getProcessedPath() {
		return processedPath;
	}

	public void setProcessedPath(String processedPath) {
		this.processedPath = processedPath;
	}
}