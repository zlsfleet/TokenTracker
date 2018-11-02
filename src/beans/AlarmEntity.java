package beans;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "alarm", schema = "bexp", catalog = "")
public class AlarmEntity {
    private int idAlarm;
    private String memo;

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
}
