import React, { useEffect, useState } from "react";
import Booking from "../../modelInterfaces/Booking";
import { TimeRange } from "../../modelInterfaces/TimeRange";
import ApiService from "../../../utils/ApiService";
import { ResourceType } from "../../../utils/EnumSupport";
import BookingModalCalendar from "../CreateBookingModal/BookingModalCalendar";
import BookingDate from "../../modelInterfaces/BookingDate";
import Resource from "../../modelInterfaces/Resource";
import { isValidDateRange } from "../../../utils/BookingSupport";

interface BookingCardProps {
  booking: Booking;
  onEdit: (id: number, updatedBooking: Booking) => void;
  onRemove: (id: number) => void;
}

const BookingCard: React.FC<BookingCardProps> = ({
  booking,
  onEdit,
  onRemove,
}) => {

  const [isEditing, setIsEditing] = useState(false);
  const [editedBooking, setEditedBooking] = useState<Booking>(booking);
  const [bookedDates, setBookedDates] = useState<BookingDate[]>([]);
  const [capacity, setCapacity] = useState<number>(0)

  useEffect (() => {
    getBookings(booking.resourceType, booking.resourceID)
    getCapacity()
    console.log("EDITED BOOKING", editedBooking)
  }, []);


  const parseDate = (date: string | Date | null): string => {
    return date ? new Date(date).toLocaleDateString() : "N/A";
  };

  const getBookings = async ( resourceType: ResourceType, id: number) => {
    try {
      const bookedDatesResponse = await ApiService.fetchBookings(resourceType, id)
      console.log("bookedDates for", booking.resourceName, bookedDatesResponse);
      setBookedDates(bookedDatesResponse.data)
    } catch(error) {
      console.log(error)
    }
  }

  const getCapacity = async () => {
    try {
      const resourceResponse = await ApiService.fetchData<Resource>(`${booking.resourceType.toLowerCase()}/${booking.resourceID}`)
      console.log("capacity for", booking.resourceName, resourceResponse.data.capacity);
      setCapacity(resourceResponse.data.capacity)
    } catch(error) {
      console.log(error)
    }
  }

  const handleDateChange = (start: Date | null, end: Date | null) => {
    setEditedBooking({
      ...editedBooking,
      startDate: start,
      endDate: end,
    });
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setEditedBooking({
      ...editedBooking,
      [name]: value as TimeRange,
    });
  };

  const handleSave = () => {
    onEdit(booking.id, editedBooking);
    setIsEditing(false);
  };

  return (
    <div className="card mb-3">
      <div className="card-body">
        {isEditing ? (
          <div>
            <BookingModalCalendar
              bookedDates={bookedDates}
              onDateChange={handleDateChange}
              resourceCapacity={capacity}
              initialStartDate={null}
              initialEndDate={null}
              inProgress={false}
              isDone={false}
            />
            <label
              htmlFor={`resourceName-${booking.id}`}
              className="form-label"
            >
              Resource Name
            </label>
            <input
              id={`resourceName-${booking.id}`}
              value={editedBooking.resourceName}
              className="form-control mb-2"
              disabled
              aria-label="Resource Name"
            />

            <label
              htmlFor={`bookStartTime-${booking.id}`}
              className="form-label"
            >
              Reservation Start Dato
            </label>
            <input
              type="text"
              id={`bookStartTime-${booking.id}`}
              name="startDate"
              value={parseDate(editedBooking.startDate)}
              className="form-control mb-2"
              aria-label="Booking Start Time"
              disabled
            />

            <label htmlFor={`bookEndTime-${booking.id}`} className="form-label">
              Reservation Slut Dato
            </label>
            <input
              type="text"
              id={`bookEndTime-${booking.id}`}
              name="endDate"
              value={parseDate(editedBooking.endDate)}
              className="form-control mb-2"
              aria-label="Booking End Time"
              disabled
            />

            <label htmlFor={`pickup-${booking.id}`} className="form-label">
              Afhentningstid
            </label>
            <select
              id={`pickup-${booking.id}`}
              name="pickupTime"
              value={editedBooking.pickupTime.toString()}
              className="form-control mb-2"
              onChange={handleChange}
              aria-label="Pickup Time"
            >
              <option value={TimeRange.EARLY}>{TimeRange.EARLY}</option>
              <option value={TimeRange.LATE}>{TimeRange.LATE}</option>
            </select>

            <label htmlFor={`dropoff-${booking.id}`} className="form-label">
              Afleveringstid
            </label>
            <select
              id={`dropoff-${booking.id}`}
              name="dropoffTime"
              value={editedBooking.dropoffTime.toString()}
              className="form-control mb-2"
              onChange={handleChange}
              aria-label="Dropoff Time"
            >
              <option value={TimeRange.EARLY}>{TimeRange.EARLY}</option>
              <option value={TimeRange.LATE}>{TimeRange.LATE}</option>
            </select>

            <button 
              onClick={handleSave} 
              className="btn btn-success me-2" 
              disabled={
                !isValidDateRange(
                  editedBooking.startDate,
                  editedBooking.endDate,
                  bookedDates,
                  capacity
                )
              }>
              Gem
            </button>
            <button
              onClick={() => setIsEditing(false)}
              className="btn btn-danger"
            >
              Anuller
            </button>
          </div>
        ) : (
          <div>
            <h5 className="card-title">{booking.resourceName}</h5>
            <p className="card-text">
              Reservation Start: {parseDate(booking.startDate)}
            </p>
            <p className="card-text">
              Reservation Slut: {parseDate(booking.endDate)}
            </p>
            <p className="card-text">Afhentningstid: {booking.pickupTime.toString()}</p>
            <p className="card-text">Afleveringstid: {booking.dropoffTime.toString()}</p>
            <button
              onClick={() => setIsEditing(true)}
              className="btn btn-secondary"
            >
              Rediger
            </button>
            <button
              onClick={() => onRemove(booking.id)}
              className="btn btn-danger"
            >
              Fjern
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
export default BookingCard;