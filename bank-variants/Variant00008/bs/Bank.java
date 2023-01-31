package bs; 
import java.util.ArrayList; 
import java.util.HashMap; 

public   class  Bank {
	
	private java.util.HashMap<java.lang.String,bs.Account> accounts;

	
	public void depisitOnAccount(String id, double amount) {
		if (accounts.containsKey(id)) {
			accounts.get(id).deposit(amount);
		} else {
			System.out.println("The account does not exist!");
		}
	}

	

	public void withdrawfromAccount(String id, double amount) {
		if (accounts.containsKey(id)) {
			accounts.get(id).withdraw(amount);
		} else {
			System.out.println("The account does not exist!");
		}
	}

	

	private void  display__wrappee__Conversion  (){
		System.out.println("Bank");
	}

	
	
	public void display(){
		display__wrappee__Conversion();
		System.out.println("Consortium");
		
	}

	
	
	private Converter converter;

	
	
	public Bank(Converter c){
		
		this.converter=c;
	}

	
	public double convert(int curSource, int curTarget, double amount) {
		return converter.conv(curSource, curTarget, amount);
	}

	
	private Consortium cons;

	
	public Bank(Consortium c){
		this.cons=c;
	}


}
