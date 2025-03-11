package kr.mayb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static java.util.Map.entry;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackAlarmService {
    private static final String SLACK_API_ENDPOINT = "https://slack.com/api/chat.postMessage";

    private final RestTemplate restTemplate;

    @Value("${slack.token}")
    private String slackToken;
    @Value("${slack.channel}")
    private String slackChannel;
    @Value("${slack.username}")
    private String slackUsername;

    private void sendSlackAlarm(String message) {
        Map<String, Object> slackData = Map.ofEntries(
                entry("channel", slackChannel),
                entry("username", slackUsername),
                entry("icon_emoji", ":earth_americas:"),
                entry("text", message)
        );
        send(slackData);
    }

    public void sendTestAlarm() {
        String message = """
                *ServerSide slack message*
                >MemberId : %d
                >Email : %s
                >Name : %s
                                
                """.formatted(
                1,
                "parkky3563@gmail.com",
                "건영"
        );

        sendSlackAlarm(message);
    }

    private void send(Map<String, Object> slackData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(slackToken);
        HttpEntity<Map<String, Object>> body = new HttpEntity<>(slackData, headers);

        try {
            restTemplate.postForEntity(SLACK_API_ENDPOINT, body, String.class);
        } catch (Exception e) {
            log.error("Slack alarm error: {}", e.getMessage());
        }
    }
}
