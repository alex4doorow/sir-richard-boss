package ru.sir.richard.boss.model.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sr_sys_role")
@Setter
@Getter
@NoArgsConstructor
@ToString
public class AppRole implements GrantedAuthority {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private Set<AppUser> users;

    public AppRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        AppRole wsRole = (AppRole) o;
        return id != null && Objects.equals(id, wsRole.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

