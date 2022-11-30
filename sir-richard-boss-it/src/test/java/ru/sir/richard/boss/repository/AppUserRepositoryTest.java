package ru.sir.richard.boss.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sir.richard.boss.TestingWebApplication;
import ru.sir.richard.boss.model.entity.AppUser;

import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest(classes = TestingWebApplication.class)
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void firstTest() {
        Optional<AppUser> user = appUserRepository.findById(1L);
        log.debug("user: {}", user.get());
        List<AppUser> users = appUserRepository.findByUserName("user");
        log.debug("users: {}", users);

    }
}
