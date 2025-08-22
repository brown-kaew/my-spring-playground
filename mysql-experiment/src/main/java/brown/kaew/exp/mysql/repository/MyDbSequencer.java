package brown.kaew.exp.mysql.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Long> nextVals(String sequenceName, int count) {
        String sql = """
                WITH RECURSIVE numbers (n) AS (
                  SELECT 1
                  UNION ALL
                  SELECT n + 1 FROM numbers WHERE n < ?
                )
                SELECT next_value(?) FROM numbers;
                """;
        return jdbcTemplate.queryForList(sql, Long.class, count, sequenceName);
    }

    @Transactional
    public Long updateNextVal(String sequenceName) {
        String updateSql = """
                UPDATE sequences
                SET current_value = last_insert_id(((current_value) % max_value) + increment)
                WHERE name = ?;
                """;
        jdbcTemplate.update(updateSql, sequenceName);
        String selectSql = "SELECT (last_insert_id() - 1)";
        return jdbcTemplate.queryForObject(selectSql, Long.class);
    }
}
