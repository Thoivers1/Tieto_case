package com.tietoevry.serverskeletonjava.repository;

import com.tietoevry.serverskeletonjava.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, String> {
}