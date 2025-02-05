package secureWebApp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;


public class TelegramBotService {
	private final static long CHAT_ID = 877356668;
	private final static String BOT_TOKEN = "7800487700:AAGVoblpiFikq5qEyPP7kCzgVKwys_PHmB8";
    private final TelegramBot bot;

    public TelegramBotService() {
        this.bot = new TelegramBot(BOT_TOKEN);
    }

    public void sendMessage(String messageText) {
        SendMessage message = new SendMessage(CHAT_ID, messageText);

        SendResponse response = bot.execute(message);

        if (response.isOk()) {
            System.out.println("Messaggio inviato con successo a chat ID: " + CHAT_ID);
        } else {
            System.out.println("Errore nell'invio del messaggio: " + response.description());
        }
    }
}
