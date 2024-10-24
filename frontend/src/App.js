import React, { useState } from "react";

const App = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [resultMessage, setResultMessage] = useState(null);
  const [uploadedImageUrl, setUploadedImageUrl] = useState(null); // For single uploaded image URL
  const [uploadedImages, setUploadedImages] = useState([]); // For list of uploaded images

  // Handle file selection
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  // Handle image upload
  const handleUpload = async () => {
    if (!selectedFile) {
      setResultMessage({ type: "error", message: "No file selected!" });
      return;
    }

    try {
      const formData = new FormData();
      formData.append("image", selectedFile);

      const response = await fetch("http://localhost:5050/api/images/uploadToGoogleDrive", {
        method: "POST",
        body: formData,
      });

      const result = await response.json();

      if (response.status === 200) {
        setUploadedImageUrl(result.url); // Store the uploaded image URL
        setResultMessage({ type: "success", message: "Image uploaded successfully!" });
      } else {
        setResultMessage({ type: "error", message: "Failed to upload image" });
      }
    } catch (error) {
      console.error("Error uploading image:", error.message);
      setResultMessage({ type: "error", message: error.message });
    }

    setTimeout(() => setResultMessage(null), 5000); // Clear the message after 5 seconds
  };

  // Fetch and display all uploaded images
  const handleShowImages = async () => {
    try {
      const response = await fetch("http://localhost:5050/api/images/getUploadedImages");
      const images = await response.json();
      setUploadedImages(images); // Store all uploaded images
    } catch (error) {
      console.error("Error fetching images:", error.message);
    }
  };

  return (
    <div className="app">
      {resultMessage && (
        <div>
          {resultMessage.type === "success"
            ? `Success: ${resultMessage.message}`
            : `Error: ${resultMessage.message}`}
        </div>
      )}

      <h1>Image Uploader</h1>

      {/* File Input for Image Upload */}
      <input type="file" accept="image/*" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={!selectedFile}>
        Upload Image
      </button>

      {/* Display the Uploaded Image */}
      {uploadedImageUrl && (
        <div>
          <h2>Uploaded Image:</h2>
          <img
            src={uploadedImageUrl}
            alt="Uploaded"
            style={{ width: "300px", marginTop: "20px" }}
          />
        </div>
      )}

      {/* Button to Show All Uploaded Images */}
      <button onClick={handleShowImages} style={{ marginTop: "20px" }}>
        Show Images
      </button>

      {/* Display All Uploaded Images */}
      {uploadedImages.length > 0 && (
        <div>
          <h2>All Uploaded Images:</h2>
          <div style={{ display: "flex", flexWrap: "wrap" }}>
            {uploadedImages.map((image, index) => (
              <img
                key={index}
                src={image}
                alt={`Uploaded ${index}`}
                style={{ width: "150px", margin: "10px" }}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default App;
