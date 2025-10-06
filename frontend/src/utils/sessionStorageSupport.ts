import Booking from "../components/modelInterfaces/Booking";

const getBookingList = (): Booking[] => {
  const bookingsString = sessionStorage.getItem("bookingList");
  if (!bookingsString) return [];
  
  return JSON.parse(bookingsString).map((booking: any) => ({
    ...booking,
    startDate: booking.startDate ? new Date(booking.startDate) : null,
    endDate: booking.endDate ? new Date(booking.endDate) : null,
  }));
};

export const getHighestBookingID = (): number => {
  const bookings = getBookingList();
  return bookings.reduce((highestID, booking) => {
    return booking.id > highestID ? booking.id : highestID;
  }, 0);
}

const saveBookingList = (bookingList: Booking[]): void => {
  sessionStorage.setItem("bookingList", JSON.stringify(bookingList));
};

const dispatchBookingsUpdatedEvent = (): void => {
  const event = new Event("bookingsUpdated");
  window.dispatchEvent(event);
};

export const getBookingCount = (): number => {
  const bookings = getBookingList();
  return bookings.length;
};

export const addBookingToSessionStorage = (booking: Booking): void => {
  const bookingList = getBookingList();
  bookingList.push(booking);
  saveBookingList(bookingList);
  dispatchBookingsUpdatedEvent();
};

export const loadBookingsFromSessionStorage = (): Booking[] => {
  return getBookingList();
};

export const updateBookingInSessionStorage = (id: number, updatedBooking: Booking): void => {
  const bookingList = getBookingList();
  const updatedBookings = bookingList.map((booking) =>
    booking.id === id ? updatedBooking : booking
  );
  saveBookingList(updatedBookings);
  dispatchBookingsUpdatedEvent();
};

export const removeBookingFromSessionStorage = (id: number): void => {
  const bookingList = getBookingList();
  const updatedBookings = bookingList.filter((booking) => booking.id !== id);
  saveBookingList(updatedBookings);
  dispatchBookingsUpdatedEvent();
};
