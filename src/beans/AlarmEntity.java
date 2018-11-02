package beans;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "alarm", schema = "bexp")
public class AlarmEntity {
    private int idAlarm;
    private String memo;
    private Integer idTrans;
    private Timestamp timestamp;

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
    @Column(name = "id_trans")
    public Integer getIdTrans() {
        return idTrans;
    }

    public void setIdTrans(Integer idTrans) {
        this.idTrans = idTrans;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
