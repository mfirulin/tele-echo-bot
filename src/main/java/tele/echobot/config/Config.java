package tele.echobot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@Data
@PropertySource("application.properties")
public class Config {

    @Value("${bot.name}") 
    private String botName;

    @Value("${bot.token}") 
    private String botToken;
}
