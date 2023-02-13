package ru.sir.richard.boss.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sir.richard.boss.model.entity.AppRole;
import ru.sir.richard.boss.model.entity.AppUser;
import ru.sir.richard.boss.repository.AppRoleRepository;
import ru.sir.richard.boss.repository.AppUserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    AppUserRepository wsUserRepository;

    @Autowired
    AppRoleRepository wsRoleRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = wsUserRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user = entityManager.find(AppUser.class, user.getId());
        user.setLastLogin(new Date());
        entityManager.merge(user);
        return user;
    }

    public AppUser findUserById(Long userId) {
        Optional<AppUser> userFromDb = wsUserRepository.findById(userId);
        return userFromDb.orElse(new AppUser());
    }

    public List<AppUser> allUsers() {
        return wsUserRepository.findAll();
    }

    public boolean saveUser(AppUser user) {
        AppUser userFromDB = wsUserRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }
        user.setRoles(Collections.singleton(new AppRole(1L, "ROLE_USER")));
        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        wsUserRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (wsUserRepository.findById(userId).isPresent()) {
            wsUserRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<AppUser> usergtList(Long idMin) {
        return entityManager.createQuery("SELECT u FROM AppUser u WHERE u.id > :paramId", AppUser.class)
                .setParameter("paramId", idMin).getResultList();
    }
}