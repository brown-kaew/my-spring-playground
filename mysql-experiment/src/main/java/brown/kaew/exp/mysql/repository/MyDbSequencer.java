package brown.kaew.exp.mysql.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MyDbSequencer {

    private static final long MAX_VALUE = 10L;

    private final JdbcTemplate jdbcTemplate;

    public MyDbSequencer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void createSequence(String sequenceName) {
        this.createSequence(sequenceName, MAX_VALUE);
    }

    void createSequence(String sequenceName, long maxValue) {
        String sql = "INSERT INTO sequences (name, max_value) VALUES (?, ?)";
        jdbcTemplate.update(sql, sequenceName, maxValue);
    }

    /**
     * Retrieves the next value from the specified sequence. The sequence is expected to cycle through values from 0 to
     * MAX_VALUE - 1.
     *
     * @param sequenceName the name of the sequence
     * @return the next value in the sequence
     */
    public Long nextVal(String sequenceName) {
        String sql = "SELECT next_value(?)";
        return jdbcTemplate.queryForObject(sql, Long.class, sequenceName);
    }
}
