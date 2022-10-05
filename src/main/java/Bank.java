import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Bank extends Thread {

    private final ConcurrentMap<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final Random random = new Random();
    long fraudLimit = 300_000;
    int numWrngTrnsct = 0; //NumberWarningTransactions

    public Bank() {
    }

    public void addAccountToBank(Account account) {
        accounts.put(account.getAccNumber(), account);
    }

    public synchronized long getBalance(Integer accountNum) {
        System.out.printf("\nAccount number: " + accountNum + ". Balance: " + accounts.get(accountNum).getMoney() + " p.");
        return accounts.get(accountNum).getMoney();
    }

    public synchronized boolean isFraud(Account from, Account to, long amount) {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (random.nextBoolean()) {
            from.blockAccount();
            to.blockAccount();
        }
        return random.nextBoolean();
    }

    public int getNumWrTr() {
        return numWrngTrnsct;
    }

    public long getSumAllAccounts() {
        long sumAllAcc = 0;
        for (Map.Entry<Integer, Account> item : accounts.entrySet()) {
            sumAllAcc += item.getValue().getMoney();
        }
        System.out.println("\nTotal amount on all accounts: " + sumAllAcc + " p.");
        return sumAllAcc;
    }

    private static String stringOutputBefore(Account from, Account to, long amount) {
        return Thread.currentThread().getName()
                + " " + from.getAccNumber() + " " + (from.getMoney() - amount)
                + " " + to.getAccNumber() + " " + (to.getMoney() + amount);
    }

    private static String stringOutputAfter(Account from, Account to) {
        return Thread.currentThread().getName()
                + " " + from.getAccNumber() + " " + from.getMoney()
                + " " + to.getAccNumber() + " " + to.getMoney();
    }

    public void transfer(Integer fromAccountNum, Integer toAccountNum, long amount) {
        Account from = accounts.get(fromAccountNum);
        Account to = accounts.get(toAccountNum);
        Account lockFirst = from;
        Account lockSecond = to;

        if (fromAccountNum > toAccountNum) {
            lockFirst = to;
            lockSecond = from;
        }
        synchronized (lockFirst) {
            synchronized (lockSecond) {
                String before = stringOutputBefore(from, to, amount);
                if (!from.getStatus() && !to.getStatus() && from.getMoney() >= amount
                        && !Objects.equals(from.getAccNumber(), to.getAccNumber())) {
                    from.setMoney(from.getMoney() - amount);
                    to.setMoney(to.getMoney() + amount);
                    if (!before.equals(stringOutputAfter(from, to))) {
                        numWrngTrnsct += 1;
                    }
                }
            }
            if (amount > fraudLimit) {
                isFraud(from, to, amount);
            }
        }
    }
}
