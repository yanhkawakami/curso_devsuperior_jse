package entities;

public class Account {

    public static double DEPOSIT_FEE_PERCENTAGE = 0.02;

    private Long id;
    private Double balance;

    public Account() {}

    public Account(Long id, Double balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void deposit(Double amount) {
        if (amount > 0) {
            balance += amount - (amount * DEPOSIT_FEE_PERCENTAGE);
        }
    }

    public void withdraw(Double amount) {
        if (amount > balance){
            throw new IllegalArgumentException("Insufficient balance");
        }
        balance -= amount;
    }

    public Double fullWithdraw() {
        double aux = balance;
        balance = 0.0;
        return aux;
    }
}
