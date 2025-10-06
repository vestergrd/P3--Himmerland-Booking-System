import React, { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import BookingDate from "../../modelInterfaces/BookingDate";
import { isBeforeToday, isWeekend } from "../../../utils/BookingSupport";

interface BookingModalCalendarProps {
  bookedDates: BookingDate[];
  onDateChange: (start: Date | null, end: Date | null) => void;
  resourceCapacity: number;
  initialStartDate: Date | null;
  initialEndDate: Date | null;
  inProgress: boolean | undefined;
  isDone: boolean | undefined;
}

const BookingModalCalendar: React.FC<BookingModalCalendarProps> = ({
  bookedDates,
  onDateChange,
  resourceCapacity,
  initialStartDate,
  initialEndDate,
  inProgress,
  isDone,
}) => {
  const [selectedStart, setSelectedStart] = useState<Date | null>(initialStartDate);
  const [selectedEnd, setSelectedEnd] = useState<Date | null>(
    initialEndDate ? new Date(initialEndDate.getTime() + 24 * 60 * 60 * 1000) : null
  );
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const isBooked = (date: Date) => {
    const booked = bookedDates.find(
      (booked) => new Date(booked.date).toDateString() === date.toDateString()
    );
    return booked ? booked.amount >= resourceCapacity : false;
  };

  const isInRange = (date: Date) => {
    return (
      selectedStart &&
      selectedEnd &&
      date >= selectedStart &&
      date < selectedEnd
    );
  };

  const handleDateClick = (date: Date) => {
    if (isDone) {
      return;
    }
    if (inProgress) {
      if (selectedStart && date > selectedStart) {
        setSelectedEnd(date);
        onDateChange(selectedStart, date);
      }
    } else {
      if (!selectedStart || (selectedStart && selectedEnd)) {
        setSelectedStart(date);
        setSelectedEnd(null);
        onDateChange(date, null);
      } else if (selectedStart && !selectedEnd) {
        if (date > selectedStart) {
          setSelectedEnd(date);
          onDateChange(selectedStart, date);
        } else {
          setSelectedStart(date);
          setSelectedEnd(null);
          onDateChange(date, null);
        }
      }
    }

  };

  return (
    <div className="calendar-wrapper">
      <Calendar
        tileClassName={({ date }) => {
          if (isWeekend(date)) {
            return "weekend-tile";
          }
          if (isBeforeToday(date)) {
            return 'unavailable-tile';
          }
          if (isBooked(date)) {
            return "booked-tile";
          }
          if (isInRange(date)) {
            return "range-tile";
          }
          return "free-tile";
        }}
        onClickDay={handleDateClick}
        tileDisabled={({ date }) => !!(isBeforeToday(date) || (inProgress && selectedStart && date < selectedStart))}
      />
      <div className="calendar-legend">
        <div className="legend-item">
          <span className="legend-color booked-color"></span>
          <span>Reserveret</span>
        </div>
        <div className="legend-item">
          <span className="legend-color range-color"></span>
          <span>Valgt periode</span>
        </div>
        <div className="legend-item">
          <span className="legend-color currentday-color"></span>
          <span> Dags dato</span>
        </div>
        <div className="legend-item">
          <span className="legend-color unavailable-color"></span>
          <span>Weekend (Kan ikke reserveres)</span>
        </div>
      </div>
    </div>
  );
};

export default BookingModalCalendar;
