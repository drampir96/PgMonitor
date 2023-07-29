package org.aod.pgstats.entity;

import lombok.Data;

@Data
public class PgStatStatement {
    private Long queryid;
    private String query;
    private Long calls;
    private Double duration;
}
