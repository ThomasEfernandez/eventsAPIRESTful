package com.gestion.eventos.api.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.gestion.eventos.api.dto.EventResponseDto;
import com.gestion.eventos.api.dto.EventSummaryDto;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.dto.EventRequestDto;


@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    //INPUT
    @Mapping (target = "id", ignore = true)
    @Mapping (target = "category", ignore = true)
    @Mapping (target = "speakers", ignore = true)
    @Mapping (target = "attendedUsers", ignore = true)
    Event toEntity (EventRequestDto eventRequestDto);



    //OUTPUT
    @Mapping (target = "categoryId", ignore = true)
    @Mapping (target = "categoryName", ignore = true)
    @Mapping (target = "speakers", ignore = true)
    EventResponseDto toResponseDto (Event event);
    List<EventResponseDto>toEventResponseDtoList (List<Event>event);

    //UPDATE
    void updateEventFromDto (EventRequestDto dto,@MappingTarget Event event);

    EventSummaryDto toSummaryDto (Event event);
    List<EventSummaryDto> toSummaryDtoList (List<Event> events);
}