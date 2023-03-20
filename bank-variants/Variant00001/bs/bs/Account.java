
package bs;

/**
 * This is an account class
 * @author aboub_bmdb7gr
 *
 */
public class Account {

	private String id;

	private double balance;

	/**
	 * Exemple of method comment
	 * @param i
	 * @param m
	 */
	public Account(String i, double m) {

		id = i;
		balance = m;
	}

	public void deposit(double amount) {
		this.balance += amount;
	}

	public double getAmount() {
		return balance;
	}

	public void withdraw(double amount) {
		if (amount <= balance) {
			balance -= amount;
		} else {
			System.out.println("Insuffisent balance..!!");
		}
	}

}
