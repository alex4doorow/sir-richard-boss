package ru.sir.richard.boss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sir.richard.boss.model.entity.AppUser;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByUserName(String userName);

}
