package brown.kaew.ttc;

import org.springframework.boot.SpringApplication;

public class TestTryTestcontainerApplication {

	public static void main(String[] args) {
		SpringApplication.from(TryTestcontainerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
