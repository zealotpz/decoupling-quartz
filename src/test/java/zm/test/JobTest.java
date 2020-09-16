package zm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zealotpz.quartz.model.QuartzJob;
import com.zealotpz.quartz.utils.HttpUtil;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * description:
 * author: zealotpz
 * create: 2019-04-25 15:06
 **/
public class JobTest {

    @Test
    public void addJob() {
        QuartzJob quartz = new QuartzJob();

        quartz.setJobName("myTask"); //任务名称
        quartz.setJobGroup("dailyTask"); //任务分组
        quartz.setDescription("每日统计"); //任务描述
        quartz.setJobClassName("com.zealotpz.quartz.job.TestJob");
        quartz.setCronExpression("0 15 2 * * ?"); //每天 04:00
//        quartz.setJobEffectDateStart(LocalDateTime.now().plusDays(1)); //任务开始时间
//        quartz.setJobEffectDateEnd(LocalDateTime.now().plusDays(2)); //任务结束时间

        JSONObject jj = JSON.parseObject("{\"merchantNo\": \"M202000001\"}");
        quartz.setJobMessage(jj);

        System.out.println("------->" + quartz.toString());

        String ret = HttpUtil.sendJsonHttpsPost("http://127.0.0.1:8089/job/add", JSON.toJSONString(quartz));
        System.out.println("addJob------->" + ret);

    }

    @Test
    public void jobList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "member5");
        jsonObject.put("jobGroup", "imms_test");

        jsonObject.put("pageNum", "1");
        jsonObject.put("pageSize", "10");

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/list", jsonObject.toJSONString());
        System.out.println("jobList ------->" + ret);

    }

    @Test
    public void triggerJob() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "myTask");
        jsonObject.put("jobGroup", "dailyTask");

        String ret = HttpUtil.sendJsonHttpsPost("http://127.0.0.1:8089/job/trigger", jsonObject.toJSONString());
        System.out.println("triggerJob ------->" + ret);

    }

    @Test
    public void pauseJob() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "member6");
        jsonObject.put("jobGroup", "dailyTask");

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/pause", jsonObject.toJSONString());
        System.out.println("pauseJob ------->" + ret);
    }

    @Test
    public void resumeJob() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "myTask");
        jsonObject.put("jobGroup", "dailyTask");

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/resume", jsonObject.toJSONString());
        System.out.println("resumeJob ------->" + ret);

    }

    //移除(删除)定时任务
    @Test
    public void removeJob() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "test2");
        jsonObject.put("jobGroup", "dailyTask");

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/remove", jsonObject.toJSONString());
        System.out.println("removeJob ------->" + ret);

    }

    @Test
    public void modifyJobTime() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "myTask");//任务名称
        jsonObject.put("jobGroup", "dailyTask"); //任务分组
        jsonObject.put("cronExpression", "0 15 2 * * ?");

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/modifyJobTime", jsonObject.toString());
        System.out.println("modifyJobTime ------->" + ret);
    }


    @Test
    public void updateJobBase() {
        QuartzJob quartz = new QuartzJob();
        quartz.setJobName("member5");
        quartz.setJobGroup("imms_test");
        quartz.setJobEffectDateStart(LocalDateTime.now().plusDays(1));
        quartz.setJobEffectDateEnd(LocalDateTime.now().plusDays(2));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "托塔天王全额全");
        jsonObject.put("age", "1024");
        jsonObject.put("阿斯达", "奥奇new去");
        quartz.setJobMessage(jsonObject);

        System.out.println("------->" + quartz.toString());

        String ret = HttpUtil.sendJsonHttpsPost("http://localhost:8089/job/updateJobBase", JSON.toJSONString(quartz));
        System.out.println("updateJobBase------->" + ret);

    }

}
