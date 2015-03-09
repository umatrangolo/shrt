# --- !Ups

-- Enable nativa full text search
CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
CALL FT_INIT();
CALL FT_CREATE_INDEX('PUBLIC', 'TEST', NULL);

# --- !Downs
-- nop
