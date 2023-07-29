package org.aod.pgstats.entity;

import lombok.Data;

@Data
public class PgStatActivity {
    private String usename;
    private String state;
    private Long value;
}
