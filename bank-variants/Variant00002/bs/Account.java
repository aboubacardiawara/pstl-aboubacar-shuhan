
package bs;

public class Account {

	private String id;

	private double balance;

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
		if (amount <= balance + limit) {
			balance -= amount;
		} else {
			System.out.println("Insuffisent balance..!!");
		}
	}

	private double limit;

	public double getLimit() {
		return limit;
	}

}
