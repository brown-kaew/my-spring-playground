package brown.kaew.exp.mysql.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
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
    JdbcTemplate jdbcTemplate;

    @Test
    void createSequence() {
        MyDbSequencer testCreateSequence = new MyDbSequencer(this.jdbcTemplate, "test_create_sequence");
        assertDoesNotThrow(testCreateSequence::init);
    }

    @Test
    void nextVal_withMax10_expectCycledValueBetween0to9() {
        MyDbSequencer myDbSequencer = new MyDbSequencer(this.jdbcTemplate, "my_sequence", 10);
        myDbSequencer.init();

        for (int expectNext : List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)) {
            Long next = myDbSequencer.nextVal();
            assertEquals(expectNext, next);
        }
    }

    @Test
    void nextVal_withConcurrentAccess1000Threads_expectValueIsRepeated10Each() {
        // Arrange: Create a sequence and prepare a map to count occurrences
        int maxValue = 10;
        Map<Long, Integer> count = new ConcurrentHashMap<>(maxValue);
        MyDbSequencer myDbSequencer = new MyDbSequencer(this.jdbcTemplate, "my_sequence_concurrent_default_max_value",
                maxValue);
        myDbSequencer.init();

        // Act: Start threads, each calling nextVal on the sequence
        // and counting occurrences of each value
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                Long next = myDbSequencer.nextVal();
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

        // Assert: Check that each value from 0 to 9 is repeated exactly 100 times
        for (long i = 0; i < maxValue; i++) {
            assertTrue(count.containsKey(i), "Count should contain key: " + i);
            assertEquals(100, count.get(i), "Count for key " + i + " should be 100");
        }
    }

    @Test
    void nextVal_withMaxValueOf100ConcurrentAccess1000Threads_expectValueIsRepeated10Each() {
        // Arrange: Create a sequence and prepare a map to count occurrences
        int maxValue = 100;
        Map<Long, Integer> count = new ConcurrentHashMap<>(maxValue);
        MyDbSequencer myDbSequencer = new MyDbSequencer(this.jdbcTemplate, "my_sequence_concurrent_100_max_value",
                maxValue);
        myDbSequencer.init();

        // Act: Start threads, each calling nextVal on the sequence
        // and counting occurrences of each value
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                Long next = myDbSequencer.nextVal();
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
    void updateNextVal_withMax10_expectCycledValueBetween0to9() {
        MyDbSequencer myDbSequencer = new MyDbSequencer(this.jdbcTemplate, "my_sequence_update_next_val", 10);
        myDbSequencer.init();

        for (int expectNext : List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)) {
            Long next = myDbSequencer.updateNextVal();
            assertEquals(expectNext, next);
        }
    }

    @Test
    void nextVals_withMax100_expectCycledValueBetween0to99() {
        MyDbSequencer myDbSequencer = new MyDbSequencer(this.jdbcTemplate, "my_sequence_update_next_vals", 100);
        myDbSequencer.init();

        // Act and Assert
        // Get 10 values (1 to 10)
        List<Long> nextVals = myDbSequencer.nextVals(10);
        assertEquals(10, nextVals.size(), "Should return 10 values");
        for (int expectNext : List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            assertEquals(expectNext, nextVals.removeFirst());
        }
        // Get another 80 values (11 to 90)
        nextVals = myDbSequencer.nextVals(80);
        assertEquals(80, nextVals.size(), "Should return another 80 values");
        for (int expectNext = 11; expectNext <= 90; expectNext++) {
            assertEquals(expectNext, nextVals.removeFirst());
        }

        // Get 10 values (91 to 0)
        nextVals = myDbSequencer.nextVals(10);
        assertEquals(10, nextVals.size(), "Should return 10 values");
        for (int expectNext : List.of(91, 92, 93, 94, 95, 96, 97, 98, 99, 0)) {
            assertEquals(expectNext, nextVals.removeFirst());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 7})
    void updateNextVal_vs_nextVal_Success_shouldReturnSameValue(int increment) {
        MyDbSequencer nextValSequencer =
                new MyDbSequencer(this.jdbcTemplate, "vs_sequence_next_val", 100, increment);
        nextValSequencer.init();
        MyDbSequencer updateNextValSequencer =
                new MyDbSequencer(this.jdbcTemplate, "vs_sequence_update_next_val", 100, increment);
        updateNextValSequencer.init();

        for (int i = 0; i < 1000; i++) {
            Long nextVal = nextValSequencer.nextVal();
            Long updateNextVal = updateNextValSequencer.updateNextVal();
            assertEquals(nextVal, updateNextVal);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 7})
    void updateNextVals_vs_nextVals_Success_shouldReturnSameValue(int increment) {
        MyDbSequencer nextValsSequencer = new MyDbSequencer(this.jdbcTemplate,
                ("vs_sequence_next_vals" + increment), 100, increment);
        nextValsSequencer.init();
        MyDbSequencer updateNextValsSequencer = new MyDbSequencer(this.jdbcTemplate,
                ("vs_sequence_update_next_vals" + increment), 100, increment);
        updateNextValsSequencer.init();
        int fetchSize = 10;

        for (int i = 0; i < 1000; i++) {
            List<Long> nextVal = nextValsSequencer.nextVals(fetchSize);
            List<Long> updateNextVal = updateNextValsSequencer.updateNextVals(fetchSize);
            assertEquals(nextVal, updateNextVal);
        }
    }

}