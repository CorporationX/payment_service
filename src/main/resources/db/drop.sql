DROP TABLE pending_operations;

DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/post_V004_pending.sql';