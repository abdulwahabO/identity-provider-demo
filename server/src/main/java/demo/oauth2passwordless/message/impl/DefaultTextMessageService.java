package demo.oauth2passwordless.message.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import demo.oauth2passwordless.message.TextMessageService;

@Service
public class DefaultTextMessageService implements TextMessageService {

    private Logger logger = LoggerFactory.getLogger(TextMessageService.class);

    private static final String HOST = "www.bulksmsnigeria.com";
    private static final String API_ENDPOINT = "/api/v1/sms/create";
    private static final String SENDER_ID = "Login";

    @Value("${bulkSmsApiToken}")
    private String apiToken;

    @Override
    public boolean sendOneTimePassword(String password, String number) {

        try {
            String body = "use " + password + " to login";
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(HOST)
                    .setPath(API_ENDPOINT)
                    .setParameter("api_token", apiToken)
                    .setParameter("from", SENDER_ID)
                    .setParameter("to", number)
                    .setParameter("body", body)
                    .build();

            HttpPost post = new HttpPost(uri);
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(post);

            return response.getStatusLine().getReasonPhrase().equals("OK");
        } catch (URISyntaxException | IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
