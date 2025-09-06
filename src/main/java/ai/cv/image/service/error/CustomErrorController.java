package ai.cv.image.service.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public Map<String, Object> handleError(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Object status = request.getAttribute("jakarta.servlet.error.status_code");
		Object message = request.getAttribute("jakarta.servlet.error.message");
		response.put("status", status);
		response.put("message", message != null ? message : "Unknown error");
		return response;
	}
}