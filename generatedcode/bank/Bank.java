Bankpublic{
accounts
public-void-depisitOnAccount(String,double){
if (accounts.containsKey(id)) {
  accounts.get(id).deposit(amount);
}
 else {
  System.out.println("The account does not exist!");
}

}
public-void-withdrawfromAccount(String,double){
if (accounts.containsKey(id)) {
  accounts.get(id).withdraw(amount);
}
 else {
  System.out.println("The account does not exist!");
}

}
public-void-display(){
System.out.println("Bank");

}
converter
public-Bank(Converter){
this.converter=c;

}
public-double-convert(int,int,double){
return converter.conv(curSource,curTarget,amount);

}
}