package org.example.hhplusconcertreservationservice.users.infrastructure;

import org.example.hhplusconcertreservationservice.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}