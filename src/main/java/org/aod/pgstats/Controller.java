package org.aod.pgstats;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aod.pgstats.mapper.*;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Controller {
    private final JdbcTemplate jdbcTemplate;

    public Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    final String databaseURL = "${influx_host_port}";
    final String userName = "${influx_username}";
    final String password = "${influx_password}";

    private String queryPgStatActivity = "SELECT usename AS USENAME,  state AS STATE, COUNT(*) AS VALUE FROM pg_stat_activity WHERE STATE IS NOT NULL GROUP BY USENAME, STATE";
    private String queryPgStatProgressVacuum = "SELECT schemaname AS SCHEMANAME, relname AS RELNAME, COALESCE(last_vacuum, DATE '2023-01-01') AS LAST_VACUUM, COALESCE(last_autovacuum, DATE '2023-01-01')  AS LAST_AUTOVACUUM, COALESCE(last_analyze, DATE '2023-01-01')  AS LAST_ANALYZE, COALESCE(last_autoanalyze, DATE '2023-01-01')  AS LAST_AUTOANALYZE FROM pg_stat_user_tables";
    private String queryPgStatStatement = "SELECT queryid AS QUERYID, query AS QUERY, calls AS CALLS, total_exec_time/CALLS AS DURATION FROM pg_stat_statements";
    private String queryPgStatDatabase = "select round(sum(blks_hit)*100/sum(blks_hit+blks_read), 2) as cache_hit_ratio, sum(conflicts) as registered_conflicts, sum(deadlocks) as registered_deadlocks, sum(temp_bytes) as temp_bytes_written, sum(temp_files) as temp_files_created, sum(blks_hit) as blocks_hit_per_second, sum(blks_read) as blocks_read_per_second, sum(xact_commit) as commits_per_second, sum(xact_commit)+ sum(xact_rollback) as transactions_per_second, sum(tup_deleted) as tuples_deleted_per_second, sum(tup_fetched) as tuples_fetched_per_second, sum(tup_inserted) as tuples_inserted_per_second, sum(tup_returned) as tuples_returned_per_second, sum(tup_updated) as tuples_updated_per_second from pg_stat_database";
    private String queryPgStatBgwriter = "select checkpoints_timed, checkpoints_req, checkpoint_write_time, checkpoint_sync_time, buffers_checkpoint, buffers_clean, maxwritten_clean, buffers_backend, buffers_backend_fsync, buffers_alloc, stats_reset from pg_stat_bgwriter";

    @Async
    @SneakyThrows
    @Scheduled(fixedDelay = 5000)
    public void pgActivity() {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
        influxDB.setDatabase("pprb_pg_mon");
        var statements = jdbcTemplate.query(queryPgStatActivity, new PgStatActivityMapper())
                .stream()
                .peek(statsActivity -> {
                    try {
                        Point point = Point.measurement("pg_stat_activity")
                                .time(timeStamp, TimeUnit.MILLISECONDS)
                                .tag("Usename", statsActivity.getUsename())
                                .tag("State", statsActivity.getState())
                                .addField("Value", statsActivity.getValue()).build();
                        influxDB.write(point);
                    } catch (Exception ex) {
                        log.error("Found NULL value: " + ex.getMessage());
                    }
                }).collect(Collectors.toList());
        influxDB.close();
    }


    @Async
    @Scheduled(fixedDelay = 15000L)
    public void pgVacuum() {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
        influxDB.setDatabase("pprb_pg_mon");
        var statements = jdbcTemplate.query(queryPgStatProgressVacuum, new PgStatUserTablesMapper())
                .stream()
                .peek(statsActivity -> {
                    try {
                        Point point = Point.measurement("pg_stat_user_tables")
                                .time(timeStamp, TimeUnit.MILLISECONDS)
                                .tag("Relname", statsActivity.getRelname())
                                .tag("Schemaname", statsActivity.getSchemaname())
                                .addField("Last_analyze", statsActivity.getLast_analyze().getTime())
                                .addField("Last_autoanalyze", statsActivity.getLast_autoanalyze().getTime())
                                .addField("Last_vacuum", statsActivity.getLast_vacuum().getTime())
                                .addField("Last_autovacuum", statsActivity.getLast_autovacuum().getTime())
                                .build();
                        influxDB.write(point);
                    } catch (Exception ex) {
                        log.error("pg_stat_user_tables error: " + ex.getMessage());
                    }
                }).collect(Collectors.toList());
        influxDB.close();
    }

    @Async
    @Scheduled(fixedDelay = 5000)
    public void pgStatement() {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
        influxDB.setDatabase("pprb_pg_mon");
        var statements = jdbcTemplate.query(queryPgStatStatement, new PgStatStatementMapper())
                .stream().peek((statsStatement) -> {
                    try {
                        Point point = Point.measurement("pg_stat_statement")
                                .time(timeStamp, TimeUnit.MILLISECONDS)
                                .tag("Queryid", Long.toString(statsStatement.getQueryid()))
                                .tag("Query", queryDefiner(statsStatement.getQuery()))
                                .addField("Calls", statsStatement.getCalls())
                                .addField("Duration", statsStatement.getDuration()).build();
                        influxDB.write(point);
                    } catch (Exception ex) {
                        log.error("Found pg_stat_statement error: " + queryDefiner(statsStatement.getQuery()));
                    }
                }).collect(Collectors.toList());
        influxDB.close();
    }

    @Async
    @SneakyThrows
    @Scheduled(fixedDelay = 5000)
    public void pgDatabase() {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
        influxDB.setDatabase("pprb_pg_mon");
        var statements = jdbcTemplate.query(queryPgStatDatabase, new PgStatDatabaseMapper())
                .stream()
                .peek(statDatabase -> {
                    try {
                        Point point = Point.measurement("pg_stat_database")
                                .time(timeStamp, TimeUnit.MILLISECONDS)
                                .tag("database", "sberpay_lt")
                                .addField("cache_hit_ratio", statDatabase.getCache_hit_ratio())
                                .addField("registered_conflicts", statDatabase.getRegistered_conflicts())
                                .addField("registered_deadlocks", statDatabase.getRegistered_deadlocks())
                                .addField("temp_bytes_written", statDatabase.getTemp_bytes_written())
                                .addField("temp_files_created", statDatabase.getTemp_files_created())
                                .addField("blocks_hit_per_second", statDatabase.getBlocks_hit_per_second())
                                .addField("blocks_read_per_second", statDatabase.getBlocks_read_per_second())
                                .addField("commits_per_second", statDatabase.getCommits_per_second())
                                .addField("transactions_per_second", statDatabase.getTransactions_per_second())
                                .addField("tuples_deleted_per_second", statDatabase.getTuples_deleted_per_second())
                                .addField("tuples_fetched_per_second", statDatabase.getTuples_fetched_per_second())
                                .addField("tuples_inserted_per_second", statDatabase.getTuples_inserted_per_second())
                                .addField("tuples_returned_per_second", statDatabase.getTuples_returned_per_second())
                                .addField("tuples_updated_per_second", statDatabase.getTuples_updated_per_second())
                                .build();
                        influxDB.write(point);
                    } catch (Exception ex) {
                        log.error("pg_stat_database error: " + ex.getMessage());
                    }
                }).collect(Collectors.toList());
        influxDB.close();
    }

    @Async
    @SneakyThrows
    @Scheduled(fixedDelay = 5000)
    public void pgBgwriter() {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
        influxDB.setDatabase("pprb_pg_mon");
        var statements = jdbcTemplate.query(queryPgStatBgwriter, new PgStatBgwriterMapper())
                .stream()
                .peek(statBgwriter -> {
                    try {
                        Point point = Point.measurement("pg_stat_bgwriter")
                                .time(timeStamp, TimeUnit.MILLISECONDS)
                                .tag("database", "sberpay_lt")
                                .addField("checkpoints_timed", statBgwriter.getCheckpoints_timed())
                                .addField("checkpoints_req", statBgwriter.getCheckpoints_req())
                                .addField("checkpoint_write_time", statBgwriter.getCheckpoint_write_time())
                                .addField("checkpoint_sync_time", statBgwriter.getCheckpoint_sync_time())
                                .addField("buffers_checkpoint", statBgwriter.getBuffers_checkpoint())
                                .addField("buffers_clean", statBgwriter.getBuffers_clean())
                                .addField("maxwritten_clean", statBgwriter.getMaxwritten_clean())
                                .addField("buffers_backend", statBgwriter.getBuffers_backend())
                                .addField("buffers_backend_fsync", statBgwriter.getBuffers_backend_fsync())
                                .addField("buffers_alloc", statBgwriter.getBuffers_alloc())
                                .addField("stats_reset", statBgwriter.getStats_reset().getTime())
                                .build();
                        influxDB.write(point);
                    } catch (Exception ex) {
                        log.error("pg_stat_bgwriter error: " + ex.getMessage());
                    }
                }).collect(Collectors.toList());
        influxDB.close();

    }


    public String queryDefiner(String query) {
        String selectPattern = "select(.*)from";
        String insertPattern = "insert into \\S+";
        String updatePattern = "update \\S+";
        Pattern p;
        Matcher m;

        if (query.contains("insert")) {
            p = Pattern.compile(insertPattern);
            for (m = p.matcher(query); m.find(); query = m.group()) {
            }
        } else if (query.contains("update")) {
            p = Pattern.compile(updatePattern);
            for (m = p.matcher(query); m.find(); query = m.group()) {
            }
        }
        if (query.contains("select")) {
            try {
                query = query.replaceAll("(\\r|\\n)", " ").replaceAll("[\\s]+", " ").replaceAll(selectPattern, "select * from ");
                if (query.length()>100){query = query.substring(0,100).concat(" >");}
            } catch (Exception ex) {
                log.error("select replace error: " + ex.getMessage());
            }
        }

        else{
            try {
                query = query.replaceAll("(\\r|\\n)", " ").replaceAll("[\\s]+", " ");
                if (query.length()>100){query=query.substring(0,100).concat(" >");}
            } catch (Exception ex) {
                log.error("SELECT replace error: " + ex.getMessage());
            }
        }
        return query;
    }
}