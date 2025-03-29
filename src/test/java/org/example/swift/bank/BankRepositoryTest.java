package org.example.swift.bank;

import org.example.swift.country.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BankRepository bankRepository;

    @Test
    void whenFindBankBySwiftCodeIgnoreCase_givenExistingSwiftCode_thenReturnBankOptional() {
        String swiftCode = "abcdabcd123";
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank = new Bank();
        bank.setName("name");
        bank.setAddress("address");
        bank.setSwiftCode("abcdabcd123");
        bank.setHeadquarter(false);
        bank.setCountry(country);
        testEntityManager.persist(bank);

        Optional<Bank> bankOptional = bankRepository.findBankBySwiftCodeIgnoreCase(swiftCode);

        assertThat(bankOptional).isNotEmpty();
        assertThat(bankOptional).hasValue(bank);
    }

    @Test
    void whenFindBanksBySwiftCodeContainingIgnoreCase_givenExistingSwiftCode_thenReturnBanks() {
        String swiftCode = "abcdabcd";
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank1 = new Bank();
        bank1.setName("name");
        bank1.setAddress("address");
        bank1.setSwiftCode("abcdabcdxxx");
        bank1.setHeadquarter(true);
        bank1.setCountry(country);
        Bank bank2 = new Bank();
        bank2.setName("name");
        bank2.setAddress("address");
        bank2.setSwiftCode("abcdabcd123");
        bank2.setHeadquarter(false);
        bank2.setCountry(country);
        Bank bank3 = new Bank();
        bank3.setName("name");
        bank3.setAddress("address");
        bank3.setSwiftCode("abcdabcd234");
        bank3.setHeadquarter(false);
        bank3.setCountry(country);
        testEntityManager.persist(bank1);
        testEntityManager.persist(bank2);
        testEntityManager.persist(bank3);

        List<Bank> banks = bankRepository.findBanksBySwiftCodeContainingIgnoreCase(swiftCode);

        assertThat(banks).hasSize(3);
    }

    @Test
    void whenDeleteBankBySwiftCodeIgnoreCase_givenExistingSwiftCode_thenDeleteBank() {
        String swiftCode = "abcdabcd123";
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank = new Bank();
        bank.setName("name");
        bank.setAddress("address");
        bank.setSwiftCode("abcdabcd123");
        bank.setHeadquarter(false);
        bank.setCountry(country);
        testEntityManager.persist(bank);

        long deleted = bankRepository.deleteBankBySwiftCodeIgnoreCase(swiftCode);

        assertThat(deleted).isEqualTo(1);
    }
}