package com.client.kafka.Service;

import com.client.kafka.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    @Autowired
    private KafkaTemplate<String, Object> template;

    public void sendMessageToTopic(String message)
    {
        CompletableFuture<SendResult<String, Object>> future = template.send("Zoo", message);
        future.whenComplete((result,ex)-> {
            if(ex == null) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            else {
                System.out.println("Unable to send message=[" + message + "] due to :" + ex.getMessage());
            }
        });

    }

    public void sendMessage(Customer customer)
    {
        CompletableFuture<SendResult<String, Object>> future = template.send("Object-5", customer);
        future.whenComplete((result,ex)-> {
            if(ex == null) {
                System.out.println("Sent message=[" + customer + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            else {
                System.out.println("Unable to send message=[" + customer + "] due to :" + ex.getMessage());
            }
        });

    }

    //Sending message to a specific partition of a  topic. Here I am sending message to 3 different partition but
    // my aim is to read from a only single partition.
    public void sendMessageToSpecificTopic(String message)
    {
        template.send("Specific-topic",0, null, message);
        template.send("Specific-topic",2, null, message);
        template.send("Specific-topic",2, null, message);
        template.send("Specific-topic",0, null, message);
        template.send("Specific-topic",1, null, message);
        template.send("Specific-topic",2, null, message);
        template.send("Specific-topic",0, null, message);
        template.send("Specific-topic",2, null, message);
        template.send("Specific-topic",0, null, message);
        template.send("Specific-topic",2, null, message);
    }
}
