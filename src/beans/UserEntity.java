package beans;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "bexp")
public class UserEntity {
    private int idUser;
    private String name;
    private String walletHash;
    private Integer balance;

    @Id
    @Column(name = "id_user")
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "wallet_hash")
    public String getWalletHash() {
        return walletHash;
    }

    public void setWalletHash(String walletHash) {
        this.walletHash = walletHash;
    }

    @Basic
    @Column(name = "balance")
    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return idUser == that.idUser &&
                Objects.equals(name, that.name) &&
                Objects.equals(walletHash, that.walletHash) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, name, walletHash, balance);
    }
}
