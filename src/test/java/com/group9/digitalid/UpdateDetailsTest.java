package com.group9.digitalid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateDetailsTest {

    private static final String TEST_FILE = "persons.txt";
    private PersonManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        // Setting up required test data file before executing each test
        manager = new PersonManager();

        List<String> testData = Arrays.asList(
                "56s_d%&fAB|John|Smith|32|Highland Street|Melbourne|Victoria|Australia|15-11-1990",
                "22s_d%&fXY|Kid|User|1|Short Street|Melbourne|Victoria|Australia|01-01-2010"
        );

        Files.write(Paths.get(TEST_FILE), testData);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Removing leftover test data after each run ensuring consistent results
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    public void testStandardValidUpdate() {
        // checking that an adult can update name and address
        boolean result = manager.updatePersonalDetails(
                "56s_d%&fAB",
                "56s_d%&fAB",
                "John",
                "Doe",
                "99|King Street|Melbourne|Victoria|Australia",
                "15-11-1990"
        );
        assertTrue(result);
    }

    @Test
    public void testEvenIDChangeFails() throws IOException {
        // preparing a record that starts with an even digit so ID change must be blocked
        List<String> testData = Arrays.asList(
                "24s_d%&fCD|Alice|Lee|10|Main Street|Melbourne|Victoria|Australia|15-05-1985"
        );
        Files.write(Paths.get(TEST_FILE), testData);

        boolean result = manager.updatePersonalDetails(
                "24s_d%&fCD",
                "34s_d%&fCD",
                "Alice",
                "Lee",
                "10|Main Street|Melbourne|Victoria|Australia",
                "15-05-1985"
        );
        assertFalse(result);
    }

    @Test
    public void testMinorAddressChangeFails() {
        // checking that under 18 cannot change address
        boolean result = manager.updatePersonalDetails(
                "22s_d%&fXY",
                "22s_d%&fXY",
                "Kid",
                "User",
                "2|Other Street|Melbourne|Victoria|Australia",
                "01-01-2010"
        );
        assertFalse(result);
    }

    @Test
    public void testBirthdayChangeSuccess() {
        // checking DOB-only update is allowed when everything else stays the same
        boolean result = manager.updatePersonalDetails(
                "56s_d%&fAB",
                "56s_d%&fAB",
                "John",
                "Smith",
                "32|Highland Street|Melbourne|Victoria|Australia",
                "16-11-1990"
        );
        assertTrue(result);
    }

    @Test
    public void testBirthdayWithOtherChangeFails() {
        // checking DOB change plus name change is rejected
        boolean result = manager.updatePersonalDetails(
                "56s_d%&fAB",
                "56s_d%&fAB",
                "Johnny",
                "Smith",
                "32|Highland Street|Melbourne|Victoria|Australia",
                "16-11-1990"
        );
        assertFalse(result);
    }
}