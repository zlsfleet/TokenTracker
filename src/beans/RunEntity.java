package beans;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "run", schema = "bexp")
public class RunEntity {
    private int idRun;
    private Timestamp timestamp;
    private String lastTransHash;
    private Integer records;

    @Id
    @Column(name = "id_run")
    public int getIdRun() {
        return idRun;
    }

    public void setIdRun(int idRun) {
        this.idRun = idRun;
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
    @Column(name = "last_trans_hash")
    public String getLastTransHash() {
        return lastTransHash;
    }

    public void setLastTransHash(String lastTransHash) {
        this.lastTransHash = lastTransHash;
    }

    @Basic
    @Column(name = "records")
    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunEntity runEntity = (RunEntity) o;
        return idRun == runEntity.idRun &&
                Objects.equals(timestamp, runEntity.timestamp) &&
                Objects.equals(lastTransHash, runEntity.lastTransHash) &&
                Objects.equals(records, runEntity.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRun, timestamp, lastTransHash, records);
    }
}
