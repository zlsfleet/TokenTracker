import beans.AlarmEntity;
import beans.RunEntity;
import beans.TransactionEntity;
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

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class TrackerMain {
    private static SessionFactory sf;

    static {
        sf = new Configuration().configure().buildSessionFactory();
    }

    private Global global;

    private Timestamp execTime;

    public TrackerMain() {
        String jsonString;
        JsonObject json;

        global = new Global();

        try {
            // get global settings
            getGlobal();

            // call etherscan api to get latest update of transactions
            jsonString = getHttpResult();
            JsonParser parse = new JsonParser();
            json = (JsonObject) parse.parse(jsonString);

            int status = json.get("status").getAsInt();
            JsonArray result = json.get("result").getAsJsonArray();

            if (result.size() == global.getCount()) {   // check whether there is new transactions
                System.out.println("No new transactions.");
            } else if (status != 1) {   // api call error
                System.out.println("API Error!");
            } else {

                int count = 0;
                String lastHash = "";

                // loop through transactions
                System.out.println(result);
                for (int i = global.getCount(); i < result.size(); i++) {


                    JsonObject transaction = result.get(i).getAsJsonObject();

                    int transactionStatus = transaction.get("txreceipt_status").getAsInt();
                    if (transactionStatus == 1) {   // transaction status ok

                        TransactionEntity entity = new TransactionEntity();

                        String input = transaction.get("input").getAsString();

                        if (isCorrectMethod(input)) {   // is a transfer record

                            entity.setTransHash(transaction.get("blockHash").getAsString());
                            entity.setFromHash(transaction.get("from").getAsString());
                            entity.setToHash(transaction.get("to").getAsString());
                            entity.setTransStatus("OK");
                            entity.setMemo("");

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制
                            Date date = new Date();
                            date.setTime(Long.parseLong(transaction.get("timeStamp").getAsString()) * 1000);
                            entity.setTimestamp(Timestamp.valueOf(simpleDateFormat.format(date)));


                            long value = getValue(input);
                            entity.setAmount(value);

                            updateDatabase(entity);

                            if (value >= global.getAlarmValue()) { // value >= alarm threshold
                                setAlarm(entity.getTransHash());
                            }
                            count++;
                            lastHash = entity.getTransHash();
                        }

                    } else {    //transaction status error
                        System.out.println("Transaction not confirmed.");
                    }

                }
                System.out.println("1");

                updateRun(count, lastHash);
                System.out.println("2");
            }
            //System.out.println("status:" + json.get("status").getAsInt());
            //System.out.println("message:" + json.get("message").getAsString());

            //System.out.println("result:" + result.get(0));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("3");
        System.exit(0);
    }

    private void updateRun(int count, String lastHash) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();

        RunEntity run = new RunEntity();
        run.setTimestamp(execTime);
        run.setLastTransHash(lastHash);
        run.setRecords(count);

        session.save(run);
        tx.commit();
        session.close();
    }

    private void getGlobal() {
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream("global.properties");
            properties.load(in);
            in.close();

            global.setAlarmValue(Long.parseLong(properties.getProperty("alarm_value")));
            global.setApikey(properties.getProperty("apikey"));
            global.setApiURL(properties.getProperty("api_url"));
            global.setContractAddress(properties.getProperty("contract_address"));
            global.setCount(Integer.parseInt(properties.getProperty("count")));
            global.setDecimals(Integer.parseInt(properties.getProperty("decimals")));
            global.setDefaultMethod(properties.getProperty("default_method"));
            global.setTimestamp(Timestamp.valueOf(properties.getProperty("timestamp")));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateDatabase(TransactionEntity transaction) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();

        TransactionEntity entity = new TransactionEntity();

        session.save(transaction);
        tx.commit();
        session.close();

    }

    private void setAlarm(String hash) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();

        AlarmEntity alarm = new AlarmEntity();
        alarm.setHashTrans(hash);
        alarm.setTimestamp(execTime);

        session.save(alarm);
        tx.commit();
        session.close();

        //sendMail(hash);
        //sendSMS();
    }

    private void sendMail(String hash) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("yandex.user", "yandex.password");
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("yandex.user@ya.ru"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("a@grasoff.net"));
            message.setSubject("Subject");
            message.setText("Text");

            Transport.send(message);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private long getValue(String input) {
        String arg2 = input.substring(input.length() - 19);
        Long value = Long.valueOf(arg2, 16) / (long) Math.pow(10, global.getDecimals());
        return value;

    }

    private boolean isCorrectMethod(String input) {
        String method = input.substring(0, 10);
//        System.out.println(method);
        return method.equals(global.getDefaultMethod());

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
        URIBuilder uriBuilder = new URIBuilder(global.getApiURL());

        // set the parameters for api call
        uriBuilder.addParameter("module", "account");
        uriBuilder.addParameter("action", "txlist");
        uriBuilder.addParameter("address", global.getContractAddress());
        uriBuilder.addParameter("startblock", "0");
        uriBuilder.addParameter("endblock", "99999999");
        uriBuilder.addParameter("sort", "asc");
        uriBuilder.addParameter("apikey", global.getApikey());
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
