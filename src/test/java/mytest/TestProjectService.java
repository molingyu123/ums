package mytest;

import org.junit.Test;

import com.lanswon.entity.ResultMsg;
import com.lanswon.service.ActivitiService;


public class TestProjectService extends TestBase {

    @Test
    public void newQuerytest() throws Exception {
        ActivitiService activitiService =
                ctx.getBean("activitiService",
                        ActivitiService.class);
        ResultMsg result = activitiService.newQueryProcessJoinByUserId("xw3204110", "id", "");
        System.out.println(result);
    }

    @Test
    public void querytest() throws Exception {
        ActivitiService activitiService =
                ctx.getBean("activitiService",
                        ActivitiService.class);
        activitiService.queryProcessJoinByUserId("xw3204110");
        System.out.println("1");
    }


}
