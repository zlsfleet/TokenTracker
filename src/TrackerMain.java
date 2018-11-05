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
    private long alarmValue;

    private Timestamp execTime;

    public TrackerMain() {
        String jsonString;
        JsonObject json;
        int count = 0;

        try {
            // get global settings
            getGlobal();

            // call etherscan api to get latest update of transactions
            jsonString = getHttpResult();
            JsonParser parse = new JsonParser();
            json = (JsonObject) parse.parse(jsonString);

            int status = json.get("status").getAsInt();
            JsonArray result = json.get("result").getAsJsonArray();

            if (result.size() == lastCount) {   // check whether there is new transactions
                System.out.println("No new transactions.");
            } else if (status != 1) {   // api call error
                System.out.println("API Error!");
            } else {
                // loop through transactions
                for (int i = lastCount; i < result.size(); i++) {
                    JsonObject transaction = result.get(i).getAsJsonObject();

                    int transactionStatus = transaction.get("txreceipt_status").getAsInt();
                    if (transactionStatus == 1) {   // transaction status ok

                        String blockHash = transaction.get("blockHash").getAsString();
                        String fromHash = transaction.get("from").getAsString();
                        String toHash = transaction.get("to").getAsString();
                        String timeStamp = transaction.get("timeStamp").getAsString();
                        String input = transaction.get("input").getAsString();

                        if (isCorrectMethod(input)) {   // is a transfer record

                            long value = getValue(input);
                            updateDatabase(transaction);

                            if (value >= this.alarmValue) { // value >= alarm threshold
                                setAlarm(transaction);
                            }
                            count++;
                        }

                    } else {    //transaction status error
                        System.out.println("Transaction not confirmed.");
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

    private void getGlobal() {
        // new hibernate session
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();

        GlobalEntity u = session.get(GlobalEntity.class, 1);

        // get settings from bexp.global
        apikey = u.getApikey();
        contractAddress = u.getContractAddress();
        apiURL = u.getApiUrl();
        lastTime = u.getTimestamp().toString();
        lastCount = u.getCount();
        decimals = u.getDecimals();
        defaultMethod = u.getDefaultMethod();
        alarmValue = u.getAlarmValue();

//            System.out.println(apikey);
//            System.out.println(contractAddress);
//            System.out.println(apiURL);
//            System.out.println(lastTime);
//            System.out.println(lastCount);

        tx.commit();
        session.close();
        sf.close();
    }

    private void updateDatabase(JsonObject transaction) {
    }

    private void setAlarm(JsonObject transaction) {
    }

    private long getValue(String input) {
        String arg2 = input.substring(input.length() - 19);
        Long value = Long.valueOf(arg2, 16) / (long) Math.pow(10, decimals);
        return value;

    }

    private boolean isCorrectMethod(String input) {
        String method = input.substring(0, 10);
//        System.out.println(method);
        return method.equals(this.defaultMethod);

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

        // set the parameters for api call
        uriBuilder.addParameter("module", "account");
        uriBuilder.addParameter("action", "txlist");
        uriBuilder.addParameter("address", contractAddress);
        uriBuilder.addParameter("startblock", "0");
        uriBuilder.addParameter("endblock", "99999999");
        uriBuilder.addParameter("sort", "asc");
        uriBuilder.addParameter("apikey", apikey);
        //System.out.println(uriBuilder.toString());

        HttpGet httpget = new HttpGet(uriBuilder.build());

        String json = null;
        HttpResponse response = closeableHttpClient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            execTime = new Timestamp(System.currentTimeMillis());   // record call timestamp
            json = EntityUtils.toString(entity, "UTF-8").trim();
        }

        return json;
    }
}
