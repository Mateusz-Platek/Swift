package org.example.swift.bank;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.swift.country.Country;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Bank {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String swiftCode;

    private String address;

    @Column(nullable = false)
    private boolean isHeadquarter;

    @ManyToOne
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="country_id")
    private Country country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bank bank)) return false;
        return isHeadquarter == bank.isHeadquarter && Objects.equals(name, bank.name) &&
                Objects.equals(swiftCode, bank.swiftCode) && Objects.equals(address, bank.address)
                && Objects.equals(country, bank.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, swiftCode, address, isHeadquarter, country);
    }
}
