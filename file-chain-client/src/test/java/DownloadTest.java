import com.all.write.NetworkMember;
import com.all.write.api.RequestingFileInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class DownloadTest {
    public static void main(String[] args) {
        NetworkMember sender = new NetworkMember("todo-key", "localhost:8090");
        createUploadRequest(sender);

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://localhost:8090/download?localFilePath=/tmp/" + UUID.randomUUID().toString() + ".txt";

        RequestingFileInfo fileInfo = RequestingFileInfo.createFileInfo("/home/roman/1.txt", sender, null);

        ResponseEntity response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(fileInfo), Object.class);

        assert true;
    }

    private static void createUploadRequest(NetworkMember sender) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://localhost:8090/uploadRequest?fileLocalPath=/home/roman/1.txt";

        ResponseEntity response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(sender), Object.class);
    }
}
