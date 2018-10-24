package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.ReentrantLock;
/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private final ReentrantLock rlock = new ReentrantLock();

    private boolean open = true;
    
    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount) {
   
        if (!open) {
            return;
        }
        
        /* waiting for amount <= balance via method call */
        accounts[from].waitForSufficientFunds(amount);
        
        /* ReentrantLock used to manipulate account balances */
        rlock.lock();
        try {
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
        }
        finally {
            rlock.unlock();
        }
        
        if (shouldTest()) {
            test();
        }

}

    public void test() {
        
        int sum = 0;
        
        /*
         * the shared part of this method is "account.getBalance"
         * hence, we lock that part with a ReentrantLock
         **/
        rlock.lock();
        
        try {
            
            for (Account account : accounts) {
                System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
            
        } 
        
        finally {
            rlock.unlock();
        }
        
        System.out.println(Thread.currentThread().toString() + 
                " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + 
                    " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + 
                    " The bank is in balance");
        }
    }

    
    public int size() {
        return accounts.length;
    }
    
    
    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }
    
    /* returns true when the account is open, false otherwise */    
    public synchronized boolean isOpen() {
        return open;
    }
    
    /* method to satisfy Task 5; 
     * closeBank() closes all accounts */
    public void closeBank() {
        
        /* the open variable is shared and hence protected */
        synchronized (this) {
            open = false;
        }
        
        /* wake up all waiting threads */
        for (Account account : accounts) {
            synchronized (account) {
                account.notifyAll();
            }
        }
    }

}