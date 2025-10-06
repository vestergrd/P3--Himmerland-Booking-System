import { Crop } from "react-image-crop";

export const validateImage = (file: File): Promise<boolean> => {
    return new Promise((resolve) => {
      const allowedTypes = ["image/png", "image/jpeg"];
      if (!allowedTypes.includes(file.type)) {
        resolve(false);
        return;
      }
  
      const img = new Image();
      img.onload = () => {
        if (img.width >= 300 && img.height >= 300) {
          resolve(true);
        } else {
          resolve(false);
        }
      };
      img.src = URL.createObjectURL(file);
    });
  };
   
  export const getCroppedImage = (
    image: HTMLImageElement,
    crop: Crop,
    fileName: string
  ): Promise<File> => {
    return new Promise((resolve) => {
      const canvas = document.createElement("canvas");
      const scaleX = image.naturalWidth / image.width;
      const scaleY = image.naturalHeight / image.height;
      const ctx = canvas.getContext("2d");
  
      canvas.width = 300;
      canvas.height = 300;
  
      ctx?.drawImage(
        image,
        crop.x! * scaleX,
        crop.y! * scaleY,
        crop.width! * scaleX,
        crop.height! * scaleY,
        0,
        0,
        canvas.width,
        canvas.height
      );
  
      canvas.toBlob(
        (blob) => {
          if (blob) {
            resolve(new File([blob], fileName, { type: "image/jpeg" }));
          }
        },
        "image/jpeg",
        1
      );
    });
  };
  