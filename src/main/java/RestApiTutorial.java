import com.google.gson.Gson;

import java.net.SocketOption;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestApiTutorial {
    public static void main(String[] args) throws Exception {
        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://www.youtube.com/redirect?event=video_description&redir_token=QUFFLUhqbER4Vk55SXY3UDFCa2NYN2JocF9INDBSYTU5Z3xBQ3Jtc0trdk9jZC1ua24yZkpPYXZfNVM4b08wX2RiY0tHNDNwNU5hbjhFd1FZZFJWLXNPZkhLNjhYU1NkOHBWSi1zdERWeHhDdk9vUkJxbkFFejFLbWFxQlJpbThKTjJBd2RyeGFoZHFBTVhKTzNwdTB4WURLWQ&q=https%3A%2F%2Fgithub.com%2Fjohnmarty3%2FJavaAPITutorial%2Fblob%2Fmain%2FThirsty.mp4%3Fraw%3Dtrue&v=9oq7Y8n1t00");

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", Constants.API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(), Transcript.class);
        System.out.println(transcript.getId());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", Constants.API_KEY)
                .build();

        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());

            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }

            Thread.sleep(1000);
        }

        System.out.println("Transcription completed!");
        System.out.println(transcript.getText());
    }
}
