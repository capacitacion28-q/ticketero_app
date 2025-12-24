package com.example.ticketero.service;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AdvisorService
 * Cubre asignación automática, balanceo de carga y liberación de asesores
 */
@ExtendWith(MockitoExtension.class)
class AdvisorServiceTest {

    @Mock
    private AdvisorRepository advisorRepository;
    
    @Mock
    private TicketRepository ticketRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private AdvisorService advisorService;

    @Test
    void assignNextTicket_withAvailableAdvisorAndTicket_shouldAssign() {
        // Given
        Advisor advisor = TestDataBuilder.advisorAvailable().build();
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.of(advisor));
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(List.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        
        // When
        Optional<Ticket> result = advisorService.assignNextTicket();
        
        // Then
        assertThat(result).isPresent();
        
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        ArgumentCaptor<Advisor> advisorCaptor = ArgumentCaptor.forClass(Advisor.class);
        
        verify(ticketRepository).save(ticketCaptor.capture());
        verify(advisorRepository).save(advisorCaptor.capture());
        
        Ticket savedTicket = ticketCaptor.getValue();
        Advisor savedAdvisor = advisorCaptor.getValue();
        
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.CALLED);
        assertThat(savedTicket.getAssignedAdvisor()).isEqualTo("María López");
        assertThat(savedTicket.getAssignedModuleNumber()).isEqualTo(1);
        
        assertThat(savedAdvisor.getStatus()).isEqualTo(AdvisorStatus.BUSY);
        assertThat(savedAdvisor.getAssignedTicketsCount()).isEqualTo(1);
        
        verify(notificationService).sendStatusChangeNotification(any(Ticket.class), eq(TicketStatus.WAITING));
    }

    @Test
    void assignNextTicket_noAvailableAdvisors_shouldReturnEmpty() {
        // Given
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.empty());
        
        // When
        Optional<Ticket> result = advisorService.assignNextTicket();
        
        // Then
        assertThat(result).isEmpty();
        verify(ticketRepository, never()).findByStatusOrderByFechaCreacionAsc(any());
        verify(ticketRepository, never()).save(any());
        verify(advisorRepository, never()).save(any());
    }

    @Test
    void assignNextTicket_noWaitingTickets_shouldReturnEmpty() {
        // Given
        Advisor advisor = TestDataBuilder.advisorAvailable().build();
        
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.of(advisor));
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(Collections.emptyList());
        
        // When
        Optional<Ticket> result = advisorService.assignNextTicket();
        
        // Then
        assertThat(result).isEmpty();
        verify(ticketRepository, never()).save(any());
        verify(advisorRepository, never()).save(any());
    }

    @Test
    void assignTicketToAdvisor_shouldUpdateBothEntities() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        Advisor advisor = TestDataBuilder.advisorAvailable().build();
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        
        // When
        Optional<Ticket> result = advisorService.assignTicketToAdvisor(ticket, advisor);
        
        // Then
        assertThat(result).isPresent();
        
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        ArgumentCaptor<Advisor> advisorCaptor = ArgumentCaptor.forClass(Advisor.class);
        
        verify(ticketRepository).save(ticketCaptor.capture());
        verify(advisorRepository).save(advisorCaptor.capture());
        
        Ticket savedTicket = ticketCaptor.getValue();
        Advisor savedAdvisor = advisorCaptor.getValue();
        
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.CALLED);
        assertThat(savedTicket.getAssignedAdvisor()).isEqualTo("María López");
        assertThat(savedTicket.getAssignedModuleNumber()).isEqualTo(1);
        assertThat(savedTicket.getFechaActualizacion()).isNotNull();
        
        assertThat(savedAdvisor.getStatus()).isEqualTo(AdvisorStatus.BUSY);
        assertThat(savedAdvisor.getAssignedTicketsCount()).isEqualTo(1);
        assertThat(savedAdvisor.getUpdatedAt()).isNotNull();
        
        verify(notificationService).sendStatusChangeNotification(ticket, TicketStatus.WAITING);
    }

    @Test
    void completeTicket_shouldCompleteAndReleaseAdvisor() {
        // Given
        String ticketNumber = "C001";
        Ticket ticket = TestDataBuilder.ticketInService()
            .numero(ticketNumber)
            .assignedAdvisor("María López")
            .build();
        Advisor advisor = TestDataBuilder.advisorBusy()
            .email("María López@banco.com")
            .assignedTicketsCount(1)
            .build();
        
        when(ticketRepository.findByNumero(ticketNumber)).thenReturn(Optional.of(ticket));
        when(advisorRepository.findByEmail("María López@banco.com")).thenReturn(Optional.of(advisor));
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.empty()); // No next assignment
        
        // When
        advisorService.completeTicket(ticketNumber);
        
        // Then
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        ArgumentCaptor<Advisor> advisorCaptor = ArgumentCaptor.forClass(Advisor.class);
        
        verify(ticketRepository).save(ticketCaptor.capture());
        verify(advisorRepository).save(advisorCaptor.capture());
        
        Ticket savedTicket = ticketCaptor.getValue();
        Advisor savedAdvisor = advisorCaptor.getValue();
        
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.COMPLETED);
        assertThat(savedTicket.getFechaActualizacion()).isNotNull();
        
        assertThat(savedAdvisor.getStatus()).isEqualTo(AdvisorStatus.AVAILABLE);
        assertThat(savedAdvisor.getAssignedTicketsCount()).isEqualTo(0);
        assertThat(savedAdvisor.getUpdatedAt()).isNotNull();
    }

    @Test
    void completeTicket_withNonExistentTicket_shouldThrowException() {
        // Given
        String ticketNumber = "NONEXISTENT";
        
        when(ticketRepository.findByNumero(ticketNumber)).thenReturn(Optional.empty());
        
        // When + Then
        assertThatThrownBy(() -> advisorService.completeTicket(ticketNumber))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Ticket not found: NONEXISTENT");
        
        verify(advisorRepository, never()).save(any());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void getAvailableAdvisors_shouldReturnOrderedByLoad() {
        // Given
        Advisor advisor1 = TestDataBuilder.advisorAvailable().assignedTicketsCount(0).build();
        Advisor advisor2 = TestDataBuilder.advisorAvailable().assignedTicketsCount(2).build();
        List<Advisor> advisors = Arrays.asList(advisor1, advisor2);
        
        when(advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(advisors);
        
        // When
        List<Advisor> result = advisorService.getAvailableAdvisors();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAssignedTicketsCount()).isEqualTo(0);
        assertThat(result.get(1).getAssignedTicketsCount()).isEqualTo(2);
    }
}