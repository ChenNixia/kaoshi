package com.ruoyi.modules.monitor.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/behavior")
    @SendTo("/topic/monitor")
    public String analyzeBehavior(String behaviorData) {
        if ("screen-switch".equals(behaviorData)) {
            return "Cheating detected: Screen switched!";
        }
        return "No cheating behavior detected.";
    }
}    