package com.gestion.eventos.api.mapper;

import org.mapstruct.Mapper;

import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.SpeakerRequestDto;
import com.gestion.eventos.api.dto.SpeakerResponseDto;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;


   @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpeakerMapper {
    
    SpeakerResponseDto toDto (Speaker speaker);
    @Mapping (target = "events", ignore = true)
    @Mapping (target = "id", ignore = true)
    
    Speaker toEntity (SpeakerRequestDto speakerDto);

   List<SpeakerResponseDto> toListSpeakersDto(List<Speaker> speakers);
   @Mapping (target = "events", ignore = true)
   @Mapping (target = "id", ignore = true)
   void updateSpeakerFromDto(SpeakerRequestDto speakerDto, @MappingTarget Speaker speaker);

}

/*

    @Mapping (target = "category", ignore = true)
    @Mapping (target = "speakersDtos", ignore = true)
    SpeakerResponseDto toDto (Speaker speaker);
    @Mapping (target = "events", ignore = true)
    @Mapping (target = "id", ignore = true)
    
    Speaker toEntity (SpeakerRequestDto speakerDto);

   List<SpeakerResponseDto> toListSpeakersDto(List<Speaker> speakers);
   @Mapping (target = "events", ignore = true)
   @Mapping (target = "id", ignore = true)
   void updateSpeakerFromDto(SpeakerRequestDto speakerDto, @MappingTarget Speaker speaker);




*/
