package project04.bank;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Part 2/3 — Scripted client. With the server freshly started (normal mode),
 * the output must be exactly the transcript given in the statement.
 *
 * Note how readable this is: every remote operation looks like a local
 * method call, because the proxy hides all marshalling.
 */
public class BankClientDemo {

    public static void main(String[] args) {
        try (BankProxy bank = new BankProxy("localhost", BankProtocol.PORT)) {

            System.out.println("balance(1)        = " + bank.getBalance(1));       // 1000.0
            System.out.println("deposit(1, 250)   = " + bank.deposit(1, 250.0));   // true
            System.out.println("balance(1)        = " + bank.getBalance(1));       // 1250.0
            System.out.println("withdraw(2, 100)  = " + bank.withdraw(2, 100.0));  // false (only 25.5)
            System.out.println("withdraw(2, 20)   = " + bank.withdraw(2, 20.0));   // true
            System.out.println("balance(2)        = " + bank.getBalance(2));       // 5.5
            System.out.println("balance(7)        = " + bank.getBalance(7));       // -1.0 (no such account)
            System.out.println("transfer(1->3, 500) = " + bank.transfer(1, 3, 500.0)); // true
            System.out.println("balance(3)        = " + bank.getBalance(3));       // 500.0

        } catch (SocketTimeoutException e) {
            // Part 3(a): with the server in --slow mode you land here.
            // All we know is that no reply arrived within TIMEOUT_MILLIS.
            // Crashed? Overloaded? Network down? From this side of the wire
            // we CANNOT tell: remember T2's uncertainty model, and T2's failure
            // detector, which is built on this same timeout idea.
            System.out.println("TIMEOUT: no reply within " + BankProxy.TIMEOUT_MILLIS
                    + " ms — server slow, crashed or unreachable (we cannot know which)");
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }
}
