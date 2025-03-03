package com.client.kafka.Controller;

import com.client.kafka.Service.KafkaMessagePublisher;
import com.client.kafka.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

    @Autowired
    private KafkaMessagePublisher publisher;

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message)
    {
        try{
            for(int i=0; i<=100000; i++)
            {
                publisher.sendMessageToTopic(message + i);
            }

            return ResponseEntity.ok("message published successfully ...");
        }catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/publish/customerDetails")
    public ResponseEntity<?> sendObject (@RequestBody Customer customer)
    {
        try
        {
            publisher.sendMessage(customer);
            return ResponseEntity.ok("Message sent successfully....");
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/publishTo/{message}")
    public ResponseEntity<?> sendMessageToPartition(@PathVariable String message)
    {
        try
        {
            publisher.sendMessageToSpecificTopic(message);
            return ResponseEntity.ok("Message sent successfully....");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }



}
