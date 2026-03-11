package in.bank.hdfc.auth.hybrid_auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String pan;

    @Column(nullable = false)
    private LocalDate dob;

    private boolean whatsappRegistered;

    private boolean mobileBankingRegistered;
}