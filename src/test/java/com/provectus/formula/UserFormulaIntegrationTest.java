// ============================================
// UserFormulaIntegrationTest.java
// ============================================
package com.provectus.formula;

import com.provectus.formula.model.User;
import com.provectus.formula.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserFormulaIntegrationTest {

    @Autowired
    private FormulaEngine engine;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        // Clear database before each test
        userRepository.deleteAll();

        // Add test users
        userRepository.save(new User("Alice", LocalDate.of(1990, 5, 15)));
        userRepository.save(new User("Bob", LocalDate.of(1985, 8, 22)));
        userRepository.save(new User("Charlie", LocalDate.of(1995, 3, 10)));
        userRepository.save(new User("Diana", LocalDate.of(1982, 12, 5)));
        userRepository.save(new User("Eve", LocalDate.of(1988, 7, 30)));
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userRepository.findAll();
        assertEquals(5, users.size());
    }

    @Test
    public void testFindOldestUserWithFormula() {
        String formula = """
            userRepository.findAll().stream()
                .min { a, b -> a.birthday.compareTo(b.birthday) }
                .get()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        User oldestUser = (User) result.getValue();
        assertEquals("Diana", oldestUser.getName());
        assertEquals(LocalDate.of(1982, 12, 5), oldestUser.getBirthday());
    }

    @Test
    public void testFindOldestUserWithParallelStream() {
        String formula = """
            userRepository.findAll().parallelStream()
                .min { a, b -> a.birthday.compareTo(b.birthday) }
                .get()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        User oldestUser = (User) result.getValue();
        assertEquals("Diana", oldestUser.getName());
    }

    @Test
    public void testFindYoungestUser() {
        String formula = """
            userRepository.findAll().stream()
                .max { a, b -> a.birthday.compareTo(b.birthday) }
                .get()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        User youngestUser = (User) result.getValue();
        assertEquals("Charlie", youngestUser.getName());
        assertEquals(LocalDate.of(1995, 3, 10), youngestUser.getBirthday());
    }

    @Test
    public void testFilterUsersBornAfter1990() {
        String formula = """
            userRepository.findAll().stream()
                .filter { it.birthday.isAfter(java.time.LocalDate.of(1990, 1, 1)) }
                .collect(java.util.stream.Collectors.toList())
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        List<User> filteredUsers = (List<User>) result.getValue();
        assertEquals(2, filteredUsers.size());
    }

    @Test
    public void testCountUsersInAgeRange() {
        String formula = """
            def now = java.time.LocalDate.now()
            userRepository.findAll().stream()
                .filter { user ->
                    def age = now.getYear() - user.birthday.getYear()
                    age >= 30 && age <= 40
                }
                .count()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertTrue(((Long) result.getValue()) > 0);
    }

    @Test
    public void testGetUserNamesAsString() {
        String formula = """
            userRepository.findAll().stream()
                .map { it.name }
                .sorted()
                .collect(java.util.stream.Collectors.joining(', '))
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals("Alice, Bob, Charlie, Diana, Eve", result.getValue());
    }

    @Test
    public void testCalculateAverageAge() {
        String formula = """
            def now = java.time.LocalDate.now()
            userRepository.findAll().stream()
                .mapToInt { now.getYear() - it.birthday.getYear() }
                .average()
                .orElse(0)
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertTrue(((Double) result.getValue()) > 0);
    }

    @Test
    public void testComplexUserAnalysisWithGroovy() {
        String formula = """
            def sortedUsers = userRepository.findAll().sort { it.birthday }
            def oldest = sortedUsers.first()
            def youngest = sortedUsers.last()
            def ageGap = youngest.birthday.getYear() - oldest.birthday.getYear()
            [oldest: oldest.name, youngest: youngest.name, ageGap: ageGap]
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> analysis = (java.util.Map<String, Object>) result.getValue();
        assertEquals("Diana", analysis.get("oldest"));
        assertEquals("Charlie", analysis.get("youngest"));
        assertEquals(13, analysis.get("ageGap"));
    }

    @Test
    public void testLoadAndExecuteGroovyFormulaFile() throws Exception {
        // Load the Groovy formula file from test resources
        File formulaFile = ResourceUtils.getFile("classpath:formulas/users_older_than_20_letter_count.groovy");
        String formula = Files.readString(formulaFile.toPath());

        // Execute the formula
        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());

        // Verify the result - should be just the total letters count
        Integer totalLetters = (Integer) result.getValue();
        assertNotNull(totalLetters);

        // Total letters in names (without spaces): Alice(5) + Bob(3) + Charlie(7) + Diana(5) + Eve(3) = 23
        assertEquals(23, totalLetters.intValue());
    }
}
