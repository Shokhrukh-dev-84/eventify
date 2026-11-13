package org.example.best.booking.mapper;

import org.example.best.booking.dto.response.BookingResponse;
import org.example.best.booking.model.Booking;
import org.example.best.common.dto.pagination.PageableBookingResponse;
import org.example.best.event.mapper.EventMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface BookingMapper {
      @Mapping(source = "user.email", target = "customerEmail")
      BookingResponse toResponse(Booking booking);

      @Mapping(target = "content", source = "bookingsPage.content")
      @Mapping(target = "totalElements", source = "bookingsPage.totalPages")
      @Mapping(target = "totalPages", source = "bookingsPage.totalPages")
      @Mapping(target = "number", source = "bookingsPage.number")
      @Mapping(target = "size", source = "bookingsPage.size")
      @Mapping(target = "numberOfElements", source = "bookingsPage.numberOfElements")
      @Mapping(target = "first", expression = "java(bookingsPage.isFirst())")
      @Mapping(target = "last", expression = "java(bookingsPage.isLast())")
      @Mapping(target = "empty", expression = "java(bookingsPage.isEmpty())")
      @Mapping(target = "pageable.pageSize", source = "pageable.pageSize")
      @Mapping(target = "pageable.pageNumber", source = "pageable.pageNumber")
      @Mapping(target = "pageable.offset", source = "pageable.offset")
      @Mapping(target = "pageable.paged", constant = "true")
      @Mapping(target = "pageable.unpaged", constant = "false")
      @Mapping(target = "sort", ignore = true)
      PageableBookingResponse toPageableResponse(Page<Booking> bookingsPage, Pageable pageable);

}