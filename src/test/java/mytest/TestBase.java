package mytest;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBase {
    protected ClassPathXmlApplicationContext ctx;

    @Before
    public void init() {
        ctx = new ClassPathXmlApplicationContext(
                "spring/applicationContext.xml", "spring/applicationContext-Redis.xml");
    }

    @After
    public void destory() {
        ctx.close();
    }
}
