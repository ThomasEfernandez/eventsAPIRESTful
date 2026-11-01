package com.gestion.eventos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.EventRequestDto;
import com.gestion.eventos.api.dto.EventResponseDto;
import com.gestion.eventos.api.dto.SpeakerResponseDto;
import com.gestion.eventos.api.exceptions.ResourceNotFoundException;
import com.gestion.eventos.api.mapper.EventMapper;
import com.gestion.eventos.api.repository.EventRepository;
import com.gestion.eventos.api.security.jwt.JwtAuthEntryPoint;
import com.gestion.eventos.api.security.jwt.JwtAuthenticationFilter;
import com.gestion.eventos.api.security.jwt.JwtGenerator;
import com.gestion.eventos.api.service.EventService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = EventController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthenticationFilter.class,
                JwtGenerator.class,
                JwtAuthEntryPoint.class
        })
)
public class EventControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper; // <-- Solucionado: Inyectado correctamente

    private EventService eventService;
    private EventMapper eventMapper;
    private EventResponseDto eventResponseDto;
    private Event eventEntity;

    @TestConfiguration 
    static class EventControllerTestConfig {
        @Bean
        @Primary 
        EventService eventService() {
            return mock(EventService.class);
        }

        @Bean
        @Primary
        EventMapper eventMapper() {
            return mock(EventMapper.class);
        }

        @Bean
        @Primary
        EventRepository eventRepository() {
            return mock(EventRepository.class);
        }
    }

    @BeforeEach
    void setUp(@Autowired EventService eventServiceMock, @Autowired EventMapper eventMapperMock) {
        this.eventService = eventServiceMock;
        this.eventMapper = eventMapperMock;

        reset(eventService, eventMapper);

        Category category = new Category(10L, "Tecnología", "Descripción de Tecnología");
        Speaker speaker1 = new Speaker(20L, "Juan Perez", "juan.perez@example.com", "Experto en Spring Boot", new HashSet<>());
        Speaker speaker2 = new Speaker(21L, "Maria Garcia", "maria.garcia@example.com", "Arquitecta en software", new HashSet<>());
        
        eventEntity = new Event();
        eventEntity.setId(1L);
        eventEntity.setName("Conferencia Tech");
        eventEntity.setDescription("Descripción del evento");
        eventEntity.setCategory(category);
        eventEntity.setLocation("Online");
        eventEntity.getSpeakers().add(speaker1);
        eventEntity.getSpeakers().add(speaker2);

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(1L);
        eventResponseDto.setName("Conferencia Tech");
        eventResponseDto.setDate(LocalDate.of(2026, 10, 10));
        eventResponseDto.setLocation("Online");
        eventResponseDto.setCategoryName("Tecnología");
        eventResponseDto.setCategoryId(10L);

        SpeakerResponseDto speakerResponseDto1 = new SpeakerResponseDto(
                20L, "Juan Perez", "juan.perez@example.com", "Experto en Spring Boot", null, null
        );

        SpeakerResponseDto speakerResponseDto2 = new SpeakerResponseDto(
                21L, "Maria Garcia", "maria.garcia@example.com", "Arquitecta en software", null, null
        );

        Set<SpeakerResponseDto> speakersDtos = new HashSet<>();
        speakersDtos.add(speakerResponseDto1);
        speakersDtos.add(speakerResponseDto2);

        eventResponseDto.setSpeakers(speakersDtos);
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} - Debe retornar un evento por id cuando existe")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void shouldReturnEventById() throws Exception {
        when(eventService.findById(anyLong())).thenReturn(eventEntity);
        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conferencia Tech"))
                .andExpect(jsonPath("$.location").value("Online"))
                .andExpect(jsonPath("$.categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.speakers.length()").value(2))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Juan Perez')].name").value("Juan Perez"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Juan Perez')].email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Juan Perez')].bio").value("Experto en Spring Boot"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Maria Garcia')].name").value("Maria Garcia"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Maria Garcia')].email").value("maria.garcia@example.com"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Maria Garcia')].bio").value("Arquitecta en software"));

        verify(eventService, times(1)).findById(1L);
        verify(eventMapper, times(1)).toResponseDto(eventEntity);
    }

    @Test 
    @DisplayName("GET /api/v1/events/{id} - Debe retornar 404 Not Found cuando el evento no existe")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void shouldReturnNotFoundException() throws Exception {
        when(eventService.findById(anyLong())).thenThrow(
                new ResourceNotFoundException("Event no encontrado con id: 99")
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Event no encontrado con id: 99"));

        verify(eventService, times(1)).findById(99L);
        verify(eventMapper, never()).toResponseDto(any(Event.class));
    }

    @Test
    @DisplayName("GET /api/v1/events - Debe retornar todos los eventos paginados y filtrados")
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturnAllEventsPagedAndFiltered() throws Exception {
        SpeakerResponseDto speakerResponseDtoUno = new SpeakerResponseDto(
                20L, "Maria Perez", "Maria@example.com", "Bio of Speaker 2", null, null
        );
        
        Set<SpeakerResponseDto> listSpeakerResponseDto = new HashSet<>(Set.of(speakerResponseDtoUno));

        EventResponseDto eventResponseDtoDos = new EventResponseDto();
        eventResponseDtoDos.setId(1L);
        eventResponseDtoDos.setName("Webinar de Spring Security");
        eventResponseDtoDos.setDate(LocalDate.of(2026,10,15));
        eventResponseDtoDos.setLocation("Virtual");
        eventResponseDtoDos.setCategoryName("Tecnología");
        eventResponseDtoDos.setCategoryId(10L);
        eventResponseDtoDos.setSpeakers(listSpeakerResponseDto);

        EventResponseDto eventResponseDtoTres = new EventResponseDto();
        eventResponseDtoTres.setId(3L);
        eventResponseDtoTres.setName("Conferencia Cloud Native");
        eventResponseDtoTres.setDate(LocalDate.of(2026,10,15));
        eventResponseDtoTres.setLocation("Auditorio Principal");
        eventResponseDtoTres.setCategoryName("Tecnología");
        eventResponseDtoTres.setCategoryId(10L);
        eventResponseDtoTres.setSpeakers(listSpeakerResponseDto);

        List<EventResponseDto> eventResponseList = List.of(eventResponseDtoDos, eventResponseDtoTres);
        Pageable pageableMock = PageRequest.of(0, 10);
        Page<EventResponseDto> eventResponseDtoPage = new PageImpl<>(eventResponseList, pageableMock, eventResponseList.size());
        
        when(eventService.findAll(eq("Spring"), any(Pageable.class))).thenReturn(eventResponseDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events")
                .param("page", "0")
                .param("size", "10")
                .param("name", "Spring")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").doesNotExist())

                // Primer elemento
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Webinar de Spring Security"))
                .andExpect(jsonPath("$.content[0].date").value("2026-10-15"))
                .andExpect(jsonPath("$.content[0].location").value("Virtual"))
                .andExpect(jsonPath("$.content[0].categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.content[0].speakers.length()").value(1))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name =='Maria Perez')].id").value(20))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name =='Maria Perez')].name").value("Maria Perez"))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name =='Maria Perez')].email").value("Maria@example.com"))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name =='Maria Perez')].bio").value("Bio of Speaker 2"))

                // Segundo elemento
                .andExpect(jsonPath("$.content[1].id").value(3))
                .andExpect(jsonPath("$.content[1].name").value("Conferencia Cloud Native"))
                .andExpect(jsonPath("$.content[1].date").value("2026-10-15"))
                .andExpect(jsonPath("$.content[1].location").value("Auditorio Principal"))
                .andExpect(jsonPath("$.content[1].categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.content[1].speakers.length()").value(1))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name =='Maria Perez')].id").value(20))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name =='Maria Perez')].name").value("Maria Perez"))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name =='Maria Perez')].email").value("Maria@example.com"))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name =='Maria Perez')].bio").value("Bio of Speaker 2"))

                // Metadatos de la paginación corregidos
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        verify(eventService, times(1)).findAll(eq("Spring"), any(Pageable.class));
        verify(eventService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("POST /api/v1/events - Debe crear un evento y retornar 201 Created")
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void shouldCreateEventSuccessfully() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto();
        eventRequestDto.setName("Taller de Microservicios");
        eventRequestDto.setDate(LocalDate.of(2025, 3, 10));
        eventRequestDto.setLocation("Centro de Convenciones");
        eventRequestDto.setCategoryId(10L);
        eventRequestDto.setSpeakerIds(Set.of(20L, 21L)); 

        Event savedEventEntity = new Event();
        savedEventEntity.setId(5L); 
        savedEventEntity.setName("Taller de Microservicios");
        savedEventEntity.setDate(LocalDate.of(2025, 3, 10));
        savedEventEntity.setLocation("Centro de Convenciones");

        Category categoryForSavedEvent = new Category(10L, "Tecnología", "Eventos tecnológicos");
        Speaker speaker1ForSavedEvent = new Speaker(20L, "Juan Pérez", "juan.perez@example.com", "Experto en Spring Boot.", new HashSet<>());
        Speaker speaker2ForSavedEvent = new Speaker(21L, "María García", "maria.garcia@example.com", "Arquitecta de software.", new HashSet<>());
        savedEventEntity.setCategory(categoryForSavedEvent);
        savedEventEntity.addSpeakers(speaker1ForSavedEvent);
        savedEventEntity.addSpeakers(speaker2ForSavedEvent);

        EventResponseDto createdEventResponseDto = new EventResponseDto();
        createdEventResponseDto.setId(5L);
        createdEventResponseDto.setName("Taller de Microservicios");
        createdEventResponseDto.setDate(LocalDate.of(2025, 3, 10));
        createdEventResponseDto.setLocation("Centro de Convenciones");
        createdEventResponseDto.setCategoryName("Tecnología");
        createdEventResponseDto.setCategoryId(10L);

        SpeakerResponseDto speakerResponse1 = new SpeakerResponseDto(20L, "Juan Pérez", "juan.perez@example.com", "Experto en Spring Boot.", null, null);
        SpeakerResponseDto speakerResponse2 = new SpeakerResponseDto(21L, "María García", "maria.garcia@example.com", "Arquitecta de software.", null, null);
        Set<SpeakerResponseDto> speakersDto = new HashSet<>();
        speakersDto.add(speakerResponse1);
        speakersDto.add(speakerResponse2);
        createdEventResponseDto.setSpeakers(speakersDto);

        when(eventService.save(any(EventRequestDto.class))).thenReturn(savedEventEntity);
        when(eventMapper.toResponseDto(savedEventEntity)).thenReturn(createdEventResponseDto);

        mockMvc.perform(post("/api/v1/events") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Taller de Microservicios"))
                .andExpect(jsonPath("$.date").value("2025-03-10"))
                .andExpect(jsonPath("$.location").value("Centro de Convenciones"))
                .andExpect(jsonPath("$.categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.categoryId").value(10))
                .andExpect(jsonPath("$.speakers.length()").value(2))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Juan Pérez')].id").value(20))
                .andExpect(jsonPath("$.speakers[?(@.name == 'María García')].id").value(21));
                
        verify(eventService, times(1)).save(any(EventRequestDto.class));
        verify(eventMapper, times(1)).toResponseDto(savedEventEntity); 
        verify(eventService, never()).findAll(anyString(), any(Pageable.class));
        verify(eventService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("PUT /api/v1/events/{id} - Debe actualizar un evento existente y retornar 200 OK")
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void shouldUpdateEventSuccessfully() throws Exception {
        final Long eventIdToUpdate = 1L;

        EventRequestDto eventToUpdateRequestDto = new EventRequestDto();
        eventToUpdateRequestDto.setName("Conferencia Tech V2.0 (Actualizado)");
        eventToUpdateRequestDto.setDate(LocalDate.of(2025, 1, 15));
        eventToUpdateRequestDto.setLocation("Centro de Convenciones Principal");
        eventToUpdateRequestDto.setCategoryId(11L);
        eventToUpdateRequestDto.setSpeakerIds(Set.of(22L));

        Event updatedEventEntity = new Event();
        updatedEventEntity.setId(eventIdToUpdate);
        updatedEventEntity.setName("Conferencia Tech V2.0 (Actualizado)");
        updatedEventEntity.setDate(LocalDate.of(2025, 1, 15));
        updatedEventEntity.setLocation("Centro de Convenciones Principal");
        
        Category newCategory = new Category(11L, "Desarrollo de Software", "Eventos de desarrollo");
        Speaker newSpeaker = new Speaker(22L, "Carlos Lopez", "carlos@example.com", "Experto en Frontend", new HashSet<>());
        updatedEventEntity.setCategory(newCategory);
        updatedEventEntity.setSpeakers(Set.of(newSpeaker));

        EventResponseDto eventUpdatedResponseDto = new EventResponseDto();
        eventUpdatedResponseDto.setId(1L);
        eventUpdatedResponseDto.setName("Conferencia Tech V2.0 (Actualizado)");
        eventUpdatedResponseDto.setDate(LocalDate.of(2025, 1, 15));
        eventUpdatedResponseDto.setLocation("Centro de Convenciones Principal");
        eventUpdatedResponseDto.setCategoryId(11L);
        eventUpdatedResponseDto.setCategoryName("Desarrollo de Software");
        eventUpdatedResponseDto.setSpeakers(Set.of(new SpeakerResponseDto(22L, "Carlos Lopez", "carlos@example.com", "Experto en Frontend", null, null)));

        when(eventService.update(eq(eventIdToUpdate), any(EventRequestDto.class))).thenReturn(updatedEventEntity);
        when(eventMapper.toResponseDto(updatedEventEntity)).thenReturn(eventUpdatedResponseDto);

        mockMvc.perform(put("/api/v1/events/{id}", eventIdToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventToUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventIdToUpdate))
                .andExpect(jsonPath("$.name").value("Conferencia Tech V2.0 (Actualizado)"))
                .andExpect(jsonPath("$.location").value("Centro de Convenciones Principal"))
                .andExpect(jsonPath("$.date").value("2025-01-15"))
                .andExpect(jsonPath("$.categoryId").value(11L))
                .andExpect(jsonPath("$.categoryName").value("Desarrollo de Software"))
                .andExpect(jsonPath("$.speakers.length()").value(1)) // <-- Corregido "lenght" por "length"
                .andExpect(jsonPath("$.speakers[0].id").value(22))
                .andExpect(jsonPath("$.speakers[0].name").value("Carlos Lopez")); // <-- Sintaxis corregida

        verify(eventService, times(1)).update(eq(eventIdToUpdate), any(EventRequestDto.class));
        verify(eventMapper, times(1)).toResponseDto(updatedEventEntity);
        verify(eventService, never()).save(any(EventRequestDto.class));
        verify(eventService, never()).findAll(anyString(), any(Pageable.class));
        verify(eventService, never()).findById(anyLong());
    }

    @Test 
    @DisplayName("DELETE /api/v1/events/{id} - Debe eliminar un evento y retornar 204 No Content")
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void shouldDeleteEventSuccessfully() throws Exception {
        final Long eventIdToDelete = 1L;

        doNothing().when(eventService).deleteById(eventIdToDelete);

        mockMvc.perform(delete("/api/v1/events/{id}", eventIdToDelete))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteById(eventIdToDelete);
        verify(eventMapper, never()).toResponseDto(any(Event.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/events/{id} - Debe retornar 404 Not Found si el evento a eliminar no existe")
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentEvent() throws Exception {
        final Long nonExistentEventId = 999L;

        doThrow(new ResourceNotFoundException("Evento no encontrado con ID: " + nonExistentEventId))
                .when(eventService).deleteById(nonExistentEventId);

        mockMvc.perform(delete("/api/v1/events/{id}", nonExistentEventId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Evento no encontrado con ID: " + nonExistentEventId));

        verify(eventService, times(1)).deleteById(nonExistentEventId);
        verify(eventMapper, never()).toResponseDto(any(Event.class));
    }
}