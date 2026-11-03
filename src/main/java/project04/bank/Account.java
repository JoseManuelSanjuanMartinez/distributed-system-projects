package project04.bank;

/**
 * A bank account. The balance never goes below zero (a rule this práctica adds to Práctica 0).
 *
 * Part 3(b): several client sessions run in parallel threads (one thread per
 * client, as in Práctica 3), so two threads may operate on the SAME account at
 * once. Is this class safe as it stands? Run the experiment in Part 3(b)
 * before you answer — then fix it (you already know how, from Práctica 1).
 *
 * (Real banks do not store money in a double — they use integer cents or
 * BigDecimal. A double is fine for this exercise.)
 */
public class Account {

    private final int id;
    private final String owner;
    private double balance;

    public Account(int id, String owner, double initialBalance) {
        this.id = id;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public synchronized double getBalance() {
        return balance;
    }

    /** Adds amount (must be > 0) and returns true, or refuses and returns false. */
    public synchronized boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        double newBalance = balance + amount;   // (1) read and compute
        pause(1);   // Part 3(b): uncomment this line to
        //             // make the lost-update race easy to observe (the pause
        //             // plays the role of a slow audit write between (1) and (2))
        balance = newBalance;                   // (2) write back
        return true;
    }

    /** Helper for the Part 3(b) experiment only. */
    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Withdraws amount if the balance allows it; the balance never goes negative. */
    public synchronized boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance = balance - amount;
        return true;
    }
}
