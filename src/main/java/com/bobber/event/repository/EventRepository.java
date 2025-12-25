package com.bobber.event.repository;

import com.bobber.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByHookIdOrderByReceivedAtDesc(UUID hookId, Pageable pageable);

}