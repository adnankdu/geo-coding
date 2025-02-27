package kdu.assignment.config;

import kdu.assignment.repository.GeoCodingRepository;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableCaching
public class AppConfig {

   @Bean
    GeoCodingRepository geoCodingRepository(){
       return new GeoCodingRepository();
   }
}
