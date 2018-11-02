package beans;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transaction", schema = "bexp")
public class TransactionEntity {
    private int idTrans;
    private String transHash;
    private String transStatus;
    private String fromHash;
    private String toHash;
    private Integer timestamp;
    private Integer amount;
    private String memo;

    @Id
    @Column(name = "id_trans")
    public int getIdTrans() {
        return idTrans;
    }

    public void setIdTrans(int idTrans) {
        this.idTrans = idTrans;
    }

    @Basic
    @Column(name = "trans_hash")
    public String getTransHash() {
        return transHash;
    }

    public void setTransHash(String transHash) {
        this.transHash = transHash;
    }

    @Basic
    @Column(name = "trans_status")
    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    @Basic
    @Column(name = "from_hash")
    public String getFromHash() {
        return fromHash;
    }

    public void setFromHash(String fromHash) {
        this.fromHash = fromHash;
    }

    @Basic
    @Column(name = "to_hash")
    public String getToHash() {
        return toHash;
    }

    public void setToHash(String toHash) {
        this.toHash = toHash;
    }

    @Basic
    @Column(name = "timestamp")
    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "amount")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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
        TransactionEntity that = (TransactionEntity) o;
        return idTrans == that.idTrans &&
                Objects.equals(transHash, that.transHash) &&
                Objects.equals(transStatus, that.transStatus) &&
                Objects.equals(fromHash, that.fromHash) &&
                Objects.equals(toHash, that.toHash) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(memo, that.memo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTrans, transHash, transStatus, fromHash, toHash, timestamp, amount, memo);
    }
}
