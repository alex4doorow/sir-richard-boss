package ru.sir.richard.boss.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sr_sys_user")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name")
    private String userName;

    private String password;

    private String email;

    private Byte enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(id, appUser.id) && Objects.equals(userName, appUser.userName) && Objects.equals(password, appUser.password) && Objects.equals(email, appUser.email) && Objects.equals(enabled, appUser.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, email, enabled);
    }
}
