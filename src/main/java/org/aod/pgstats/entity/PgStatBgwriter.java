package org.aod.pgstats.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PgStatBgwriter {
    private Long        checkpoints_timed;
    private Long        checkpoints_req;
    private Double      checkpoint_write_time;
    private Double      checkpoint_sync_time;
    private Long        buffers_checkpoint;
    private Long        buffers_clean;
    private Long        maxwritten_clean;
    private Long        buffers_backend;
    private Long        buffers_backend_fsync;
    private Long        buffers_alloc;
    private Timestamp   stats_reset;
}
