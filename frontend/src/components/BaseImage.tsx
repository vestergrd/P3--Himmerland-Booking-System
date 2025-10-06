import React, { useState, useEffect } from "react";
import ApiService from "../utils/ApiService";

interface DefaultImageProps {
  fetchImage: () => Promise<Blob | null>;
  defaultImage: string;
  altText: string;
  imageStyle?: React.CSSProperties;
}

const BaseImage: React.FC<DefaultImageProps> = ({
  fetchImage,
  defaultImage,
  altText,
  imageStyle = {},
}) => {
  const [imageSrc, setImageSrc] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadImage = async () => {
      setIsLoading(true);
      try {
        const imageBlob = await fetchImage();
        if (imageBlob) {
          const imageUrl = URL.createObjectURL(imageBlob);
          setImageSrc(imageUrl);
        } else {
          setImageSrc(defaultImage);
        }
      } catch (err: any) {
        console.error("Error loading image:", err);
        setError("Failed to load image.");
        setImageSrc(defaultImage);
      } finally {
        setIsLoading(false);
      }
    };

    loadImage();
  }, [fetchImage, defaultImage]);

  return (
    <div>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {isLoading ? (
        <p>Loading...</p>
      ) : (
        <img
          src={imageSrc || defaultImage}
          alt={altText}
          style={imageStyle}
        />
      )}
    </div>
  );
};

export default BaseImage;
