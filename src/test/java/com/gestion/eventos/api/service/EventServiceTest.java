package com.gestion.eventos.api.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.EventRequestDto;
import com.gestion.eventos.api.dto.EventResponseDto;
import com.gestion.eventos.api.exceptions.ResourceNotFoundException;
import com.gestion.eventos.api.mapper.EventMapper;
import com.gestion.eventos.api.repository.EventRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)

public class EventServiceTest {
    @Mock
  private  EventRepository eventRepository;
  @Mock
  private  EventMapper eventMapper;
  @Mock
  private  CategoryService categoryService;
  @Mock
  private  SpeakerService speakerService;
  @InjectMocks 
  private EventService eventService;

  private Event event;
  private EventRequestDto eventRequestDto;
  private EventResponseDto eventResponseDto;
  private Category category;
  private Speaker speaker;
  private Speaker speaker2;
  private Pageable pageable;


  @BeforeEach 
  void setUP (){
    category = new Category(1L,"Conferencia", "Description de conferencia");
    speaker = new Speaker(10L,"Jhon Doe", "jhonDOe@example.com","Bio de jhon", new HashSet<>());
    speaker2 = new Speaker(11L,"Jane Smith", "jane@example.com","Bio de jane", new HashSet<>());


    event = new Event();
    event.setId(1L);
    event.setName("Event 1");
    event.setDescription("Description 1");
    event.setCategory(category);
    event.setLocation("Online");
    event.setDate(LocalDate.of(2026, 10, 10));
    event.getSpeakers().add(speaker);
    event.getSpeakers().add(speaker2);

    eventRequestDto = new EventRequestDto();
    eventRequestDto.setName("Spring Boot Conf");
    eventRequestDto.setDate(LocalDate.of(2026, 10, 10));
    eventRequestDto.setLocation("Online");
    eventRequestDto.setCategoryId(1L);
    eventRequestDto.setSpeakerIds(new HashSet<>(Set.of(10L, 11L)));

    eventResponseDto = new EventResponseDto();
    eventResponseDto.setId(1L);
    eventResponseDto.setName("Spring Boot Conf");
    eventResponseDto.setDate(LocalDate.of(2026, 10, 10));
    eventResponseDto.setLocation("Online");
    eventResponseDto.setCategoryId(1L);
    eventResponseDto.setCategoryName("Conferencia");


    pageable = Pageable.ofSize(10).withPage(0);


  }



@Test 
@DisplayName ("Debe retornar un Event cuando el ID existe")
void shouldReturnEventWhenIdExists() {
    // Arrange
    Long eventId = 1L;
    when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
    //when(eventMapper.toResponseDto(event)).thenReturn(eventResponseDto);

    Event foundEvent = eventService.findById(1L);
    assertNotNull(foundEvent);
    assertEquals(event.getId(), foundEvent.getId());
    verify(eventRepository,times(1)).findById(eventId);


}

@Test 
@DisplayName ("Debe lanzar ResourceNotFoundException cuando el ID no existe")
void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

ResourceNotFoundException exception = assertThrows(
  ResourceNotFoundException.class, () -> {
   
       eventService.findById(99L);
  });

  assertEquals("Evento no encontrado con id: 99", exception.getMessage());
  verify(eventRepository,times(1)).findById(99L);

}


@Test 
@DisplayName ("Debe guardar un evento correctamente con categoria y speakers")
void shouldSaveEventWithCategoryAndSpeakers() {
    Event eventWithoutID = new Event();

    eventWithoutID.setName(eventRequestDto.getName());
    eventWithoutID.setDate(eventRequestDto.getDate());
    eventWithoutID.setLocation(eventRequestDto.getLocation());
    when(eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoutID);
    

    when(categoryService.findById(eventRequestDto.getCategoryId())).thenReturn(category);
    when(speakerService.findById(10L)).thenReturn(speaker);
    when(speakerService.findById(11L)).thenReturn(speaker2);


    when(eventRepository.save(any(Event.class))).thenAnswer(

      invocation ->{
        Event savedEvent = invocation.getArgument(0);
        savedEvent.setId(1l);     
        return savedEvent;
      
      }

    );

    Event savedEvent = eventService.save(eventRequestDto);
    assertNotNull(savedEvent);
    assertEquals(1L, savedEvent.getId());
    assertEquals(eventRequestDto.getName(), savedEvent.getName());
    assertEquals(category, savedEvent.getCategory());
    assertEquals(2, savedEvent.getSpeakers().size());

    assertTrue(savedEvent.getSpeakers().contains(speaker));
    assertTrue(savedEvent.getSpeakers().contains(speaker2));  

    verify(eventMapper, times(1)).toEntity(any(EventRequestDto.class));
   verify(categoryService, times(1)).findById(eventRequestDto.getCategoryId());
   verify(speakerService, times(1)).findById(10L);
   verify(speakerService, times(1)).findById(11L);
   verify(eventRepository, times(1)).save(any(Event.class));

}
@Test 
@DisplayName ("Debe guardar un evento correctamente sin speakers")
void  shouldSaveEventWithoutSpeakers(){
  eventRequestDto.setSpeakerIds(null);
  
  Event eventWithoutId = new Event();
  
eventWithoutId.setName(eventRequestDto.getName());
eventWithoutId.setLocation(eventRequestDto.getLocation());
eventWithoutId.setDate(eventRequestDto.getDate());

when (eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoutId);

when (categoryService.findById(eventRequestDto.getCategoryId())).thenReturn(category);

when (eventRepository.save(any(Event.class))).thenAnswer (

  invocation -> {
    Event savedEvent = invocation.getArgument(0);
    savedEvent.setId(1L);
    return savedEvent;
  });
  Event savedEvent = eventService.save(eventRequestDto);
  assertNotNull(savedEvent);
  assertEquals(1L, savedEvent.getId());
  assertEquals(eventRequestDto.getName(), savedEvent.getName());
  assertEquals(category, savedEvent.getCategory());
  assertTrue(savedEvent.getSpeakers().isEmpty());




  verify (eventMapper, times(1)).toEntity(any(EventRequestDto.class));
  verify (categoryService, times(1)).findById(eventRequestDto.getCategoryId());
  verify(speakerService, never()).findById(anyLong());
  verify (eventRepository, times(1)).save(any(Event.class));


}

@Test 
@DisplayName ("Debe lanzar excepcion ResourceNotFoundException si la categoria no existe al guardar")
void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundOnSave (){

Event eventWithoutId = new Event();

when(eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoutId);

when (categoryService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Categoria no encontrada con id: "+ eventRequestDto.getCategoryId()));

ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
  eventService.save(eventRequestDto);



});

assertEquals("Categoria no encontrada con id: "+ eventRequestDto.getCategoryId(), thrown.getMessage());
verify(eventRepository,never()).save(any(Event.class));
}

@Test 
@DisplayName ("Debe retornar una pagina de eventos sin filtro de nombre")

void shouldReturnEventPageWithoutNameFilter() {
 List<Event> events = Collections.singletonList(event);
 Page <Event> eventPage = new PageImpl<>(events, pageable, 1);




 when (eventRepository.findAll(pageable)).thenReturn (eventPage);
 when (eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDto);














 Page <EventResponseDto> resultPage = eventService.findAll(null, pageable);
 assertNotNull(resultPage);
assertEquals(1, resultPage.getTotalElements());
assertEquals(1, resultPage.getContent().size());
assertEquals(eventResponseDto, resultPage.getContent().get(0));

    
verify (eventRepository, times(1)).findAll(pageable);
verify (eventRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
verify (eventMapper, times(1)).toResponseDto(event);



}


@Test 
@DisplayName ("Debe retornar una pagina de eventos con filtro de nombre")
void shouldReturnEventPageWithNameFilter() {
String filterName = "Spring";
List<Event> events = Collections.singletonList(event);
Page<Event>eventPage = new PageImpl<>(events,pageable,1);

when(eventRepository.findByNameContainingIgnoreCase(filterName, pageable)).thenReturn(eventPage);
when (eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDto);


Page<EventResponseDto> resultPage = eventService.findAll(filterName, pageable);
assertNotNull(resultPage);
assertEquals(1, resultPage.getTotalElements());
assertEquals(1, resultPage.getContent().size());
assertEquals(eventResponseDto, resultPage.getContent().get(0));


verify (eventRepository, times(1)).findByNameContainingIgnoreCase(filterName, pageable);
verify (eventRepository, never()).findAll(any(Pageable.class));
verify (eventMapper, times(1)).toResponseDto(event);



}












}