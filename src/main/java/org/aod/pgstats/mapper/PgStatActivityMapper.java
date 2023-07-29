package org.aod.pgstats.mapper;

import org.aod.pgstats.entity.PgStatActivity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
public class PgStatActivityMapper implements RowMapper<PgStatActivity> {

    @Override
    public PgStatActivity mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgStatActivity pgStatActivity = new PgStatActivity();
        pgStatActivity.setUsename(rs.getString("USENAME"));
        pgStatActivity.setState(rs.getString("STATE"));
        pgStatActivity.setValue(rs.getLong("VALUE"));
        return pgStatActivity;
    }
}
