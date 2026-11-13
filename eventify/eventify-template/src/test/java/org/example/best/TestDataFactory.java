package org.example.best;

import org.example.best.auth.dto.AuthType;
import org.example.best.auth.dto.response.AuthResponse;
import org.example.best.booking.dto.request.CreateBookingRequest;
import org.example.best.booking.dto.request.UpdateBookingRequest;
import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.model.Booking;
import org.example.best.common.dto.pagination.PageableEventResponse;
import org.example.best.event.dto.request.EventCreateRequest;
import org.example.best.event.dto.request.EventUpdateRequest;
import org.example.best.event.dto.response.EventResponse;
import org.example.best.event.model.Event;
import org.example.best.user_profile.dto.NotificationPreferences;
import org.example.best.user_profile.dto.request.LoginRequest;
import org.example.best.user_profile.dto.request.RegisterRequest;
import org.example.best.user_profile.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class TestDataFactory {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_EMAIL = "example@mail.com";
    public static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.now().plusMinutes(1L);

    private TestDataFactory() {}

    public static User defaultUser() {
        return new User();
    }

    public static Event defaultEvent() {
        return new Event(
                DEFAULT_ID,
                "title",
                "description",
                DEFAULT_DATE_TIME,
                50,
                30,
                new ArrayList<>(),
                defaultUser()
        );
    }

    public static Booking defaultBooking() {
        Event event = defaultEvent();
        return new Booking(
                DEFAULT_ID,
                50,
                DEFAULT_DATE_TIME,
                DEFAULT_DATE_TIME.plusMinutes(15),
                "Europe/Moscow",
                true,
                defaultUser(),
                event
        );
    }

    public static EventResponse defaultEventResponse() {
        return new EventResponse(
           DEFAULT_ID,
           "title",
           "description",
           DEFAULT_DATE_TIME,
           50,
           30
        );
    }

    public static BookingResponse defaultBookingResponse() {
        EventResponse eventResponse = defaultEventResponse();
        return new BookingResponse(
                DEFAULT_ID,
                eventResponse,
                DEFAULT_EMAIL,
                50,
                DEFAULT_DATE_TIME,
                DEFAULT_DATE_TIME.plusMinutes(15),
                true,
                "Europe/Moscow"
        );
    }

    public static EventCreateRequest defaultEventCreateRequest() {
        return new EventCreateRequest(
               "title",
               "description",
               "coverUrl",
               DEFAULT_DATE_TIME,
               50
        );
    }

    public static CreateBookingRequest defaultCreateBookingRequest() {
        return new CreateBookingRequest(
                DEFAULT_ID,
                20
        );
    }

    public static EventUpdateRequest defaultEventUpdateRequest() {
        return new EventUpdateRequest(
                "title",
                "description",
                DEFAULT_DATE_TIME,
                50,
                "coverUrl"
        );
    }

    public static UpdateBookingRequest defaultUpdateBookingRequest() {
        return new UpdateBookingRequest(50);
    }

    public static RegisterRequest defaultRegisterRequest() {
        return new RegisterRequest(
          "example@maikl.com",
          "password");
    }

    public static LoginRequest defaultLoginRequest() {
        return new LoginRequest(
                "example@maikl.com",
                "password" );
    }

    public static AuthResponse defaultAuthResponse() {
        return new AuthResponse(
              "token",
              AuthType.ADMIN
        );
    }

    public static NotificationPreferences defaultPreferences() {
        return new NotificationPreferences(
                true,
                true,
                20
        );
    }

    }

