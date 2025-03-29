package org.example.swift.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findBankBySwiftCodeIgnoreCase(String swiftCode);
    List<Bank> findBanksBySwiftCodeContainingIgnoreCase(String swiftCode);
    @Transactional
    long deleteBankBySwiftCodeIgnoreCase(String swiftCode);
}
