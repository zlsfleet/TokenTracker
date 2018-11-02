import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.sql.Timestamp;


public class TrackerMain {
    public static final String APIKEY = "4R7V8WHSWN8HWZWVPYD5QFSY4B6QN6VJ7M";
    public static final String CONTRACT_ADDRESS = "0x292ee3b58ce3f007b9ee88605b76033eaa60cbde";
    public static final String API_URL = "http://api.etherscan.io/api";

    Timestamp execTime;

    public TrackerMain() {
        String rs = "";
        try {

            rs = getHttpResult();
            parseJSON(rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void parseJSON(String json) {
        JsonParser parse = new JsonParser();
        JsonObject jsonObject = (JsonObject) parse.parse(json);
        System.out.println("status:" + jsonObject.get("status").getAsInt());
        System.out.println("message:" + jsonObject.get("message").getAsString());

        JsonArray result = jsonObject.get("result").getAsJsonArray();
        System.out.println("result:" + result.get(0));

    }

    public static void main(String args[]) {
        new TrackerMain();
    }

    public String getHttpResult() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //httpClientBuilder =  HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0");
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        URIBuilder uriBuilder = new URIBuilder(API_URL);

        uriBuilder.addParameter("module", "account");
        uriBuilder.addParameter("action", "txlist");
        uriBuilder.addParameter("address", CONTRACT_ADDRESS);
        uriBuilder.addParameter("startblock", "0");
        uriBuilder.addParameter("endblock", "99999999");
        uriBuilder.addParameter("sort", "asc");
        uriBuilder.addParameter("apikey", APIKEY);

        System.out.println(uriBuilder.toString());
        HttpGet httpget = new HttpGet(uriBuilder.build());

        String json = null;
        HttpResponse response = closeableHttpClient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            execTime = new Timestamp(System.currentTimeMillis());

            json = EntityUtils.toString(entity, "UTF-8").trim();
        }

        return json;
    }
}
