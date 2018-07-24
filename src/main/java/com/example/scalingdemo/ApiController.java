package com.example.scalingdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api")
public class ApiController {


    @Value("${vcap.application.application_id:#{null}}")
    private String applicationId;

    private Date bootTime = new Date();

    private List<byte[]> memoryList = new LinkedList();


    @RequestMapping("fill-memory")
    public synchronized void fillMemory(){

        int fillPercentage = 90;
        int megaByteSize = 1024*1024;

        long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        long maxMemory = Runtime.getRuntime().maxMemory();

        long fillAmount = (maxMemory*fillPercentage/100)-allocatedMemory;

        System.out.println("AMemory : " + allocatedMemory + " / " + maxMemory);
        System.out.println("Allocating additional memory by " + fillAmount);

        for (int i = 0; i < fillAmount/megaByteSize; i++) {
            memoryList.add(new byte[megaByteSize]);
        }

    }

    @RequestMapping("free-memory")
    public synchronized void freeMemory(){

        memoryList = new LinkedList<>();
    }

    @RequestMapping("info")
    public Map<String, String> memoryInfo(){
        Map<String, String> map = new HashMap<>();

        long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        long maxMemory = Runtime.getRuntime().maxMemory();

        double memoryUsage = Math.round((double)allocatedMemory/maxMemory*10000)/100;

        map.put("bootTime", new SimpleDateFormat("hh:mm:ss a").format(bootTime));
        map.put("applicationId", applicationId);
        map.put("allocatedJvmMemory", allocatedMemory / (1024*1024) + " MB");
        map.put("maxJvmMemory", maxMemory / (1024*1024) + " MB");
        map.put("memoryUsage", memoryUsage + "%");
        map.put("maxContainerMemory", System.getenv("MEMORY_LIMIT"));

        return map;
    }
}
