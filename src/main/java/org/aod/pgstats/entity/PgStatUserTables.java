package org.aod.pgstats.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PgStatUserTables {
    private String schemaname;
    private String relname;
    private Timestamp last_vacuum;
    private Timestamp last_autovacuum;
    private Timestamp last_analyze;
    private Timestamp last_autoanalyze;
}
