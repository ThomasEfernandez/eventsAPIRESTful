package com.gestion.eventos.api.service;

import java.util.List;

import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.SpeakerRequestDto;

public interface SpeakerService {
    List<Speaker> findAll();
   Speaker findById(Long id);
   Speaker update(Long id, SpeakerRequestDto speakerRequestDto);
    Speaker save(SpeakerRequestDto speakerRequestDto);
    void deleteById(Long id);
}
