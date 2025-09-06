import cv2
import os

def process_image(image_path, method="grayscale"):
    img = cv2.imread(image_path)
    if method == "grayscale":
        processed = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    elif method == "edges":
        processed = cv2.Canny(img, 100, 200)
    else:
        return None

    output_path = os.path.join("processed_images", os.path.basename(image_path))
    cv2.imwrite(output_path, processed)
    return output_path
