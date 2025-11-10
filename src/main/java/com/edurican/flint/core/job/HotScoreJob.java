package com.edurican.flint.core.job;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class HotScoreJob {

    private final JdbcTemplate jdbcTemplate;

    //확인 상 1분마다 바꿈
    @Scheduled(cron =  "0 0/1 * * * ?")
    public void calculateHotScore() {
        jdbcTemplate.update("DELETE FROM hot_posts");
        jdbcTemplate.update("ALTER TABLE hot_posts AUTO_INCREMENT = 1");

        String sql = """
            INSERT INTO hot_posts(post_id, hot_score, computed_at)
            SELECT p.id,(
                (7.4 * p.like_count)
                + ( 2.5 * p.comment_count)
                + ( 0.1 * LOG(p.view_count + 1))
            )* EXP(- (TIMESTAMPDIFF(HOUR, p.created_at, NOW())) / 72.0),
            NOW()
            FROM posts p
            WHERE p.status = 'ACTIVE'
            AND p.created_at >= NOW() - INTERVAL 7 DAY
        """;
        jdbcTemplate.update(sql);

    }
}
