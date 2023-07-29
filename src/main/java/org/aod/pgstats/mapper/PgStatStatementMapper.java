package org.aod.pgstats.mapper;

import org.aod.pgstats.entity.PgStatStatement;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgStatStatementMapper implements RowMapper<PgStatStatement> {

    @Override
    public PgStatStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgStatStatement pgStatStatement = new PgStatStatement();
        pgStatStatement.setQueryid(rs.getLong("QUERYID"));
        pgStatStatement.setQuery(rs.getString("QUERY"));
        pgStatStatement.setCalls(rs.getLong("CALLS"));
        pgStatStatement.setDuration(rs.getDouble("DURATION"));
        return pgStatStatement;
    }
}