package beans;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "global", schema = "bexp")
public class GlobalEntity {
    private int id;
    private Integer count;
    private Timestamp timestamp;
    private String contractAddress;
    private String apikey;
    private String apiUrl;
    private Integer decimals;
    private String defaultMethod;
    private Integer alarmValue;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "count")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "contract_address")
    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @Basic
    @Column(name = "apikey")
    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    @Basic
    @Column(name = "api_url")
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Basic
    @Column(name = "decimals")
    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    @Basic
    @Column(name = "default_method")
    public String getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(String defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    @Basic
    @Column(name = "alarm_value")
    public Integer getAlarmValue() {
        return alarmValue;
    }

    public void setAlarmValue(Integer alarmValue) {
        this.alarmValue = alarmValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalEntity that = (GlobalEntity) o;
        return id == that.id &&
                Objects.equals(count, that.count) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(contractAddress, that.contractAddress) &&
                Objects.equals(apikey, that.apikey) &&
                Objects.equals(apiUrl, that.apiUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, count, timestamp, contractAddress, apikey, apiUrl);
    }


}
