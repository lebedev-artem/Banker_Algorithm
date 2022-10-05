import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Bank bank = new Bank();
        ArrayList<Thread> threads = new ArrayList<>();

//        create Accounts and adding to Bank
        for (int i = 1; i < 200; i++) {
            Account account = new Account(i, getRandomLong(10_000, 51_000));
            bank.addAccountToBank(account);
        }
//        Adding threads
        for (int i = 0; i < 8; i++) {
            Thread threadOne = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    bank.transfer(getRandomInt(1, 199), getRandomInt(1, 199), getRandomLong(200, 30_000));
                }
            });
            threads.add(threadOne);
        }
        long time2Sync = System.currentTimeMillis();
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("\nComputing time with double synchronized var - " + (System.currentTimeMillis() - time2Sync) + " ms");
        System.out.printf(bank.getNumWrTr() + " wrong transactions\n");
    }

    private static Long getRandomLong(long f, long t) {
        return f + (long) (Math.random() * t);
    }

    private static int getRandomInt(int f, int t) {
        return (int) (f + (Math.random() * t));
    }
}
