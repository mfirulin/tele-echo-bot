package tele.echobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import tele.echobot.service.Bot;

@SpringBootApplication
public class EchobotApplication implements CommandLineRunner {

	@Autowired
	private Bot bot;

	public static void main(String[] args) {
		SpringApplication.run(EchobotApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
	}
}
