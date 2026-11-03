package project04.bank;

import java.io.IOException;

/**
 * Part 3(b) — Two clients hammer the SAME account in parallel.
 *
 * Each thread opens its own session (so the server runs two BankSession
 * threads) and deposits 1.00 into account 3, one thousand times.
 * Expected final balance: exactly startingBalance + 2000.0.
 *
 * Without synchronized on Account.deposit (as shipped) and with the pause(1) line
 * uncommented, deposits get lost (the Práctica 1 Counter race, now with money): a balance
 * below the expected value, different on every run.
 */
public class BankStressDemo {

    private static final int DEPOSITS_PER_CLIENT = 1000;

    public static void main(String[] args) throws Exception {
        double before;
        try (BankProxy p = new BankProxy("localhost", BankProtocol.PORT)) {
            before = p.getBalance(3);
        }

        Runnable clientTask = () -> {
            try (BankProxy proxy = new BankProxy("localhost", BankProtocol.PORT)) {
                for (int i = 0; i < DEPOSITS_PER_CLIENT; i++) {
                    proxy.deposit(3, 1.0);
                }
            } catch (IOException e) {
                System.out.println("client failed: " + e.getMessage());
            }
        };

        Thread client1 = new Thread(clientTask);
        Thread client2 = new Thread(clientTask);
        client1.start();
        client2.start();
        client1.join();      // wait for both, as in Práctica 1
        client2.join();

        try (BankProxy proxy = new BankProxy("localhost", BankProtocol.PORT)) {
            double after = proxy.getBalance(3);
            System.out.println("balance(3) before = " + before);
            System.out.println("balance(3) after  = " + after
                    + "   (expected " + (before + 2 * DEPOSITS_PER_CLIENT) + ")");
        }
    }
}
