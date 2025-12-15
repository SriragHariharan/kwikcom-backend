package com.kwikcom.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"DB_USERNAME=root",
		"DB_PASSWORD=mysrisql"
})
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}

}
