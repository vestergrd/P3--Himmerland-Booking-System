import React from "react";
import BaseImage from "../../BaseImage";
import ApiService from "../../../utils/ApiService";
import defaultImage from "../../../assets/deafultResourcePic.jpg";
import { ResourceType } from "../../../utils/EnumSupport";

interface CardImageProps {
  id: number;
  type: ResourceType;
  name: string;
}

const CardImage: React.FC<CardImageProps> = ({ id, type, name }) => {
  const fetchImage = async () => {
    try {
      const response = await ApiService.fetchResourcePic(type, id);
      return response.data;
    } catch {
      return null;
    }
  };

  return (
    <BaseImage
      fetchImage={fetchImage}
      defaultImage={defaultImage}
      altText={`${name} image`}
      imageStyle={{ width: "100%", height: "auto" }}
    />
  );
};

export default CardImage;
