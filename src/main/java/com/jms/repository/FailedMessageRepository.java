package com.jms.repository;

import com.jms.entity.FailedMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FailedMessageRepository implements InitializingBean, DisposableBean {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<FailedMessage> failedMessageRowMapper = (rs, rowNum) -> {
        FailedMessage failedMessage = new FailedMessage();
        failedMessage.setId(rs.getInt("id"));
        failedMessage.setExchange(rs.getString("exchange"));
        failedMessage.setRootingKey(rs.getString("routing_key"));
        failedMessage.setPayload(rs.getString("payload"));
        failedMessage.setStatus(rs.getString("status"));
        return failedMessage;
    };

    public void save(FailedMessage failedMessage) {
        jdbcTemplate.update("INSERT INTO failure_messages (exchange, routing_key, payload, status) VALUES (?, ?, ?, ?)",
                failedMessage.getExchange(),
                failedMessage.getRootingKey(),
                failedMessage.getPayload(),
                failedMessage.getStatus());
    }

    public boolean exists(Integer id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM failure_messages WHERE id = ?", Integer.class, id) > 0;
    }

    public FailedMessage findById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM failure_messages WHERE id = ?", failedMessageRowMapper, id);
    }

    public void removeById(Integer id) {
        jdbcTemplate.update("DELETE FROM failure_messages WHERE id = ?", id);
    }

    public List<FailedMessage> findAll() {
        return jdbcTemplate.query("SELECT * FROM failure_messages", failedMessageRowMapper);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate.execute("CREATE TABLE failure_messages (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "exchange VARCHAR(20) NOT NULL, " +
                "routing_key VARCHAR(20) NOT NULL, " +
                "payload VARCHAR(2000) NOT NULL, " +
                "status VARCHAR(20) DEFAULT 'NONE')");
    }

    @Override
    public void destroy() throws Exception {
        jdbcTemplate.execute("DROP TABLE failure_messages");
    }
}
