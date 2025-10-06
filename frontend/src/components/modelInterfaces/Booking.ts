import { ResourceType } from "../../utils/EnumSupport";
import { TimeRange } from "./TimeRange";

export default interface Booking {
  id: number;
  resourceID: number;
  resourceType: ResourceType;
  resourceName: string;
  startDate: Date | null;
  endDate: Date | null;
  pickupTime: TimeRange;
  dropoffTime: TimeRange;
  status: string | null;
}
