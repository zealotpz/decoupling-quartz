package com.zealotpz.quartz.mapper;

import com.zealotpz.quartz.model.QuartzJob;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface JobMapper extends Mapper<QuartzJob> {

    List<QuartzJob> listJob(@Param("jobName") String jobName);

    QuartzJob getJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int saveJob(QuartzJob job);

    int updateJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup, @Param("status") String status);

    int removeQuartzJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int updateJob(QuartzJob quartz);

    int updateJobBase(QuartzJob quartz);
}
