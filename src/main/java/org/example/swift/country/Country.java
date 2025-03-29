package org.example.swift.country;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.swift.bank.Bank;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Country {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String iso2Code;

    @OneToMany(mappedBy = "country")
    @ToString.Exclude
    private Set<Bank> banks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country country)) return false;
        return Objects.equals(name, country.name) && Objects.equals(iso2Code, country.iso2Code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, iso2Code);
    }
}
