package brown.kaew.exp.mysql.repository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.LongStream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

class MyDbSequencer {

    private static final long DEFAULT_MAX_VALUE = 1_000L;
    private static final int DEFAULT_INCREMENT = 1;
    private static final int MAX_FETCH_SIZE = 1_000;

    private final JdbcTemplate jdbcTemplate;
    private final String sequenceName;
    private final long maxValue;
    private final long increment;

    public MyDbSequencer(JdbcTemplate jdbcTemplate, String sequenceName) {
        this(jdbcTemplate, sequenceName, DEFAULT_MAX_VALUE, DEFAULT_INCREMENT);
    }

    public MyDbSequencer(JdbcTemplate jdbcTemplate, String sequenceName, long maxValue) {
        this(jdbcTemplate, sequenceName, maxValue, DEFAULT_INCREMENT);
    }

    public MyDbSequencer(JdbcTemplate jdbcTemplate, String sequenceName, long maxValue, int increment) {
        this.jdbcTemplate = jdbcTemplate;
        this.sequenceName = sequenceName;
        this.maxValue = maxValue;
        this.increment = increment;
    }

    @PostConstruct
    void init() {
        this.createSequenceIfNotExist(this.sequenceName, this.maxValue, this.increment);
    }

    void createSequenceIfNotExist(String sequenceName, long maxValue, long increment) {
        String sql = "INSERT IGNORE INTO sequence (name, max_val, increment) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, sequenceName, maxValue, increment);
    }

    /**
     * Retrieves the next value from the specified sequence. The sequence is expected to cycle through values from 0 to
     * DB sequence max_val - 1
     *
     * @return the next value in the sequence
     */
    public Long nextVal() {
        String sql = "SELECT next_val(?)";
        return jdbcTemplate.queryForObject(sql, Long.class, this.sequenceName);
    }

    /**
     * Retrieves the next 'fetchSize' values from the specified sequence. The sequence is expected to cycle through
     * values from 0 to DB sequence max_val - 1
     *
     * @param fetchSize number of values to fetch, must be between 1 and 1000
     * @return list of next values in the sequence
     * @throws IllegalArgumentException if fetchSize is less than 1 or greater than 1000
     */
    public List<Long> nextVals(int fetchSize) {
        if (fetchSize < 1 || fetchSize > MAX_FETCH_SIZE) {
            throw new IllegalArgumentException("Fetch size must be between 1 and " + MAX_FETCH_SIZE);
        }
        String sql = "SELECT next_val(?) FROM seq_1_to_1000 s WHERE s.id <= ?";
        return jdbcTemplate.queryForList(sql, Long.class, this.sequenceName, fetchSize);
    }

    /**
     * Updates and retrieves the next value from the specified sequence using an atomic update statement. The sequence
     * is expected to cycle through values from 0 to DB sequence max_val - 1
     *
     * @return the next value in the sequence
     * @throws IllegalStateException if last_insert_id() returns null, which should not happen
     */
    @Transactional
    public Long updateNextVal() {
        String updateSql = "UPDATE sequence SET cur_val = last_insert_id((cur_val % max_val) + increment) WHERE name = ?";
        jdbcTemplate.update(updateSql, this.sequenceName);
        Long lastInsertId = jdbcTemplate.queryForObject("SELECT last_insert_id()", Long.class);
        if (lastInsertId == null) {
            throw new IllegalStateException("last_insert_id() should not return null");
        }
        return lastInsertId % this.maxValue;
    }

    /**
     * Updates the sequence to the next 'fetchSize' values and returns them. This method uses a single SQL update to
     * increment the sequence value and retrieves the last inserted ID to calculate the next values.
     *
     * @param fetchSize number of values to fetch, must be a positive integer between 1 and 1000
     * @return list of next values in the sequence
     * @throws IllegalArgumentException if fetchSize is less than 1
     * @throws IllegalStateException    if last_insert_id() returns null, which should not happen
     */
    @Transactional
    public List<Long> updateNextVals(int fetchSize) {
        if (fetchSize < 1 || fetchSize > MAX_FETCH_SIZE) {
            throw new IllegalArgumentException("Fetch size must be between 1 and " + MAX_FETCH_SIZE);
        }
        String updateSql = "UPDATE sequence SET cur_val = last_insert_id((cur_val % max_val) + (increment * ?)) WHERE name = ?";
        jdbcTemplate.update(updateSql, fetchSize, this.sequenceName);
        Long lastInsertId = jdbcTemplate.queryForObject("SELECT last_insert_id()", Long.class);
        if (lastInsertId == null) {
            throw new IllegalStateException("last_insert_id() should not return null");
        }
        return this.toSequenceValues(fetchSize, lastInsertId);
    }

    List<Long> toSequenceValues(int fetchSize, Long lastInsertId) {
        if (fetchSize <= 0 || lastInsertId < 0) {
            throw new IllegalArgumentException(
                    "Fetch size must be a positive integer and lastInsertId must be non-negative");
        }
        final long startSequence =
                Math.abs(lastInsertId - (this.increment * fetchSize) + this.maxValue) % this.maxValue;
        return LongStream.rangeClosed(1, fetchSize)
                .mapToObj(i -> {
                    long next = i * this.increment;
                    // Use modulo to recycle the sequence if it exceeds maxValue
                    return (startSequence + next) % this.maxValue;
                })
                .toList();
    }

}
