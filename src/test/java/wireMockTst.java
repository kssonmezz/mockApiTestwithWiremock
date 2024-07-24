import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import okhttp3.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class wireMockTst {

    WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
    OkHttpClient client = new OkHttpClient.Builder()
            .build();

    @BeforeEach
    public void setup() {
        wireMockServer.start();
        configurestubFor();
        configurestubForPost();

    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
  public void wiremock_ruler_test() throws IOException {

        //set the body used for port request
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{ \"firstName\": \",Kubra\", \"lastName\": \"Sonmez\" }";
        RequestBody body = RequestBody.create(json, JSON);

        //create a test post request
        Request request2 = new Request
                .Builder()
                .url("http://localhost:8080/createUser")
                .method("POST",body)
                .build();
        Response response2= client.newCall(request2).execute();

        //Assert this request' response
        System.out.println(response2.body());
        Assertions.assertEquals(201, response2.code());

        //create a get request
        Request request = new Request
                .Builder()
                .url("http://localhost:8080/getAllUsers")
                .method("GET",null)
                .build();
        Response response= client.newCall(request).execute();

        //Assert this request' response
        Assertions.assertEquals(200, response.code());
        ResponseBody responseBody = response.body();
        String responseBodyString = responseBody != null ? responseBody.string() : null;
        Assertions.assertEquals("{ \"id\": 1,\n" +
                "      \"firstName\": \"Emily\",\n" +
                "      \"lastName\": \"Johnson\",\n" + "}", responseBodyString);


    }

    //configuration for getAllUsers mock api
    private void configurestubFor() {
        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/getAllUsers"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("{ \"id\": 1,\n" +
                                "      \"firstName\": \"Emily\",\n" +
                                "      \"lastName\": \"Johnson\",\n" + "}")));

    }
    //configuration for createUser mock api
    private void configurestubForPost() {
        configureFor("localhost", 8080);
        stubFor(post(urlEqualTo("/createUser"))
                .withRequestBody(matchingJsonPath("$.firstName", matching(".*")))
                .withRequestBody(matchingJsonPath("$.lastName", matching(".*")))
                .withRequestBody(notMatching("$.extraField")) // Ekstra alanlara izin verilmez
                .willReturn(created()
                .withBody("User Created")
                .withHeader("content-type","application/plain")));


    }

}





