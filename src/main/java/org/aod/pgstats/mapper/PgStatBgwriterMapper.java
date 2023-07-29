package org.aod.pgstats.mapper;

import org.aod.pgstats.entity.PgStatBgwriter;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgStatBgwriterMapper implements RowMapper<PgStatBgwriter> {

    @Override
    public PgStatBgwriter mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgStatBgwriter pgStatBgwriter = new PgStatBgwriter();
        pgStatBgwriter.setCheckpoints_timed(rs.getLong("checkpoints_timed"));
        pgStatBgwriter.setCheckpoints_req(rs.getLong("checkpoints_req"));
        pgStatBgwriter.setCheckpoint_write_time(rs.getDouble("checkpoint_write_time"));
        pgStatBgwriter.setCheckpoint_sync_time(rs.getDouble("checkpoint_sync_time"));
        pgStatBgwriter.setBuffers_checkpoint(rs.getLong("buffers_checkpoint"));
        pgStatBgwriter.setBuffers_clean(rs.getLong("buffers_clean"));
        pgStatBgwriter.setMaxwritten_clean(rs.getLong("maxwritten_clean"));
        pgStatBgwriter.setBuffers_backend(rs.getLong("buffers_backend"));
        pgStatBgwriter.setBuffers_backend_fsync(rs.getLong("buffers_backend_fsync"));
        pgStatBgwriter.setBuffers_alloc(rs.getLong("buffers_alloc"));
        pgStatBgwriter.setStats_reset(rs.getTimestamp("stats_reset"));
        return pgStatBgwriter;
    }
}
