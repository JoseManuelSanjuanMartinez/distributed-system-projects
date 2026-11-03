package project04.bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Part 2 — The client-side proxy.
 *
 * The proxy is the ONLY class in the client that knows the wire protocol.
 * The rest of the client program calls ordinary Java methods
 * (deposit, withdraw, getBalance) and never touches a stream: the proxy
 * MARSHALS the arguments into bytes, sends the request, and UNMARSHALS the
 * response. This is precisely the role the client stub/proxy plays in
 * RPC middleware — in Lab 2 you will write one per remote class.
 *
 * Note that the proxy's Java API does not have to mirror the wire format
 * one-to-one: getBalance() maps the wire pair (found, balance) to a single
 * return value. Designing that mapping is part of designing the proxy.
 *
 * Connection model: ONE TCP connection for the whole session (opened in the
 * constructor, closed by close()). Compare with Lab 2's ORB1, which opens a
 * new connection per call — you will discuss the trade-off there.
 */
public class BankProxy implements AutoCloseable {

    /** Give up on the server if a reply takes longer than this (Part 3(a)). */
    public static final int TIMEOUT_MILLIS = 3000;

    private final Socket socket;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    public BankProxy(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(TIMEOUT_MILLIS);   // reads throw SocketTimeoutException
        socket.setTcpNoDelay(true);            // see footnote in the statement:
                                               // tiny request/response messages +
                                               // Nagle's algorithm = needless latency
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
    }

    /** Returns the balance, or -1.0 if the account does not exist. */
    public double getBalance(int accountId) throws IOException {
        dos.writeInt(BankProtocol.OP_BALANCE);
        dos.writeInt(accountId);
        dos.flush();                                   // one flush() per message: the habit
        boolean found = dis.readBoolean();             // unmarshal, in order
        double balance = dis.readDouble();
        return found ? balance : -1.0;
    }

    /** Returns true if the deposit was accepted. */
    public boolean deposit(int accountId, double amount) throws IOException {
        // TODO: marshal OP_DEPOSIT + accountId + amount (in this order),
        //       flush, then unmarshal boolean ok + double newBalance.
        //       Return ok. Remember: read BOTH response values even if you
        //       only use one — the protocol says they are on the wire!
        int opCode = BankProtocol.OP_DEPOSIT;
        // Start marshalling
        dos.writeInt(opCode);
        dos.writeInt(accountId);
        dos.writeDouble(amount);
        dos.flush();
        // End marshalling
        // Start unmarshalling
        boolean opStatus = dis.readBoolean();
        dis.readDouble();
        // End unmarshalling
        return opStatus;
    }

    /** Returns true if the withdrawal was accepted (enough balance). */
    public boolean withdraw(int accountId, double amount) throws IOException {
        // TODO: same pattern as deposit, with OP_WITHDRAW.
        // Start marshalling
        int opCode = BankProtocol.OP_WITHDRAW;
        dos.writeInt(opCode);
        dos.writeInt(accountId);
        dos.writeDouble(amount);
        dos.flush();
        // End marshalling
        // Start unmarshalling
        boolean opStatus = dis.readBoolean();
        dis.readDouble();
        // End unmarshalling
        return opStatus;
    }

    /** Optional Part 4. Returns true if the transfer was made. */
    public boolean transfer(int fromId, int toId, double amount) throws IOException {
        // TODO — OPTIONAL Part 4: OP_TRANSFER + fromId + toId + amount;
        //       response is boolean ok + double newFromBalance.
        // Start marshalling
        int opCode = BankProtocol.OP_TRANSFER;
        dos.writeInt(opCode);
        dos.writeInt(fromId);
        dos.writeInt(toId);
        dos.writeDouble(amount);
        dos.flush();
        // End marshalling
        // Start unmarshalling
        boolean opStatus = dis.readBoolean();
        dis.readDouble();
        return opStatus;
    }

    /** Tells the server we are done and closes the connection. */
    @Override
    public void close() throws IOException {
        dos.writeInt(BankProtocol.OP_QUIT);
        dos.flush();
        socket.close();
    }
}
