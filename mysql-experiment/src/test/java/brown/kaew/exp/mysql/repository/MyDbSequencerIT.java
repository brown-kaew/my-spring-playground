package brown.kaew.exp.mysql.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class MyDbSequencerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.41"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema-mysql.sql"),
                    "/docker-entrypoint-initdb.d/schema.sql");

    @Autowired
    MyDbSequencer myDbSequencer;

    @Test
    void createSequence() {
        assertDoesNotThrow(() -> myDbSequencer.createSequence("test_create_sequence"));
    }

    @Test
    void nextVal_defaultMax10_expectCycledValueBetween0to9() {
        String testSequence = "my_sequence";
        myDbSequencer.createSequence(testSequence);
        for (int expectNext = 0; expectNext < 10; expectNext++) {
            Long next = myDbSequencer.nextVal(testSequence);
            assertEquals(expectNext, next);
        }

        Long result = myDbSequencer.nextVal(testSequence);
        assertEquals(0L, result, "The 11th round of the nextVal should be cycled to 0");
    }

    @Test
    void nextVal_withConcurrentAccess1000Threads_expectValueIsRepeated10Each() {
        // Arrange: Create a sequence and prepare a map to count occurrences
        int defaultMaxValue = 10;
        Map<Long, Integer> count = new ConcurrentHashMap<>(defaultMaxValue);
        String testSequence = "my_sequence_concurrent_default_max_value";
        myDbSequencer.createSequence(testSequence);

        // Act: Start threads, each calling nextVal on the sequence
        // and counting occurrences of each value
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                Long next = myDbSequencer.nextVal(testSequence);
                assertTrue(next >= 0 && next < defaultMaxValue, "Next value should be between 0 and 9");
                count.merge(next, 1, Integer::sum); // Increment the count for the next value
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread interrupted: " + e.getMessage());
            }
        }

        // Assert: Check that each value from 0 to 9 is repeated exactly 100 times
        for (long i = 0; i < defaultMaxValue; i++) {
            assertTrue(count.containsKey(i), "Count should contain key: " + i);
            assertEquals(100, count.get(i), "Count for key " + i + " should be 100");
        }
    }

    @Test
    void nextVal_withMaxValueOf100ConcurrentAccess1000Threads_expectValueIsRepeated10Each() {
        // Arrange: Create a sequence and prepare a map to count occurrences
        int maxValue = 100;
        Map<Long, Integer> count = new ConcurrentHashMap<>(maxValue);
        String testSequence = "my_sequence_concurrent_100_max_value";
        myDbSequencer.createSequence(testSequence, maxValue);

        // Act: Start threads, each calling nextVal on the sequence
        // and counting occurrences of each value
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                Long next = myDbSequencer.nextVal(testSequence);
                assertTrue(next >= 0 && next < maxValue, "Next value should be between 0 and 9");
                count.merge(next, 1, Integer::sum); // Increment the count for the next value
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread interrupted: " + e.getMessage());
            }
        }

        // Assert: Check that each value from 0 to 99 is repeated exactly 10 times
        for (long i = 0; i < maxValue; i++) {
            assertTrue(count.containsKey(i), "Count should contain key: " + i);
            assertEquals(10, count.get(i), "Count for key " + i + " should be 10");
        }
    }

    @Test
    void updateNextVal_defaultMax10_expectCycledValueBetween0to9() {
        String testSequence = "my_sequence_update_next_val";
        myDbSequencer.createSequence(testSequence);

        for (int expectNext = 0; expectNext < 10; expectNext++) {
            Long next = myDbSequencer.updateNextVal(testSequence);
            assertEquals(expectNext, next);
        }

        Long result = myDbSequencer.updateNextVal(testSequence);
        assertEquals(0L, result, "The 11th round of the nextVal should be cycled to 0");
    }

    @Test
    void nextVals_withMax100_expectCycledValueBetween0to99() {
        String testSequence = "my_sequence_update_next_vals";
        myDbSequencer.createSequence(testSequence, 100L);

        // Act and Assert
        // Get 10 values (0 to 9)
        List<Long> nextVals = myDbSequencer.nextVals(testSequence, 10);
        assertEquals(10, nextVals.size(), "Should return 10 values");
        for (int i = 0; i < 10; i++) {
            assertEquals(i, nextVals.get(i), "Value at index " + i + " should be " + i);
        }
        // Get another 10 values (10 to 19)
        nextVals = myDbSequencer.nextVals(testSequence, 10);
        assertEquals(10, nextVals.size(), "Should return another 10 values");
        for (int i = 0; i < 10; i++) {
            assertEquals(10 + i, nextVals.get(i), "Value at index " + i + " should be " + i);
        }
    }

}