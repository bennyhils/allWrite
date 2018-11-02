import com.all.write.NetworkMember;
import com.all.write.api.RequestingFileInfo;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class DownloadTest {

    @Test
    public void exchangeTest() {
        String senderAddress = "10.0.0.102:8090";
        String receiverAddress = "10.0.0.102:8091";



        NetworkMember sender = new NetworkMember("todo-key", senderAddress);
        Map<String, NetworkMember> trackerMembers = getNetworkMembersFromTracker("localhost", false, sender);
        Iterator<NetworkMember> iterator = trackerMembers.values().iterator();
        sender = iterator.next();
        NetworkMember receiver = iterator.next();


        createUploadRequest(sender, receiver);

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://" + receiverAddress + "/download?localFilePath=/tmp/" + UUID.randomUUID().toString() + ".txt";

        SecretKey secretKey;

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom(); // cryptograph. secure random
            keyGen.init(random);
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        RequestingFileInfo fileInfo = RequestingFileInfo.createFileInfo("/home/roman/1.txt", sender, secretKey);

        ResponseEntity response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(fileInfo), Object.class);

        assert true;
    }

    private static void createUploadRequest(NetworkMember sender, NetworkMember receiver) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://" + sender.getAddress() + "/uploadRequest" +
                "?fileLocalPath=1.txt";

        ResponseEntity response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(receiver), Object.class);
    }


    public Map<String, NetworkMember> getNetworkMembersFromTracker(String trackerAddress, boolean register, NetworkMember me) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String method = "/tracker/list";
        if (register) {
            method = "/tracker/register";
        }
        String uri = "http://" + trackerAddress + ":8080" + method;
        ResponseEntity<NetworkMember[]> response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(me), NetworkMember[].class);
        List<NetworkMember> memberList = Arrays.asList(response.getBody());

        return memberList.stream()
                .collect(Collectors.toMap(NetworkMember::getPublicKey, i -> i,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}
