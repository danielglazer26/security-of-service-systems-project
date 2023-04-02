package bednarz.glazer.sakowicz.sso.system.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;
    @Column(nullable = false, unique = true)
    private String username;
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Roles role;

    private String secret = /*Base32.random()*/"AEBHQXB774LN5KR5";

    public Person(String username, String password, String email, Roles role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
