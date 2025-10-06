import React, { useState, useEffect } from "react";
import BookingCard from "./BookingCard";
import Booking from "../../modelInterfaces/Booking";
import {
  loadBookingsFromSessionStorage,
  updateBookingInSessionStorage,
  removeBookingFromSessionStorage,
} from "../../../utils/sessionStorageSupport";
import ApiService from "../../../utils/ApiService";

const Checkout: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [trigger, setTrigger] = useState(false);

  // Load bookings from session storage on mount
  useEffect(() => {
    setBookings(loadBookingsFromSessionStorage());
  }, [trigger]);

  const handleEdit = (id: number, updatedBooking: Booking) => {
    updateBookingInSessionStorage(id, updatedBooking);
    setBookings(loadBookingsFromSessionStorage());
  };

  const handleRemove = (id: number) => {
    removeBookingFromSessionStorage(id);
    setBookings(loadBookingsFromSessionStorage());
  };

  const handleFinalize = async () => {
    console.log("Bookings finalized:", bookings);
  
    for (const booking of bookings) {
      console.log("Creating booking", booking);
  
      const transformedBooking = transformBooking(booking);
  
      let response = await ApiService.createBooking(transformedBooking);
      if (response.status !== 200) {
        console.error("Failed to create booking", booking);
        return;
      }
      removeBookingFromSessionStorage(booking.id);
    }
    setTrigger((prev) => !prev); //used to reload the bookings
  };

  const transformBooking = (booking: any) => {
    return {
      resourceID: booking.resourceID,
      resourceType: booking.resourceType.toUpperCase(),
      startDate: formatDate(booking.startDate),
      endDate: formatDate(booking.endDate),
      pickupTime: booking.pickupTime,
      dropoffTime: booking.dropoffTime
    };
  };
  
  const formatDate = (date: string) => {
    const newDate = new Date(date);
    return newDate.toLocaleDateString('en-CA');
  };
  
  
  
  return (
    <div className="container mt-4 border border-darkgrey border-4 rounded">
      <h2 className="text-center mb-4">
        <strong>Dine Ubekræftede Reservationer</strong>
      </h2>
      {bookings.length === 0 ? (
        <p>Ingen reservationer endnu</p>
      ) : (
        bookings.map((booking) => (
          <BookingCard
            key={booking.id}
            booking={booking}
            onEdit={handleEdit}
            onRemove={handleRemove}
          />
        ))
      )}
      {bookings.length !== 0 && (
        <button onClick={handleFinalize} className="btn btn-primary w-100 mt-3">
          Bekræft
        </button>
      )}
    </div>
  );
};

export default Checkout;
