package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class Account {

    private volatile int balance;
    private final int id;
    private final Bank myBank;
    
    public Account(Bank myBank, int id, int initialBalance) {
        this.myBank = myBank;
        this.id = id;
        balance = initialBalance;
    }

    /* 
     * getBalance() does not need to be synchronized, despite balance being
     * a shared variable, because getBalance() does not modify balance. 
     **/
    public int getBalance() {
        return balance;
    }

    /* 
     * withdraw() method is synchronized via synchronized keyword in it's
     * declaration 
     **/
    public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
            //Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    /* 
     * synchronized same as withdraw()
     **/
    public synchronized void deposit(int amount) {
        int currentBalance = balance;
        //Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        
        notifyAll();
    }
    
    /*
     * implementation of task 4
     * this method waits for amount <= balance 
     **/
    public synchronized void waitForSufficientFunds(int amount) {
        
        while (myBank.isOpen() && amount >= balance) {
            try {
                wait();
            } catch (InterruptedException ex) {/*ignore*/}
        }
    }
    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}