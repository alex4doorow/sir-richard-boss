package ru.sir.richard.boss.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sir.richard.boss.web.entity.WsRole;
import ru.sir.richard.boss.web.entity.WsUser;
import ru.sir.richard.boss.web.repository.WsRoleRepository;
import ru.sir.richard.boss.web.repository.WsUserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    WsUserRepository wsUserRepository;

    @Autowired
    WsRoleRepository wsRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WsUser user = wsUserRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public WsUser findUserById(Long userId) {
        Optional<WsUser> userFromDb = wsUserRepository.findById(userId);
        return userFromDb.orElse(new WsUser());
    }

    public List<WsUser> allUsers() {
        return wsUserRepository.findAll();
    }

    public boolean saveUser(WsUser user) {
        WsUser userFromDB = wsUserRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setRoles(Collections.singleton(new WsRole(1L, "ROLE_USER")));
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

    public List<WsUser> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM WsUser u WHERE u.id > :paramId", WsUser.class)
                .setParameter("paramId", idMin).getResultList();
    }
}
