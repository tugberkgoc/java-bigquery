
package cloudcode.helloworld.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cloudcode.helloworld.payload.response.MessageResponse;

@RestController
public final class HelloWorldController {

    @GetMapping("/hello")
    public ResponseEntity<?> helloWorld() {
        String message = "Hello World!";
        return ResponseEntity.ok(new MessageResponse(message));
    }

}
