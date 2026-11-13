package org.example.best.event.mapper;

import org.example.best.common.dto.pagination.PageableEventResponse;
import org.example.best.event.dto.request.EventCreateRequest;
import org.example.best.event.dto.request.EventUpdateRequest;
import org.example.best.event.dto.response.EventResponse;
import org.example.best.event.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "eventId", target = "id")
    EventResponse toResponse(Event event);

    @Mapping(target = "eventId", ignore = true)
    Event toEntity(EventCreateRequest request);

    void updateEntityFromDto(EventUpdateRequest dto,
                             @MappingTarget Event event);


    @Mapping(target = "pageable", source = "events.pageable")
    @Mapping(target = "sort", source = "events.sort")
    PageableEventResponse toPageableResponse(Page<Event> events);

}

