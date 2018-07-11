package com.example.scalingdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api")
public class ApiController {


    @Value("${vcap.application.application_id:local_guid}")
    private String applicationId;

    private Date bootTime = new Date();

    private List memoryList = new LinkedList();


    @RequestMapping("fill-memory")
    public void fillMemory(){

        memoryList.add(new byte[1024*1024*100]);

    }

    @RequestMapping("info")
    public Map<String, String> memoryInfo(){
        Map<String, String> map = new HashMap<>();

        long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        long maxMemory = Runtime.getRuntime().maxMemory();

        double memoryUsage = Math.round((double)allocatedMemory/maxMemory*10000)/100;

        map.put("bootTime", new SimpleDateFormat("hh:mm:ss a").format(bootTime));
        map.put("applicationId", applicationId);
        map.put("allocatedMemory", allocatedMemory / (1024*1024) + " MB");
        map.put("maxMemory", maxMemory / (1024*1024) + " MB");
        map.put("memoryUsage", memoryUsage + " %");

        return map;
    }
}
