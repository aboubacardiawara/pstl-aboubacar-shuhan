public class Bank {
  private java.util.HashMap<java.lang.String,bs.Account> accounts;
  public void depisitOnAccount(  String id,  double amount){
    if (accounts.containsKey(id)) {
      accounts.get(id).deposit(amount);
    }
 else {
      System.out.println("The account does not exist!");
    }
  }
  public void withdrawfromAccount(  String id,  double amount){
    if (accounts.containsKey(id)) {
      accounts.get(id).withdraw(amount);
    }
 else {
      System.out.println("The account does not exist!");
    }
  }
  public void display(){
    System.out.println("Bank");
  }
}
