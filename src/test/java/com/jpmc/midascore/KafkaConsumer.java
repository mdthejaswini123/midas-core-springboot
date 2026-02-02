package com.jpmc.midascore;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Component
public class KafkaConsumer {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    KafkaConsumer(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    public void consume( Transaction transaction ) {

        validateAndProcess(transaction);
        //transactions.add(transaction);
        //System.out.println("🔥 RECEIVED: " + transaction.getAmount());
    }
    public void validateAndProcess(Transaction transaction) {
        //check if the sender and recipient exists
        UserRecord sender= userRepository.findById(transaction.getSenderId());
        UserRecord recipient= userRepository.findById(transaction.getRecipientId());
        if(sender==null || recipient==null)
        {
            return ;
        }
        //check if the sender account balance is less than transaction amount
        else if(sender.getBalance()<transaction.getAmount())
        {
            return;
        }
        //if the transaction is valid
        else {
            //deduct the transaction amount from the sender
            sender.setBalance(sender.getBalance()-transaction.getAmount());
            //add the transaction amount to recipients account
            recipient.setBalance(recipient.getBalance()+transaction.getAmount());
            //save it
            userRepository.save(sender);
            userRepository.save(recipient);
            TransactionRecord record=new TransactionRecord(
                    transaction.getSenderId(),
                    transaction.getRecipientId(),
                    transaction.getAmount()
            );
            //add the successful transaction data to transaction history db
            transactionRepository.save(record);
        }
    }
//    public List<Transaction> getTransactions() {
//        return transactions;
//    }
}
