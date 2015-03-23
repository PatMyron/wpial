package Project1;

/**
a bank account which can be changed with deposits
and withdrawals
*/
public class BankAccount
{

//private data field - data is ALWAYS private

private double balance;


/**
constructor for a new account with no deposit
no return type; same name as class         */
public BankAccount()
{
  balance = 0.0;
}

/**
constructs a bank account with a given balance
@param a double representing the new balance
*/
public BankAccount(double d)
{
  balance = d;
}

/**
make a deposit to the account and update the balance
@param a double representing the new deposit amount
public so it can be accessed to deposit money!         */
public void deposit(double d)
{
  balance  += d;
}

/**
withdraw specified amount of money
assumes that specified amount is in account
@param double for the amount to withdraw
*/
public void withdraw(double d)
{
  balance -= d;
}

/**
 gets the amount currently in the account
 @return the current balance
*/
public double getBalance()
{
  return balance;
}

// end of class
} 