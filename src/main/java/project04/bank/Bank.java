package project04.bank;

import java.util.HashMap;
import java.util.Map;

/**
 * The bank held by the server: a table of accounts.
 *
 * The map itself is only read after start-up (accounts are pre-created), so
 * no synchronisation is needed on the map — the per-account operations are
 * the ones that must be atomic (see Account).
 */
public class Bank {

    private final Map<Integer, Account> accounts = new HashMap<>();

    public Bank() {
        // Deterministic contents so that every student sees the same results.
        add(new Account(1, "Ada", 1000.00));
        add(new Account(2, "Grace", 25.50));
        add(new Account(3, "Edsger", 0.00));
    }

    private void add(Account account) {
        accounts.put(account.getId(), account);
    }

    /** Returns the account, or null if the id is unknown. */
    public Account find(int accountId) {
        return accounts.get(accountId);
    }

    /**
     * Optional Part 4 — atomic transfer between two accounts.
     * Returns true iff amount could be moved from fromId to toId.
     */
    public boolean transfer(int fromId, int toId, double amount) {
        // TODO — OPTIONAL Part 4: withdraw from one account and deposit into
        // the other as ONE atomic step. You need BOTH account locks at once.
        // Hint: always take the two locks in accountId order — think about
        // what can happen if transfers A->B and B->A each grab their own
        // "from" lock first and then wait for the other lock (deadlock!).
        return false;
    }
}
