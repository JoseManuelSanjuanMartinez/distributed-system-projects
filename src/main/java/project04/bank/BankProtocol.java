package project04.bank;

/**
 * The Acme Bank wire protocol, v1 — shared constants.
 *
 * A binary protocol is a CONTRACT: both sides must agree on every byte.
 * Keeping the opcodes in one class shared by client and server is the
 * cheapest way to honour that contract (compare with .proto files in gRPC,
 * which you will meet at the end of Lab 2).
 *
 * All integers and doubles travel in network byte order (big-endian);
 * DataOutputStream/DataInputStream guarantee this. Strings travel in
 * modified UTF-8 via writeUTF/readUTF.
 */
public final class BankProtocol {

    public static final int PORT = 9945;

    // ---- opcodes (request header, sent as writeInt) ----
    public static final int OP_QUIT     = 0;   // payload: none            | response: none (server closes)
    public static final int OP_BALANCE  = 1;   // payload: int accountId   | response: boolean found, double balance
    public static final int OP_DEPOSIT  = 2;   // payload: int, double     | response: boolean ok, double newBalance
    public static final int OP_WITHDRAW = 3;   // payload: int, double     | response: boolean ok, double balance
    public static final int OP_TRANSFER = 4;   // payload: int, int, double| response: boolean ok, double newFromBalance

    private BankProtocol() { }   // constants only — never instantiated
}
