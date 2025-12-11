CREATE TABLE IF NOT EXISTS CallRecord
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    call_id     BIGINT,
    method      VARCHAR(255),
    object_view TEXT,
    timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引以优化查询
CREATE INDEX IF NOT EXISTS idx_callrecord_method ON CallRecord (method);
