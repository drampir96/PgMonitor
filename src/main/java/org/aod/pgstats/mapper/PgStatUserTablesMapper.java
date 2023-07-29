package org.aod.pgstats.mapper;

import org.aod.pgstats.entity.PgStatUserTables;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgStatUserTablesMapper implements RowMapper<PgStatUserTables> {

    @Override
    public PgStatUserTables mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgStatUserTables pgStatUserTables = new PgStatUserTables();
        pgStatUserTables.setSchemaname(rs.getString("SCHEMANAME"));
        pgStatUserTables.setRelname(rs.getString("RELNAME"));
        pgStatUserTables.setLast_vacuum(rs.getTimestamp("LAST_VACUUM"));
        pgStatUserTables.setLast_autovacuum(rs.getTimestamp("LAST_AUTOVACUUM"));
        pgStatUserTables.setLast_analyze(rs.getTimestamp("LAST_ANALYZE"));
        pgStatUserTables.setLast_autoanalyze(rs.getTimestamp("LAST_AUTOANALYZE"));
        return pgStatUserTables;
    }
}
