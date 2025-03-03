# Kafka Producer - Detailed Explanation
This project demonstrates a Kafka Producer using Spring Boot. The producer sends messages to Kafka topics, including simple text messages and complex JSON objects.

1. Kafka Producer Configuration (KafkaProducerConfig.java)
 -> The KafkaProducerConfig class is a Spring Configuration Class responsible for setting up the Kafka producer.
   (KafkaProducerConfig.java)

    Key Components:

        1. Creating a Kafka Topic

        @Bean
        public NewTopic createTopic() {
        return new NewTopic("Specific-topic", 3, (short) 1);
        }

      Defines a Kafka topic named Specific-topic with 3 partitions and a replication factor of 1.

        2. Producer Configuration Properties
        
            @Bean
            public Map<String, Object> producerConfig() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return props;
            }

      BOOTSTRAP_SERVERS_CONFIG: Specifies the Kafka broker (localhost:9092).

      KEY_SERIALIZER_CLASS_CONFIG: Serializes keys as String.

      VALUE_SERIALIZER_CLASS_CONFIG: Serializes values as JSON using JsonSerializer.class.

        3. Creating a Producer Factory
        
        @Bean
        public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
        }
      A ProducerFactory is created using the configuration properties.

        4. Creating a Kafka Template
        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
        }
      The KafkaTemplate is used to send messages to Kafka.

2. Event Controller (EventController.java)

   The EventController class exposes REST endpoints to send messages to Kafka.

   -> Send a simple text message

        @GetMapping("/publish/{message}")
        public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
          for (int i = 0; i <= 100000; i++) {
            publisher.sendMessageToTopic(message + i);
            }
            return ResponseEntity.ok("Message published successfully ...");
       } catch (Exception ex) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
       }
    Sends 100,000 messages to the topic Zoo using KafkaMessagePublisher.

   -> Send a JSON Object

       @PostMapping("/publish/customerDetails")
       public ResponseEntity<?> sendObject(@RequestBody Customer customer) {
       try {
           publisher.sendMessage(customer);
         return ResponseEntity.ok("Message sent successfully....");
         } catch (Exception ex) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }
       }
   
   -> Send a message to a specific partition
   
       @GetMapping("/publishTo/{message}")
       public ResponseEntity<?> sendMessageToPartition(@PathVariable String message) {
       try {
           publisher.sendMessageToSpecificTopic(message);
               return ResponseEntity.ok("Message sent successfully....");
           } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
             }
         }

    -> Sends a message to specific partitions of Specific-topic.


3. Kafka Message Publisher

    -> The KafkaMessagePublisher is responsible for sending messages to Kafka.

    -> Send a Simple Message

        public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<String, Object>> future = template.send("Zoo", message);
          future.whenComplete((result, ex) -> {
          if (ex == null) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
          } else {
                System.out.println("Unable to send message=[" + message + "] due to: " + ex.getMessage());
                }
              });
        }

    -> Send a JSON Object

        public void sendMessage(Customer customer) {
        CompletableFuture<SendResult<String, Object>> future = template.send("Object-5", customer);
                          future.whenComplete((result, ex) -> {
                    if (ex == null) {
                          System.out.println("Sent message=[" + customer + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                    } else {
                          System.out.println("Unable to send message=[" + customer + "] due to: " + ex.getMessage());
                    }
                });
        }

    -> Sends a Customer object as JSON to the Object-5 topic.
    -> Send Message to Specific Partitions

        public void sendMessageToSpecificTopic(String message) {
          template.send("Specific-topic", 0, null, message);
          template.send("Specific-topic", 2, null, message);
          template.send("Specific-topic", 1, null, message);
        }

    -> Sends the message to specific partitions (0, 1, 2) of Specific-topic.

    Summary

    KafkaProducerConfig: Configures Kafka producer and creates the KafkaTemplate.

    EventController: Exposes REST APIs to publish messages.

    Customer: Represents a customer entity serialized as JSON.

    KafkaMessagePublisher: Sends messages to different Kafka topics and partitions.

    Running the Kafka Producer

      1. Start Zookeeper and Kafka Broker:

         zookeeper-server-start.sh config/zookeeper.properties
         kafka-server-start.sh config/server.properties
   
      2. Run the Spring Boot application.

      3. Use Postman or a browser to test the APIs. 
   
-> Important Linux Commands
1. Start Zookeeper Server

    sh bin/zookeeper-server-start.sh config/zookeeper.properties

2. Start Kafka Server / Broker

    sh bin/kafka-server-start.sh config/server.properties

3. Create topic

    sh bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic NewTopic --partitions 3 --replication-factor 1

4. list out all topic names

    sh bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

5. Describe topics

    sh bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic NewTopic

6. Produce message

    sh bin/kafka-console-producer.sh --broker-list localhost:9092 --topic NewTopic

7. consume message

    sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic NewTopic --from-beginning
