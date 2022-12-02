package ru.sir.richard.boss.web.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sr_sys_user")
public class WsUser implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "user_name")
    @Size(min=2, message = "Не меньше 5 знаков")
    private String username;
    @Size(min=2, message = "Не меньше 5 знаков")
    private String password;
    @Transient
    private String passwordConfirm;
    //@ManyToMany(fetch = FetchType.EAGER)
    @Transient
    private Set<WsRole> roles;

    public WsUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<WsRole> result = new HashSet<WsRole>();
        result.add(new WsRole(1L, "ROLE_USER"));
        result.add(new WsRole(2L, "ROLE_ADMIN"));
        return result;
        //return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<WsRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<WsRole> roles) {
        this.roles = roles;
    }

}

