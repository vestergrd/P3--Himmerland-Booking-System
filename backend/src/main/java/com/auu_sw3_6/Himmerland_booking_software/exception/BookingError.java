package com.auu_sw3_6.Himmerland_booking_software.exception;

public enum BookingError {
  INVALID_DATE_RANGE("Startdato skal være før slutdato, og perioden må ikke overstige maksimum tilladt dage.", 1001),
  START_DATE_IN_PAST("Startdato kan ikke være tidligere end dags dato.", 1002),
  END_DATE_IN_PAST("Slutdato kan ikke være tidligere end dags dato.", 1003),
  DROPOFF_TIME_IN_PAST("Afleveringstidspunkt skal være i fremtiden for slutdatoen.", 1004),
  WEEKEND_BOOKING_NOT_ALLOWED("Booking på weekender er ikke tilladt.", 1005),
  PICKUP_TIME_IN_PAST("Afhentningstidspunkt skal være i fremtiden for startdatoen.", 1006),
  USER_NOT_FOUND("Bruger ikke fundet", 2001),
  TOO_MANY_BOOKINGS("For mange aktive bookinger for denne ressource.", 2002),
  TOO_OFTEN_BOOKING("For mange bookinger på en periode for denne ressource.", 2003),
  COMPLETED_BOOKING_EDIT("Kan ikke redigere afsluttet booking.", 2004),
  CANCELLED_BOOKING_EDIT("Kan ikke redigere annulleret booking.", 2005),
  LATE_BOOKING_EDIT("Kan ikke redigere forpasset booking.", 2006),
  ACTIVE_BOOKING_START_EDIT("Kan ikke ændre starttiden for aktiv booking.", 2007),
  DEFAULT_ERROR("Ressourcen er ikke tilgængelig for de valgte datoer.", 2008),
  USER_NOT_ALLOWED("Brugeren har ikke tilladelse til at redigere denne booking.", 2009);

  private final String errorMessage;
  private final int errorCode;

  BookingError(String errorMessage, int errorCode) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getErrorCode() {
    return errorCode;
  }

}
