package org.aod.pgstats.entity;

import lombok.Data;

@Data
public class PgStatDatabase {
    private Double cache_hit_ratio;
    private Double registered_conflicts;
    private Double registered_deadlocks;
    private Double temp_bytes_written;
    private Double temp_files_created;
    private Double blocks_hit_per_second;
    private Double blocks_read_per_second;
    private Double commits_per_second;
    private Double transactions_per_second;
    private Double tuples_deleted_per_second;
    private Double tuples_fetched_per_second;
    private Double tuples_inserted_per_second;
    private Double tuples_returned_per_second;
    private Double tuples_updated_per_second;
}
