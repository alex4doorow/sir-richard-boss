package ru.sir.richard.boss.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sir.richard.boss.web.entity.WsRole;

@Repository
public interface WsRoleRepository extends JpaRepository<WsRole, Long> {
}

