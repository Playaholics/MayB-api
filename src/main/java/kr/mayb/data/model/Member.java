package kr.mayb.data.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.mayb.enums.Gender;

@Getter
@Setter
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
}
