package com.group9.digitalid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

class PersonAddIDTest {

    private Path tmpIdsFile;

    @BeforeEach
    void setup() throws IOException {
        // Generating new temporary ID storage file prior to test execution
        tmpIdsFile = Files.createTempFile("ids_", ".txt");
    }

    @AfterEach
    void cleanup() throws IOException {
        // Removing temporary IDs storage to prevent interference between test runs
        if (tmpIdsFile != null) Files.deleteIfExists(tmpIdsFile);
    }

    @Test
    void validPassportReturnsTrue() {
        // Recording correct passport number for person in ID file
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertTrue(p.addID("passport", "AB123456", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void invalidPassportLengthReturnsFalse() {
        // Refusing passport ID addition when number of characters is incorrect
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertFalse(p.addID("passport", "AB12345", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void validDriversLicenceReturnsTrue() {
        // Saving valid drivers licence information into temporary ID file
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertTrue(p.addID("drivers licence", "AB12345678", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void invalidMedicareNonDigitReturnsFalse() {
        // Blocking Medicare ID registration due to non-numeric input value
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertFalse(p.addID("medicare", "12345A789", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void studentCardAllowedForUnder18() {
    // checking allowing student card for under 18
        Person under18 = new Person("22s_d%&fXY", "01-01-2010");
        assertTrue(under18.addID("student card", "123456789012",
                tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void studentCardBlockedForOver18() {
    // checking blocking student card for 18 and above
        Person over18 = new Person("56s_d%&fAB", "01-01-1990");
        assertFalse(over18.addID("student card", "123456789012",
                tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }
}