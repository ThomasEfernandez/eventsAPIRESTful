package com.gestion.eventos.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.SpeakerRequestDto;
import com.gestion.eventos.api.exceptions.ResourceNotFoundException;
import com.gestion.eventos.api.mapper.SpeakerMapper;
import com.gestion.eventos.api.repository.SpeakerRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SpeakerServiceImpl implements SpeakerService {
private final SpeakerRepository speakerRepository;
private final SpeakerMapper speakerMapper;
@Override
    public Speaker save(SpeakerRequestDto speakerRequestDto) {

       Speaker speaker = speakerMapper.toEntity(speakerRequestDto);
       return speakerRepository.save(speaker);

    }


    @Override
    
    @Transactional(readOnly = true)
    public List<Speaker> findAll() {
     
        return speakerRepository.findAll();
    }

   
    @Override
    @Transactional(readOnly = true)
    public Speaker findById(Long id) {
    return speakerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Speaker not found with id: " + id));
   
}

    @Override
    @Transactional 
    public Speaker update(Long id, SpeakerRequestDto speakerRequestDto) {
    
        Speaker existingSpeaker = speakerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speaker no encontrado con id: " + id));
        speakerMapper.updateSpeakerFromDto(speakerRequestDto, existingSpeaker);
        return speakerRepository.save(existingSpeaker);
    
    }

 

    @Override
    @Transactional
    public void deleteById(Long id) {
    
        if (!speakerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Speaker not found with id: " + id);
        }
        speakerRepository.deleteById(id);
    }

}
