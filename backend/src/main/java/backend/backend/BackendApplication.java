package backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

@EnableScheduling
@SpringBootApplication
public class BackendApplication {
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
