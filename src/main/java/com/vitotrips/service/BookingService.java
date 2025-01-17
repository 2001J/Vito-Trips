package com.vitotrips.service;

import com.vitotrips.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking updateBooking(Long bookingId, Booking updatedBooking);
    Booking getBookingById(Long bookingId);
    List<Booking> getAllBookings();
    boolean deleteBooking(Long bookingId);
}