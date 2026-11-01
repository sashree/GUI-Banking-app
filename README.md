# NexaBank

NexaBank is a **desktop banking simulator** written in Java 17+ with a custom-painted Swing interface. It was built as a B.Tech Object-Oriented Programming project.

It is an **educational application**. It does not connect to a real bank, move real money, or qualify as production financial software.

## What it does

- Sign in with account number and PIN
- Open a new local demo account
- Dashboard with balances, totals, and recent activity
- Deposit, withdraw, and transfer funds
- Searchable, filterable transaction history
- Change PIN (stored as a salted PBKDF2 hash)
- Persist accounts and the ledger in a local SQLite file

## Requirements (macOS)

- JDK 17 or newer (`java -version`)
- Apache Maven 3.8+ (`mvn -version`)
- Apple Silicon and Intel Macs are both supported

Install a JDK if needed:

```bash
brew install openjdk@17
```

On macOS, point the shell at the real JDK (the `/usr/bin/java` stub is not enough):

```bash
export JAVA_HOME="$(/usr/libexec/java_home)"
export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"
```

## How to run

From the project root:

```bash
cd "/Users/sashreekchatterjee/Desktop/banking app"
mvn -q package
java -jar target/nexabank.jar
```

Or compile and launch without packaging:

```bash
mvn -q compile exec:java -Dexec.mainClass=com.nexabank.NexaBankApp
```

The SQLite database is created at:

```text
~/.nexabank/nexabank.db
```

Closing and reopening the app reloads the same accounts and transactions.

## Demo accounts

| Account number | PIN  | Holder         |
|----------------|------|----------------|
| 1001001001     | 1234 | Priya Sharma   |
| 1001001002     | 5678 | Arjun Mehta    |
| 1001001003     | 9012 | Ananya Reddy   |

These accounts are inserted only when the database is empty. After the first launch they keep whatever balances you create.

## Tests

```bash
mvn -q test
```

The tests cover PIN hashing, input validation, deposits, overdraft rejection, atomic transfers, and account registration. They use a temporary SQLite file, not your personal `~/.nexabank` data.

## Project layout

```text
src/main/java/com/nexabank/
  NexaBankApp.java          Application entry (Swing EDT)
  model/                    Account and transaction domain
  service/                  Auth, banking facade, demo seeder
  service/operation/        Polymorphic deposit/withdraw/transfer
  repository/               SQLite access
  exception/                Checked banking errors
  util/                     PIN hashing, money, IDs, validation
  ui/                       Frames, panels, custom Swing widgets
```

## Major classes

| Class | Role |
| --- | --- |
| `NexaBankApp` | Starts the UI on the Event Dispatch Thread and wires services |
| `AbstractAccount` | Encapsulated account state; `credit` / `debit` / `canDebit` |
| `SavingsAccount` | Concrete account type with a zero minimum balance |
| `Transaction` | Immutable ledger row |
| `Auditable` | Interface implemented by accounts and transactions |
| `BankOperation` | Abstract command; subclasses override `perform` |
| `DepositOperation` / `WithdrawOperation` / `TransferOperation` | Concrete operations |
| `BankingService` | Facade that executes operations inside one SQLite transaction |
| `AuthService` | Login, registration, PIN change |
| `DatabaseManager` | Schema, connections, commit/rollback |
| `AccountRepository` / `TransactionRepository` | Persistence |
| `PinHasher` | PBKDF2-HMAC-SHA256 with a random salt |
| `LoginFrame` / `MainFrame` | Top-level windows |
| `DashboardPanel` and other `ui.panels` classes | Feature screens |
| `RoundedButton`, `CardPanel`, `GradientPanel` | Custom Java2D painting |

## OOP concepts demonstrated

1. **Classes and objects** — accounts, transactions, services, UI frames
2. **Constructors** — full reconstruction from the database vs. a new account
3. **Constructor overloading** — `SavingsAccount` and `AbstractAccount`
4. **Encapsulation** — private fields, getters, PIN credentials replaced only through `replacePinCredentials`
5. **Inheritance** — `SavingsAccount extends AbstractAccount`; operation subclasses
6. **Method overriding** — `getAccountType()`, `getMinimumBalance()`, `perform()`
7. **Polymorphism** — `BankingService.execute(BankOperation)`
8. **Abstraction** — `Auditable` interface and `BankOperation` / `AbstractAccount`
9. **Exception handling** — checked `BankingException` hierarchy
10. **Collections** — `ArrayList` of transactions and accounts
11. **Database connectivity** — SQLite over JDBC
12. **Event handling** — `ActionListener`, document listeners, mouse hover
13. **Java Swing** — frames, tables, timers, custom painting

## Design notes for viva

- Money uses `BigDecimal`, never `float` or `double`.
- Transfers update both accounts and both ledger rows inside `DatabaseManager.inTransaction`.
- PINs are not stored in plaintext.
- The GUI does not contain banking rules; panels call `AuthService` and `BankingService`.
- `SwingUtilities.invokeLater` is used in `NexaBankApp.main`.

## Example viva questions

**Q. Why is `AbstractAccount` abstract?**  
A. Shared balance rules live in one place, while product-specific rules such as minimum balance are overridden by `SavingsAccount`. That is inheritance plus abstraction without extra unused subclasses.

**Q. How does polymorphism appear in transfers vs deposits?**  
A. Each action is a `BankOperation`. `BankingService.execute` calls `perform` without knowing which subclass it received.

**Q. Why `BigDecimal`?**  
A. Binary floating-point cannot represent most decimal rupees exactly. `BigDecimal` with scale 2 keeps ledger math exact for a classroom demo.

**Q. How is the PIN protected?**  
A. A random salt is stored next to a PBKDF2 hash. Login re-hashes the typed PIN and compares it in constant time.

**Q. What happens if a transfer fails halfway?**  
A. The work runs in one SQLite transaction. Any `BankingException` triggers rollback, so neither account is left half-updated.

**Q. Where is encapsulation in the UI?**  
A. Frames never touch SQL. They call services. Repositories never paint Swing components.

**Q. Is this safe for real banking?**  
A. No. It is a local simulator without network security, audit compliance, or a real payments rail.

## Testing checklist

- [ ] Application launches to the branded login screen
- [ ] Invalid account or PIN shows an error and does not open the dashboard
- [ ] Demo user `1001001001` / `1234` reaches the dashboard
- [ ] Dashboard shows name, account number, live clock, and four stat cards
- [ ] Deposit of `500.25` increases available balance and appears in history
- [ ] Withdrawal larger than the balance is rejected
- [ ] Withdrawal of `0` or `-1` is rejected
- [ ] Transfer to the same account is rejected
- [ ] Transfer to an unknown account is rejected
- [ ] Transfer to `1001001002` changes both balances
- [ ] History search and type filter narrow the table
- [ ] Change PIN, sign out, and sign in with the new PIN
- [ ] Create a new demo account and see a generated 10-digit number
- [ ] Quit and reopen: previous balances and transactions are still there
- [ ] Window can be resized without overlapping the sidebar into unusable layout

## Resetting local data

```bash
rm -rf ~/.nexabank
```

The next launch recreates the three demo accounts.
