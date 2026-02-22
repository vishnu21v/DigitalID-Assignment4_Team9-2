package com.group9.digitalid;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 5 test cases for addPerson():
 * 1. Valid person (ID, address, birthdate all correct) - expect true
 * 2. Invalid ID length (9 chars) - expect false
 * 3. Invalid ID (only 1 special char in middle) - expect false
 * 4. Invalid address (State not Victoria) - expect false
 * 5. Invalid birthdate (YYYY-MM-DD instead of DD-MM-YYYY) - expect false
 */
class PersonAddPersonTest {

    private Path tmpFile;

    @BeforeEach
    void setup() throws IOException {
        // Preparing temporary file to ensure clean environment for every test
        tmpFile = Files.createTempFile("persons_", ".txt");
    }

    @AfterEach
    void cleanup() throws IOException {
        // removing temp file so tests dont affect each other
        if (tmpFile != null && Files.exists(tmpFile))
            Files.deleteIfExists(tmpFile);
    }

    @Test
    void validPersonReturnsTrue() {
        // Adding valid person to file should return successful true result
        Person p = new Person("56s_d%&fAB", "John", "Smith",
                "32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addPerson(tmpFile.toString()));
    }

    @Test
    void wrongIdLength() {
        // Validating addPerson prevents saving person when ID length invalid
        Person p = new Person("56s_d%&fA", "Jane", "Doe",
                "10|Main Street|Melbourne|Victoria|Australia", "01-05-1985");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void idNeedsTwoSpecialChars() {
        // Testing rejection when person ID does not contain two special characters
        Person p = new Person("56a_b123AB", "Alice", "Brown",
                "5|Oak Avenue|Melbourne|Victoria|Australia", "20-03-1992");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void addressMustBeVictoria() {
        // Checking system enforces Victoria as required state for address
        Person p = new Person("78x@y#z!CD", "Chris", "Lee",
                "15|George Street|Sydney|NSW|Australia", "25-07-1995");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void birthdateFormat() {
        // Ensuring method returns false for person with invalid birthdate format
        Person p = new Person("56p_q!r@GH", "Eve", "Taylor",
                "22|River Road|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(p.addPerson(tmpFile.toString()));
    }
    
    @Test
    void duplicateIdReturnsFalse() {
        // Checking system enforces unique person IDs for every record
        Person p1 = new Person("56s_d%&fAB", "John", "Smith",
            "32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p1.addPerson(tmpFile.toString()));

        Person p2 = new Person("56s_d%&fAB", "Jane", "Doe",
            "10|Main Street|Melbourne|Victoria|Australia", "01-05-1985");
        assertFalse(p2.addPerson(tmpFile.toString()));
    }

    @Test
    void missingFirstNameReturnsFalse() {
        // Validating person cannot be added when first name is absent
        Person p = new Person("78x@y#z!CD", null, "Lee",
            "15|George Street|Melbourne|Victoria|Australia", "25-07-1995");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void missingLastNameReturnsFalse() {
        // Testing rejection when last name is missing for person addition
        Person p = new Person("78x@y#z!CD", "Chris", null,
            "15|George Street|Melbourne|Victoria|Australia", "25-07-1995");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void addressWrongNumberOfFieldsReturnsFalse() {
        // Blocking person record creation when address field count is wrong
        Person p = new Person("78x@y#z!CD", "Chris", "Lee",
            "15|George Street|Melbourne|Victoria", "25-07-1995");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    @Test
    void emptyBirthdateReturnsFalse() {
        // Preventing save operation for person with missing birthdate information
        Person p = new Person("78x@y#z!CD", "Chris", "Lee",
            "15|George Street|Melbourne|Victoria|Australia", "");
        assertFalse(p.addPerson(tmpFile.toString()));
    }

    
}
