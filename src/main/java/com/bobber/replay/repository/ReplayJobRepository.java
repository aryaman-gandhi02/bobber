package com.bobber.replay.repository;

import com.bobber.replay.domain.ReplayJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReplayJobRepository extends JpaRepository<ReplayJob, UUID> {

    @Query("""
        select job
        from ReplayJob job
        join fetch job.event e
        where job.id = :id
    """)
    Optional<ReplayJob> findByIdWithEvent(UUID id);

    Page<ReplayJob> findByEventIdOrderByCreatedAtDesc(UUID eventId, Pageable pageable);

}
