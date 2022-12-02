package ru.sir.richard.boss.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sir.richard.boss.web.entity.WsUser;

@Repository
public interface WsUserRepository extends JpaRepository<WsUser, Long> {
    WsUser findByUsername(String username);
}

