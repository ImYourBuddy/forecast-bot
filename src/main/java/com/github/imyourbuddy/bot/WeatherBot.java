package com.github.imyourbuddy.bot;

import com.github.imyourbuddy.service.WeatherService;
import com.github.imyourbuddy.view.CurrentWeatherView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherBot extends TelegramLongPollingBot {
    private static final String GEO_SET_NAME = "locations";
    @Value("${forecast.bot.token}")
    private String botToken;
    @Value("${forecast.bot.username}")
    private String botUsername;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private Jedis jedis;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    //TODO: use switch case
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.equals("/start")) {
                    sendWelcomeMessage(chatId);
                }
                if (messageText.equals("/currentweather")) {
                    if (jedis.zscore(GEO_SET_NAME, String.valueOf(chatId)) != null) {
                        List<GeoCoordinate> coordinates = jedis.geopos(GEO_SET_NAME, String.valueOf(chatId));
                        if (!coordinates.isEmpty() && coordinates.get(0) != null) {
                            double latitude = coordinates.get(0).getLatitude();
                            double longitude = coordinates.get(0).getLongitude();
                            sendForecastMessage(chatId, latitude, longitude);
                        } else {
                            SendMessage message = requestLocation(chatId);
                            sendMessage(message);
                        }
                    } else {
                        SendMessage message = requestLocation(chatId);
                        sendMessage(message);
                    }
                }
            } else if (update.getMessage().hasLocation()) {
                Location userLocation = update.getMessage().getLocation();
                jedis.geoadd(GEO_SET_NAME, userLocation.getLongitude(), userLocation.getLatitude(), String.valueOf(chatId));
                sendForecastMessage(chatId, userLocation.getLatitude(), userLocation.getLongitude());
            }
        }
    }

    //TODO: create MessageSender class
    private void sendWelcomeMessage(long chatId) {
        String welcomeText = """
                Добро пожаловать!
                Вот команды, которые вы можете использовать:
                /currentweather - Получить прогноз погоды на текущий момент.""";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(welcomeText);
        sendMessage(message);
    }

    private void sendForecastMessage(long chatId, double latitude, double longitude) {
        CurrentWeatherView currentWeather = weatherService.getCurrentWeather(latitude, longitude);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(currentWeather.toString());
        sendMessage(message);
    }

    private SendMessage requestLocation(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Пожалуйста предоставьте ваши текущие координаты");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow rowLocation = new KeyboardRow();
        KeyboardButton locationButton = new KeyboardButton("Предоставить координаты");
        locationButton.setRequestLocation(true);
        rowLocation.add(locationButton);

        KeyboardRow rowCancel = new KeyboardRow();
        KeyboardButton cancelButton = new KeyboardButton("Отмена");
        rowCancel.add(cancelButton);

        keyboard.add(rowLocation);
        keyboard.add(rowCancel);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
