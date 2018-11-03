import beans.GlobalEntity;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Timestamp;


public class TrackerMain {
    private static SessionFactory sf;

    static {
        sf = new Configuration().configure().buildSessionFactory();
    }

    private String apikey;
    private String contractAddress;
    private String apiURL;
    private String lastTime;
    private int decimals;
    private int lastCount;
    private String defaultMethod;


    Timestamp execTime;

    public TrackerMain() {
        String rs;
        JsonObject json;

        try {
            Session session = sf.openSession();
            Transaction tx = session.beginTransaction();
            GlobalEntity u = session.get(GlobalEntity.class, 1);

            apikey = u.getApikey();
            contractAddress = u.getContractAddress();
            apiURL = u.getApiUrl();
            lastTime = u.getTimestamp().toString();
            lastCount = u.getCount();
            decimals = u.getDecimals();

            System.out.println(apikey);
            System.out.println(contractAddress);
            System.out.println(apiURL);
            System.out.println(lastTime);
            System.out.println(lastCount);

            tx.commit();
            session.close();
            sf.close();

            rs = getHttpResult();

            JsonParser parse = new JsonParser();
            json = (JsonObject) parse.parse(rs);

            int status = json.get("status").getAsInt();
            JsonArray result = json.get("result").getAsJsonArray();

            if (result.size() == lastCount) {
                System.out.println("No new transactions.");
            } else if (status != 1) {
                System.out.println("API Error!");

            } else {
                for (int i = lastCount; i < result.size(); i++) {
                    JsonObject transaction = result.get(i).getAsJsonObject();

                    int transactionStatus = transaction.get("txreceipt_status").getAsInt();
                    if (transactionStatus == 1) {
                        
                        String blockHash = transaction.get("blockHash").getAsString();
                        String fromHash = transaction.get("from").getAsString();
                        String toHash = transaction.get("to").getAsString();
                        String timeStamp = transaction.get("timeStamp").getAsString();
                        String input = transaction.get("input").getAsString();
                        if (isCorrectMethod(input)) {
                            long value = getValue(input);
                            System.out.println(value);
                        }

                    } else {
                        //System.out.println("Transaction not confirmed.");
                    }

                }
            }
            //System.out.println("status:" + json.get("status").getAsInt());
            //System.out.println("message:" + json.get("message").getAsString());

            //System.out.println("result:" + result.get(0));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private long getValue(String input) {
        String arg2 = input.substring(input.length() - 64);
        return Long.parseLong(arg2) / (long) Math.pow(10, decimals);

    }

    private boolean isCorrectMethod(String input) {
        String method = input.substring(0, 10);
        return method.equals(defaultMethod);

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
        URIBuilder uriBuilder = new URIBuilder(apiURL);

        uriBuilder.addParameter("module", "account");
        uriBuilder.addParameter("action", "txlist");
        uriBuilder.addParameter("address", contractAddress);
        uriBuilder.addParameter("startblock", "0");
        uriBuilder.addParameter("endblock", "99999999");
        uriBuilder.addParameter("sort", "asc");
        uriBuilder.addParameter("apikey", apikey);

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
