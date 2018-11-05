package beans;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "alarm", schema = "bexp")
public class AlarmEntity {
    private int idAlarm;
    private String memo;
    private Timestamp timestamp;
    private String hashTrans;

    @Id
    @Column(name = "id_alarm")
    public int getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(int idAlarm) {
        this.idAlarm = idAlarm;
    }

    @Basic
    @Column(name = "memo")
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmEntity that = (AlarmEntity) o;
        return idAlarm == that.idAlarm &&
                Objects.equals(memo, that.memo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlarm, memo);
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
    @Column(name = "hash_trans")
    public String getHashTrans() {
        return hashTrans;
    }

    public void setHashTrans(String hashTrans) {
        this.hashTrans = hashTrans;
    }
}
