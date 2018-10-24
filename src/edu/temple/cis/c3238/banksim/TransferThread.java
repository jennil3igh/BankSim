package edu.temple.cis.c3238.banksim;
/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
import java.io.*; 
import java.util.*;

class TransferThread extends Thread {

    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;

    public TransferThread(Bank b, int from, int max) {
        bank = b;
        fromAccount = from;
        maxAmount = max;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            int toAccount = (int) (bank.size() * Math.random());
            int amount = (int) (maxAmount * Math.random());
            
            synchronized(bank){
            bank.transfer(fromAccount, toAccount, amount);
            }
        }
        
        /* when one bank closes, it calls closeBank (closing them all) */
        bank.closeBank();
    }
}
//AHHH
//testing