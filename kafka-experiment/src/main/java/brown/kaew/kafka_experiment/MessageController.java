package brown.kaew.kafka_experiment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;

    public MessageController(KafkaTemplate<String, MessageDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String publishMessage(@RequestBody MessageDTO message) {
        kafkaTemplate.send("test-topic", message);
        return "Message published: " + message;
    }
}

