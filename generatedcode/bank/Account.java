Accountpublic{
id
balance
public-Account(String,double){
id=i;

balance=m;

}
public-void-deposit(double){
this.balance+=amount;

}
public-double-getAmount(){
return balance;

}
public-void-withdraw(double){
if (amount <= balance) {
  balance-=amount;
}
 else {
  System.out.println("Insuffisent balance..!!");
}

if (amount <= balance + limit) {
  balance-=amount;
}
 else {
  System.out.println("Insuffisent balance..!!");
}

}
limit
public-double-getLimit(){
return limit;

}
currency
public-int-getCurrency(){
return currency;

}
}