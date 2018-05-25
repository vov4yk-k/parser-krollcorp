package com.vkruk.parserkrollcorp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest({"db.port=3306",
		"db.username=root",
		"server.port=5050",
		"db.password=root",
		"db.schema=products",
		"db.address=localhost",
		"usr.login=test",
		"usr.password=test"})
public class ParserKrollcorpApplicationTests {

	@Test
	public void contextLoads() {
	}

}
