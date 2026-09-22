package com.hugo.humami;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "aws.s3.access-key=local-test-access-key",
        "aws.s3.secret-key=local-test-secret-key"
})
class HumamiApplicationTests {

	@Test
	void contextLoads() {
	}

}
