

package bs; 

public   class  Account {
	
	private String id;

	
	private double balance;

	

   public Account(String i, double m){
	   
	   id=i;
	   balance=m;
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

	
	
	private int currency;

	
	
	public int getCurrency(){
		
		return currency;
	}


}
