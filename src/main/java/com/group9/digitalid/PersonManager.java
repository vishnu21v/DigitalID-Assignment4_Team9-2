package com.group9.digitalid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PersonManager {

    private static final String FILENAME = "persons.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public boolean updatePersonalDetails(String oldID,
                                        String newID,
                                        String newFirstName,
                                        String newLastName,
                                        String newAddress,
                                        String newBirthdate) 
                                         {

        if (oldID == null || newID == null || newFirstName == null || newLastName == null
                || newAddress == null || newBirthdate == null) {
            return false;
        }

        Path path = Paths.get(FILENAME);

        try {
            if (!Files.exists(path)) return false;

            List<String> lines = Files.readAllLines(path);

            int foundIndex = -1;
            String foundLine = null;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(oldID + "|")) {
                    foundIndex = i;
                    foundLine = lines.get(i);
                    break;
                }
            }

            if (foundIndex == -1) return false;

            String[] parts = foundLine.split("\\|", -1);
            if (parts.length != 9) return false;

            String currentID = parts[0];
            String currentFirstName = parts[1];
            String currentLastName = parts[2];

            String currentAddress = parts[3] + "|" + parts[4] + "|" + parts[5] + "|" + parts[6] + "|" + parts[7];
            String currentBirthdate = parts[8];

            // checking addPerson rules for updated values
            if (!isValidPersonID(newID)) return false;
            if (!isValidAddress(newAddress)) return false;
            if (!isValidBirthdate(newBirthdate)) return false;

            // checking Condition 1: If under 18, address cannot change
            int ageNow = calculateAge(currentBirthdate, LocalDate.now());
            if (ageNow < 18) {
                if (!currentAddress.trim().equals(newAddress.trim())) return false;
            }

            // checking Condition 2: If birthday is changed, no other fields can change
            boolean birthdayChanged = !newBirthdate.trim().equals(currentBirthdate.trim());
            if (birthdayChanged) {
                if (!newID.trim().equals(currentID.trim())) return false;
                if (!newFirstName.trim().equals(currentFirstName.trim())) return false;
                if (!newLastName.trim().equals(currentLastName.trim())) return false;
                if (!newAddress.trim().equals(currentAddress.trim())) return false;
            }

            // checking Condition 3: If first digit of current ID is even, ID cannot change
            char firstChar = currentID.charAt(0);
            if (firstChar == '2' || firstChar == '4' || firstChar == '6' || firstChar == '8') {
                if (!newID.trim().equals(currentID.trim())) return false;
            }

            // checking duplicate ID if changing ID
            boolean idChanging = !newID.trim().equals(currentID.trim());
            if (idChanging) {
                if (idExists(path, newID.trim())) return false;
            }

            String[] addrParts = newAddress.trim().split("\\|", -1);
            if (addrParts.length != 5) return false;

            String updatedLine = newID.trim() + "|" + newFirstName.trim() + "|" + newLastName.trim()
                + "|" + addrParts[0].trim()
                + "|" + addrParts[1].trim()
                + "|" + addrParts[2].trim()
                + "|" + addrParts[3].trim()
                + "|" + addrParts[4].trim()
                + "|" + newBirthdate.trim();

            lines.set(foundIndex, updatedLine);
            Files.write(path, lines);

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private boolean idExists(Path path, String id) throws IOException {
        // Checking whether given ID already exists in specified file
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length >= 1 && parts[0].trim().equals(id)) return true;
        }
        return false;
    }

    private boolean isValidPersonID(String id) {
        // Verifying last two characters are uppercase letters as expected
        if (id == null || id.length() != 10) return false;

        for (int i = 0; i < 2; i++) {
            char c = id.charAt(i);
            if (c < '2' || c > '9') return false;
        }

        String mid = id.substring(2, 8);
        int special = 0;
        for (int i = 0; i < mid.length(); i++) {
            if (!Character.isLetterOrDigit(mid.charAt(i))) special++;
        }
        if (special < 2) return false;

        for (int i = 8; i < 10; i++) {
            char c = id.charAt(i);
            if (c < 'A' || c > 'Z') return false;
        }

        return true;
    }

    private boolean isValidAddress(String addr) {
        // Returning true only if address format and state are correct
        if (addr == null || addr.isEmpty()) return false;
        String[] p = addr.split("\\|", -1);
        if (p.length != 5) return false;
        return "Victoria".equals(p[3].trim());
    }

    private boolean isValidBirthdate(String d) {
        // Ensuring method rejects birthdate values that cannot be parsed correctly
        if (d == null || d.isEmpty()) return false;
        try {
            LocalDate.parse(d.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private int calculateAge(String birthdate, LocalDate onDate) {
        // Parsing birthdate string into LocalDate using predefined formatter
        LocalDate dob = LocalDate.parse(birthdate.trim(), DATE_FORMATTER);
        return Period.between(dob, onDate).getYears();
    }
}