import { ResourceType } from "../../utils/EnumSupport";


export default interface Resource {
    id: number;
    type: ResourceType;
    name: string;
    img: string;
    description: string;
    status: string;
    capacity: number;
  }