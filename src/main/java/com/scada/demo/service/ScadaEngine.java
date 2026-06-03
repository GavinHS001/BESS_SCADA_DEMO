package com.scada.demo.service;

import com.scada.demo.model.BatteryPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Controller // 增加此注解以处理前端 STOMP 消息
public class ScadaEngine {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final List<BatteryPack> batteryPackList = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private int currentMode = 0; 
    private double targetPowerW = 0.0; 
    private boolean isSwitchClosed = false; 
    private final List<Map<String, String>> operationLogs = new ArrayList<>(); 

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 8; i++) {
            batteryPackList.add(new BatteryPack(i, 50.0, 1270.0));
        }
        scheduler.scheduleAtFixedRate(this::pollAndBroadcastData, 0, 2, TimeUnit.SECONDS);
    }

    // 接收前端刀闸控制
    @MessageMapping("/switch")
    public void handleSwitch(Map<String, Object> data) {
        this.isSwitchClosed = (Boolean) data.get("closed");
        String email = (String) data.get("email");
        String action = isSwitchClosed ? "CLOSED" : "OPENED";
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());

        Map<String, String> log = new HashMap<>();
        log.put("operator", email != null && !email.isEmpty() ? email : "Anonymous");
        log.put("action", action);
        log.put("time", timestamp);

        operationLogs.add(0, log);
        if (operationLogs.size() > 3) operationLogs.remove(3);

        if (!isSwitchClosed) {
            this.currentMode = 0;
            this.targetPowerW = 0;
        }

        // 广播状态更新
        messagingTemplate.convertAndSend("/topic/switch", isSwitchClosed);
        messagingTemplate.convertAndSend("/topic/logs", operationLogs);
    }

    // 接收前端功率控制
    @MessageMapping("/control")
    public void handleControl(Map<String, Object> data) {
        if (!isSwitchClosed) return;

        String action = (String) data.get("action");
        if ("stop".equals(action)) {
            this.currentMode = 0;
            this.targetPowerW = 0;
            return;
        }

        double kw = Double.parseDouble(data.get("power").toString());
        if (kw > 1250.0) kw = 1250.0;
        if (kw < -1250.0) kw = -1250.0;
        
        this.targetPowerW = kw * 1000.0; 
        this.currentMode = (targetPowerW == 0) ? 0 : 1;
    }

    private void pollAndBroadcastData() {
        if (!isSwitchClosed) {
            this.currentMode = 0;
            this.targetPowerW = 0;
        }

        double perPackPowerW = this.targetPowerW / 8.0;

        for (BatteryPack pack : batteryPackList) {
            if (targetPowerW != 0 && isSwitchClosed) {
                pack.setPower(perPackPowerW);
                double currentA = perPackPowerW / pack.getVoltage();
                pack.setCurrent(currentA);
                double capacityAh = 300.0;
                double deltaSoc = (currentA * (2.0 / 3600.0) / capacityAh) * 100.0;
                pack.setSoc(pack.getSoc() - deltaSoc);
                double socFactor = (pack.getSoc() - 50.0) / 50.0 * 50.0; 
                double polarization = currentA * 0.2; 
                pack.setVoltage(1270.0 + socFactor + polarization);
            } else {
                pack.setPower(0);
                pack.setCurrent(0);
                pack.setVoltage(pack.getVoltage() + (1270.0 - pack.getVoltage()) * 0.1);
            }
        }

        // 定时推送数据和状态
        messagingTemplate.convertAndSend("/topic/battery", batteryPackList);
        // 发送给新连接的初始状态（这里简化为全网广播）
        messagingTemplate.convertAndSend("/topic/switch", isSwitchClosed);
        messagingTemplate.convertAndSend("/topic/logs", operationLogs);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
    }
}
