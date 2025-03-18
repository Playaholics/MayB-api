package kr.mayb.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kr.mayb.enums.AuthorityName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
@NoArgsConstructor
public class Authority extends BaseEntity {
    @Id
    private long id;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

    public Authority(long id, AuthorityName name) {
        this.id = id;
        this.name = name;
    }
}
