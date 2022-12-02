package ru.sir.richard.boss.web.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "sr_sys_role")
public class WsRole implements GrantedAuthority {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Transient
    //@ManyToMany(mappedBy = "roles")
    private Set<WsUser> users;

    public WsRole() {
    }

    public WsRole(Long id) {
        this.id = id;
    }

    public WsRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WsUser> getUsers() {
        return users;
    }

    public void setUsers(Set<WsUser> users) {
        this.users = users;
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}

