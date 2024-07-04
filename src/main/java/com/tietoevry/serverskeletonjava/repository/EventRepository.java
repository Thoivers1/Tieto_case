package com.tietoevry.serverskeletonjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tietoevry.serverskeletonjava.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
