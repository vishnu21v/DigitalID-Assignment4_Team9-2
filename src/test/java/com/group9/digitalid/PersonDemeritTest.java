package com.group9.digitalid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for Person demerit points functionality.
 * Covers validation, boundary conditions, suspension logic and file writing.
 */
class PersonDemeritTest {

    @BeforeEach
    void setUp() throws IOException {
        // deleting runtime file before each test to prevent cross-test interference
        Files.deleteIfExists(Paths.get("demeritPoints.txt"));
    }

    @AfterEach
    void tearDown() throws IOException {
        // cleaning up runtime file after each test
        Files.deleteIfExists(Paths.get("demeritPoints.txt"));
    }

    @Test
    void testValidDemeritAddition() {
        // Verifying successful demerit point addition using valid input values
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Success",
                p.addDemeritPoints("10-02-2026", 3),
                "Should succeed with valid inputs");
    }

    @Test
    void testInvalidDateFormat() {
        // Verifying method rejects invalid date format during demerit addition
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("10/02/2026", 3),
                "Should fail due to invalid date format");
    }

    @Test
    void testInvalidDateFormatReversed() {
        // Ensuring incorrect reversed date format results in failed response
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("2026-02-10", 3),
                "Should fail due to incorrect date format");
    }

    @Test
    void testEmptyDateString() {
        // Checking validation logic for missing date value input
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("", 3),
                "Should fail with empty date string");
    }

    @Test
    void testPointsOutsideRange() {
        // Verifying method rejects addition of points outside valid range
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("10-02-2026", 7),
                "Should fail because points exceed 6");
    }

    @Test
    void testPointsBelowValidRange() {
        // Confirming system handles below-range demerit points appropriately
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("10-02-2026", 0),
                "Should fail because points are below minimum of 1");
    }

    @Test
    void testNegativePoints() {
        // Testing failure when demerit points are negative and invalid
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertEquals("Failed",
                p.addDemeritPoints("10-02-2026", -1),
                "Should fail because points cannot be negative");
    }

    @Test
    void testUnder21SuspensionTrigger() {
        // Checking suspension logic for persons under twenty-one years old
        Person p = new Person("22s_d%&fXY", "01-01-2008");

        p.addDemeritPoints("01-01-2026", 4);
        p.addDemeritPoints("02-01-2026", 3); // total = 7

        assertTrue(p.getIsSuspended(),
                "Under 21 should be suspended if total points > 6");
    }

    @Test
    void testUnder21ExactlyAtThresholdNotSuspended() {
        // Verifying no suspension occurs exactly at maximum allowed points
        Person p = new Person("22s_d%&fXY", "01-01-2008");

        p.addDemeritPoints("01-01-2026", 6); // total = 6

        assertFalse(p.getIsSuspended(),
                "Under 21 should NOT be suspended with exactly 6 points");
    }

    @Test
    void testOver21SuspensionTrigger() {
        // Confirming system suspends over-21 individuals exceeding maximum demerit points
        Person p = new Person("56s_d%&fAB", "01-01-1990");

        p.addDemeritPoints("01-01-2026", 6);
        p.addDemeritPoints("02-01-2026", 6);
        p.addDemeritPoints("03-01-2026", 1); // total = 13

        assertTrue(p.getIsSuspended(),
                "Over 21 should be suspended if total points > 12");
    }

    @Test
    void testOver21ExactlyAtThresholdNotSuspended() {
        // Testing over-21 person not suspended when points equal threshold
        Person p = new Person("56s_d%&fAB", "01-01-1990");

        p.addDemeritPoints("01-01-2026", 6);
        p.addDemeritPoints("02-01-2026", 6); // total = 12

        assertFalse(p.getIsSuspended(),
                "Over 21 should NOT be suspended with exactly 12 points");
    }

    @Test
    void testDemeritPointsWrittenToFile() throws IOException {
        //  Storing demerit points data correctly in the designated text file
        Person p = new Person("56s_d%&fAB", "15-11-1990");

        p.addDemeritPoints("10-02-2026", 3);

        assertTrue(Files.exists(Paths.get("demeritPoints.txt")),
                "File should be created");

        String content = Files.readString(Paths.get("demeritPoints.txt"));

        assertTrue(content.contains("56s_d%&fAB"),
                "File should contain person ID");

        assertTrue(content.contains("10-02-2026"),
                "File should contain offense date");
    }
}