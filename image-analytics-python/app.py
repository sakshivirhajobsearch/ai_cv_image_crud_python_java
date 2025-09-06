# app.py
from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
from werkzeug.utils import secure_filename
import os
import json

app = Flask(__name__)
CORS(app)  # Allow all origins

UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

IMAGES_JSON = 'images.json'

# Load existing images on startup
if os.path.exists(IMAGES_JSON):
    with open(IMAGES_JSON, 'r') as f:
        images = json.load(f)
else:
    images = []

# Helper function to save images to JSON
def save_images():
    with open(IMAGES_JSON, 'w') as f:
        json.dump(images, f)

# Home route
@app.route('/')
def home():
    return "Flask Image Analytics API is running!"

# Get all images
@app.route('/api/images', methods=['GET'])
def get_images():
    result = []
    for img in images:
        img_copy = img.copy()
        img_copy["url"] = f"/uploads/{img['name']}"
        result.append(img_copy)
    return jsonify(result)

# Upload image
@app.route('/api/images', methods=['POST'])
def upload_image():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "Empty filename"}), 400

    # Sanitize filename
    filename = secure_filename(file.filename)
    filepath = os.path.join(UPLOAD_FOLDER, filename)
    file.save(filepath)

    # Debug print
    print(f"Uploaded file saved as: {filename}")

    image_record = {
        "id": len(images) + 1,
        "name": filename,
        "path": filepath,
        "processedPath": None,
        "url": f"/uploads/{filename}"
    }
    images.append(image_record)
    save_images()  # Save to JSON for persistence
    return jsonify(image_record), 201

# Get single image by ID
@app.route('/api/images/<int:image_id>', methods=['GET'])
def get_image(image_id):
    for img in images:
        if img["id"] == image_id:
            img_copy = img.copy()
            img_copy["url"] = f"/uploads/{img['name']}"
            return jsonify(img_copy)
    return jsonify({"error": "Not found"}), 404

# Delete image by ID
@app.route('/api/images/<int:image_id>', methods=['DELETE'])
def delete_image(image_id):
    global images
    images = [img for img in images if img["id"] != image_id]
    save_images()  # Save changes to JSON
    return jsonify({"message": "Deleted successfully"}), 200

# Serve uploaded images
@app.route('/uploads/<path:filename>')
def serve_image(filename):
    return send_from_directory(UPLOAD_FOLDER, filename)

# List all uploaded images as clickable links
@app.route('/uploads/')
def list_uploads():
    files = os.listdir(UPLOAD_FOLDER)
    links = [f"<a href='/uploads/{f}'>{f}</a>" for f in files]
    return "<br>".join(links) if links else "No files uploaded."

if __name__ == "__main__":
    print(f"Uploads folder: {os.path.abspath(UPLOAD_FOLDER)}")
    app.run(debug=True)
