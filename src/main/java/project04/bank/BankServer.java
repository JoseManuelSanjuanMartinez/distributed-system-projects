package project04.bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Part 2 — The Acme Bank server.
 *
 * Structure (nothing new): the accept() loop and the thread-per-client scheme
 * are exactly Práctica 3's multithreaded echo server. What is new is INSIDE
 * the session: instead of echoing text lines, the server reads a typed,
 * opcode-based request and dispatches it with a switch — the same dispatcher
 * pattern you will grow into a mini-ORB in Lab 2.
 *
 * Run with --slow to simulate an overloaded server (BALANCE takes 5 s):
 * used by the client's timeout experiment in Part 3(a).
 */
public class BankServer {

    public static void main(String[] args) throws IOException {
        boolean slowMode = args.length > 0 && args[0].equals("--slow");
        Bank bank = new Bank();

        try (ServerSocket serverSocket = new ServerSocket(BankProtocol.PORT)) {
            System.out.println("BankServer listening on port " + BankProtocol.PORT
                    + (slowMode ? " (SLOW MODE)" : ""));
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);   // send small replies immediately
                // One thread per client, as in Práctica 3. (In Lab 2 / ORB7 you
                // will replace this with a thread pool and learn why.)
                new Thread(new BankSession(socket, bank, slowMode)).start();
            }
        }
    }
}

/**
 * One client session: reads requests until OP_QUIT, end of stream, or an
 * unknown opcode. Package-private: only the server uses it.
 */
class BankSession implements Runnable {

    private final Socket socket;
    private final Bank bank;
    private final boolean slowMode;

    BankSession(Socket socket, Bank bank, boolean slowMode) {
        this.socket = socket;
        this.bank = bank;
        this.slowMode = slowMode;
    }

    @Override
    public void run() {
        String client = socket.getRemoteSocketAddress().toString();
        System.out.println("session started: " + client);
        try (Socket s = socket;
             DataInputStream dis = new DataInputStream(s.getInputStream());
             DataOutputStream dos = new DataOutputStream(s.getOutputStream())) {

            boolean sessionOpen = true;
            while (sessionOpen) {
                int opcode = dis.readInt();          // blocks until the next request
                switch (opcode) {
                    case BankProtocol.OP_QUIT:
                        sessionOpen = false;
                        break;
                    case BankProtocol.OP_BALANCE:
                        balance(dis, dos);
                        break;
                    case BankProtocol.OP_DEPOSIT:
                        deposit(dis, dos);
                        break;
                    case BankProtocol.OP_WITHDRAW:
                        withdraw(dis, dos);
                        break;
                    case BankProtocol.OP_TRANSFER:
                        transfer(dis, dos);
                        break;
                    default:
                        // Part 2, question P2: after an unknown opcode we no longer know
                        // where the next request starts — the only safe
                        // reaction is to drop the session.
                        System.out.println("unknown opcode " + opcode + " from "
                                + client + " — closing session");
                        sessionOpen = false;
                        break;
                }
            }
        } catch (EOFException e) {
            // The client closed (or died) between requests: normal on a binary
            // stream — readInt() signals end-of-stream with EOFException,
            // NOT with null (that is BufferedReader.readLine()).
            System.out.println("client " + client + " closed the connection");
        } catch (IOException e) {
            System.out.println("session " + client + " aborted: " + e.getMessage());
        }
        System.out.println("session ended: " + client);
    }

    // ---- one handler per opcode: read payload IN ORDER, reply IN ORDER ----

    private void balance(DataInputStream dis, DataOutputStream dos) throws IOException {
        int accountId = dis.readInt();
        if (slowMode) {simulateOverload();}
        Account account = bank.find(accountId);
        dos.writeBoolean(account != null);
        dos.writeDouble(account != null ? account.getBalance() : 0.0);
        dos.flush();                       // one flush() per message: the habit
    }

    private void deposit(DataInputStream dis, DataOutputStream dos) throws IOException {
        // TODO: unmarshal int accountId + double amount (IN ORDER);
        //       find the account; try the deposit; reply
        //       boolean ok + double newBalance; flush.
        int accountId = dis.readInt();
        double amount = dis.readDouble();
        if (slowMode) {simulateOverload();}
        Account account = bank.find(accountId);
        dos.writeBoolean(account != null && account.deposit(amount));
        dos.writeDouble(account != null ? account.getBalance() : 0.0); // Send account balance if found; 0.0 i.o.c.
        dos.flush();
    }

    private void withdraw(DataInputStream dis, DataOutputStream dos) throws IOException {
        // TODO: same pattern as deposit, calling Account.withdraw.
        int accountId = dis.readInt();
        double amount = dis.readDouble();
        if (slowMode) {simulateOverload();}
        Account account = bank.find(accountId);
        dos.writeBoolean(account != null && account.withdraw(amount));
        dos.writeDouble(account != null ? account.getBalance() : 0.0);
        dos.flush();
    }

    private void transfer(DataInputStream dis, DataOutputStream dos) throws IOException {
        // TODO — OPTIONAL Part 4: unmarshal int from + int to +
        //       double amount; call bank.transfer; reply boolean ok +
        //       double newFromBalance; flush.
        int fromId = dis.readInt();
        int toId = dis.readInt();
        double amount = dis.readDouble();
        if (slowMode) {simulateOverload();}
        Account fromAccount = bank.find(fromId);
        Account toAccount = bank.find(toId);
        if (fromAccount != null && toAccount != null) {
            double finalBalance;
            // Implementation of a Monitor
            // 1. First lock both resources
            // 2. Move funds
            // IMPORTANT NOTICE: the locking order of resources should be the same
            // for every operation, i.e. transfer(A -> B) locks the resources in
            // the same order as trasnfer(B -> A), as to not produce deadlocks
            synchronized (fromId < toId ? fromAccount : toAccount) {
                synchronized (fromId < toId ? toAccount : fromAccount) {
                    fromAccount.withdraw(amount);
                    toAccount.deposit(amount);
                    finalBalance =  fromAccount.getBalance();
                }
            }
            dos.writeBoolean(true);
            dos.writeDouble(finalBalance);
        } else {
            dos.writeBoolean(false);
            dos.writeDouble(fromAccount !=  null ? fromAccount.getBalance() : 0.0);
        }
        dos.flush();
    }

    private void simulateOverload() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
