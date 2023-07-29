package org.aod.pgstats.mapper;

import org.aod.pgstats.entity.PgStatDatabase;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgStatDatabaseMapper implements RowMapper<PgStatDatabase> {

    @Override
    public PgStatDatabase mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgStatDatabase pgStatDatabase = new PgStatDatabase();
        pgStatDatabase.setCache_hit_ratio(rs.getDouble("cache_hit_ratio"));
        pgStatDatabase.setRegistered_conflicts(rs.getDouble("registered_conflicts"));
        pgStatDatabase.setRegistered_deadlocks(rs.getDouble("registered_deadlocks"));
        pgStatDatabase.setTemp_bytes_written(rs.getDouble("temp_bytes_written"));
        pgStatDatabase.setTemp_files_created(rs.getDouble("temp_files_created"));
        pgStatDatabase.setBlocks_hit_per_second(rs.getDouble("blocks_hit_per_second"));
        pgStatDatabase.setBlocks_read_per_second(rs.getDouble("blocks_read_per_second"));
        pgStatDatabase.setCommits_per_second(rs.getDouble("commits_per_second"));
        pgStatDatabase.setTransactions_per_second(rs.getDouble("transactions_per_second"));
        pgStatDatabase.setTuples_deleted_per_second(rs.getDouble("tuples_deleted_per_second"));
        pgStatDatabase.setTuples_fetched_per_second(rs.getDouble("tuples_fetched_per_second"));
        pgStatDatabase.setTuples_inserted_per_second(rs.getDouble("tuples_inserted_per_second"));
        pgStatDatabase.setTuples_returned_per_second(rs.getDouble("tuples_returned_per_second"));
        pgStatDatabase.setTuples_updated_per_second(rs.getDouble("tuples_updated_per_second"));
        return pgStatDatabase;
    }
}
