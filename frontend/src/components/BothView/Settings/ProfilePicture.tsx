import React from "react";
import BaseImage from "../../BaseImage";
import ApiService from "../../../utils/ApiService";
import defaultImage from "../../../assets/defaultProfilePic.jpg";

interface ProfilePictureProps {
  imageSource: string;
}

const ProfilePicture: React.FC<ProfilePictureProps> = ({ imageSource }) => {
  const fetchImage = async () => {
    try {
      const response = await ApiService.fetchImage(imageSource);
      return response.data;
    } catch {
      return null;
    }
  };

  return (
    <BaseImage
      fetchImage={fetchImage}
      defaultImage={defaultImage}
      altText="Profile picture"
      imageStyle={{ borderRadius: "50%", width: "150px", height: "150px" }}
    />
  );
};

export default ProfilePicture;
