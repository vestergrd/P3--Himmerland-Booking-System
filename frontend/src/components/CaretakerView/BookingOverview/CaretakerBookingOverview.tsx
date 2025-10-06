import React, { useState, useEffect } from 'react';
import CaretakerBookingCard from './CaretakerBookingCard';
import { Collapse, Button } from 'react-bootstrap';
import Booking from '../../modelInterfaces/Booking';
import ApiService from '../../../utils/ApiService';
import CaretakerOptions from './CaretakerOptions';

interface CaretakerBooking {
  id: number;
  name: string;
  resourceName: string;
  startDate: Date;
  endDate: Date;
  pickupTime: string;
  dropoffTime: string;
  mobileNumber: string;
  houseAddress: string;
  email: string;
  status: string;
  receiverName: string;
  handoverName: string;
  isFutureBooking: boolean;
  isPastBooking: boolean;
}

const CaretakerBookingOverview: React.FC = () => {
  const [bookings, setBookings] = useState<CaretakerBooking[]>([]);
  const [showActive, setShowActive] = useState(true);
  const [showFuture, setShowFuture] = useState(true);
  const [showPast, setShowPast] = useState(true);
  const [trigger, setTrigger] = useState(false);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const bookingsResponse = await ApiService.fetchData<any>("booking/get-all");
        console.log("booking response:", bookingsResponse);

        const transformedBookings: CaretakerBooking[] = bookingsResponse.data
        .filter((booking: any) => booking.resource.status !== "deleted")
        .map((booking: any) => {
          const startDate = new Date(booking.startDate[0], booking.startDate[1] - 1, booking.startDate[2]);
          const endDate = new Date(booking.endDate[0], booking.endDate[1] - 1, booking.endDate[2]);
          return {
            id: booking.id.toString(),
            name: booking.user.name,
            resourceName: booking.resource.name,
            startDate,
            endDate,
            status: booking.status,
            pickupTime: booking.pickupTime,
            dropoffTime: booking.dropoffTime,
            mobileNumber: booking.user.mobileNumber,
            houseAddress: booking.user.houseAddress,
            email: booking.user.email,
            receiverName: booking.receiverName,
            handoverName: booking.handoverName,
            isFutureBooking: startDate > currentDate || booking.status === "PENDING",
            isPastBooking: endDate < currentDate || booking.status === "COMPLETED",
          };

        });

        setBookings(transformedBookings);
        updateFutureBookings(transformedBookings);
      } catch (error) {
        console.error("Error fetching bookings:", error);
      }
    };

    fetchBookings();
  }, [trigger]);


  const handleCancel = async (id: number) => {
    try {
      await ApiService.updateData(`booking/${id}/cancel`);
      setBookings((prevBookings) => prevBookings.filter((booking) => booking.id !== id));
      window.dispatchEvent(new Event("bookingCanceled"));
    } catch (error) {
      console.error('Error canceling resource:', error);
    }
  };

  const onBookingComplete = (id: number) => {
    const updatedBookings = bookings.map((booking) => {
      if (booking.id === id) {
        const isCurrentlyConfirmed = booking.status === "CONFIRMED";
        const isLateBooking = booking.status === "LATE";
        return {
          ...booking,
          status: isCurrentlyConfirmed || isLateBooking ? "COMPLETED" : "CONFIRMED",
          isFutureBooking: false,
          isPastBooking: isCurrentlyConfirmed || isLateBooking,
        };
      }
      return booking;
    });

    setBookings(updatedBookings);
  };





  const updateFutureBookings = (bookings: CaretakerBooking[]) => {
    const updatedBookings = bookings.map((booking) => ({
      ...booking,
      isFutureBooking: booking.startDate > currentDate && booking.status === "PENDING",
      isPastBooking: booking.endDate < currentDate || booking.status === "COMPLETED",
    }));

    setBookings(updatedBookings);
  };


  const currentDate = new Date();

  useEffect(() => {
    const markLateBookings = async () => {
      const lateBookings = bookings.filter(
        (booking) =>
          booking.endDate < currentDate && booking.status === "CONFIRMED"
      );

      if (lateBookings.length > 0) {
        try {
          const updatedBookings = bookings.map((booking) => {
            if (
              lateBookings.some((lateBooking) => lateBooking.id === booking.id)
            ) {
              return { ...booking, status: "LATE" };
            }
            return booking;
          });

          await Promise.all(
            lateBookings.map((lateBooking) =>
              ApiService.markBookingAsLate(lateBooking.id)
            )
          );

          setBookings((prevBookings) =>
            JSON.stringify(prevBookings) !== JSON.stringify(updatedBookings)
              ? updatedBookings
              : prevBookings
          );
        } catch (error) {
          console.error("Error marking booking as late:", error);
        }
      }
    };

    markLateBookings();
  }, [bookings, currentDate]);



  const activeBookings = bookings.filter(
    (booking) =>
      (booking.status === "CONFIRMED" || booking.status === "LATE")
  );


  const futureBookings = bookings.filter(
    (booking) =>
      booking.status === "PENDING"
  );

  const pastBookings = bookings.filter(
    (booking) =>
      booking.status === "COMPLETED"
  );



  return (
    <>
      <div>
        <CaretakerOptions />
      </div>
      <div className="container mt-4 border border-darkgrey border-4 rounded mb-3">
        <h2 className="text-center mb-5"><strong>Reservationer</strong></h2>
        {/* Active Bookings */}
        <h3>
          <Button
            variant="secondary"
            onClick={() => setShowActive(!showActive)}
            aria-controls="active-bookings-collapse"
            aria-expanded={showActive}
            className="fs-5"
          >
            Nuværende reservationer
          </Button>
        </h3> <hr />
        <Collapse in={showActive}>
          <div id="active-bookings-collapse">
            {activeBookings.length === 0 ? (
              <p>Ingen nuværende reservationer</p>
            ) : (
              activeBookings.map((booking) => (<CaretakerBookingCard key={booking.id} booking={booking} onCancel={handleCancel} onComplete={onBookingComplete} trigger={() => setTrigger((prev) => !prev)} />))
            )}
          </div>
        </Collapse>

        {/* Future Bookings */}
        <h3>
          <Button
            variant="secondary"
            onClick={() => setShowFuture(!showFuture)}
            aria-controls="future-bookings-collapse"
            aria-expanded={showFuture}
            className="fs-5"
          >
            Kommende reservationer
          </Button>
        </h3> <hr />
        <Collapse in={showFuture}>
          <div id="future-bookings-collapse">
            {futureBookings.length === 0 ? (
              <p>Ingen kommende reservationer</p>
            ) : (
              futureBookings.map((booking) => (<CaretakerBookingCard key={booking.id} booking={booking} onCancel={handleCancel} onComplete={onBookingComplete} trigger={() => setTrigger((prev) => !prev)} />))
            )}
          </div>
        </Collapse>

        {/* Past Bookings */}
        <h3>
          <Button
            variant="secondary"
            onClick={() => setShowPast(!showPast)}
            aria-controls="past-bookings-collapse"
            aria-expanded={showPast}
            className="fs-5"
          >
            Tidligere reservationer
          </Button>
        </h3> <hr />
        <Collapse in={showPast}>
          <div id="past-bookings-collapse">
            {pastBookings.length === 0 ? (
              <p>Ingen tidligere reservationer</p>
            ) : (
              pastBookings.map((booking) => (<CaretakerBookingCard key={booking.id} booking={booking} onCancel={handleCancel} onComplete={onBookingComplete} trigger={() => setTrigger((prev) => !prev)} />))
            )}
          </div>
        </Collapse>
      </div>
    </>
  );
};

export default CaretakerBookingOverview;