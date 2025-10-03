package com.aopbuddy.mapper;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CallRecordMapper extends BaseMapper {
    void insertBatchCallRecords(List<CallRecordDo> callRecords);

    List<CallRecordDo> selectByIdGreaterThan(Long id);

    List<CallRecordDo> selectByMethods(@Param("methods") List<String> methods);

    void clearAllRecords();
}