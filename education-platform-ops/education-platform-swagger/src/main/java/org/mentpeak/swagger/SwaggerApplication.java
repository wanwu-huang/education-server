package org.mentpeak.swagger;

import org.mentpeak.core.launch.PlatformApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * swagger聚合启动器
 */
@SpringBootApplication
public class SwaggerApplication {

	public static void main(String[] args) {
		PlatformApplication.run("platform-swagger", SwaggerApplication.class, args);
	}

}
