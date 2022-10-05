public class Account {

    private volatile int accNumber;
    private volatile long money;
    private volatile boolean isBlocked;

    public Account(Integer accNumber, long money) {
        this.accNumber = accNumber;
        this.money = money;
        isBlocked = false;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public Integer getAccNumber() {
        return accNumber;
    }

    public synchronized boolean getStatus() {
        return isBlocked;
    }

    public void blockAccount() {
        isBlocked = true;
        setMoney(0);
    }
}
