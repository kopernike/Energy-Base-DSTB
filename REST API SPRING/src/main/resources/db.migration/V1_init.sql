CREATE TABLE usage_entity (
                              hour TIMESTAMP PRIMARY KEY,
                              community_produced DOUBLE PRECISION,
                              community_used DOUBLE PRECISION,
                              grid_used DOUBLE PRECISION
);

CREATE TABLE percentage_entity (
                                   hour TIMESTAMP PRIMARY KEY,
                                   community_depleted DOUBLE PRECISION,
                                   grid_portion DOUBLE PRECISION
);
