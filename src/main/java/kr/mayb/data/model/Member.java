package kr.mayb.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kr.mayb.enums.AccountStatus;
import kr.mayb.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class Member extends BaseEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String profileUrl;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private LocalDate birthdate;

    @Column
    private String occupation; 

    @Column
    private String contact;

    @Column
    private String introduction;

    @Column
    private String idealType;


    @JsonIgnore
    @BatchSize(size = 10)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "mayb",
            name = "rel_member_authority",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
