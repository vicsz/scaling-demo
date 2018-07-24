package com.example.scalingdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api")
public class ApiController {


    @Value("${vcap.application.application_id:#{null}}")
    private String applicationId;

    private Date bootTime = new Date();

    private List<byte[]> memoryList;

    private List<Thread> threadList = new ArrayList<>();

    @RequestMapping("fill-memory")
    public synchronized void fillMemory(){

        memoryList = new LinkedList<>();

        int fillPercentage = 90;
        int megaByteSize = 1024*1024;

        long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        long maxMemory = Runtime.getRuntime().maxMemory();

        long fillAmount = (maxMemory*fillPercentage/100)-allocatedMemory;
        System.out.println("Allocating additional memory by " + fillAmount);

        for (int i = 0; i < fillAmount/megaByteSize; i++) {
            memoryList.add(new byte[megaByteSize]);
        }
    }

    @RequestMapping("free-memory")
    public synchronized void freeMemory(){

        memoryList = new LinkedList<>();
    }


    @RequestMapping("fill-cpu")
    public synchronized void fillCpu(){

        freeCpu();

        threadList = new ArrayList<>();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            Thread thread = new Thread(this::getLargestPrime);
            thread.start();
            threadList.add(thread);
        }
    }

    @RequestMapping("free-cpu")
    public synchronized void freeCpu(){

        for (Thread thread : threadList)
        {
            System.out.println("Stopping Threads");
            thread.interrupt();
        }

    }


    //checks whether an int is prime or not.
    private int getLargestPrime() {

        int largestPrime = 1;

        for(int i=3;i<Integer.MAX_VALUE;i++){
            if(isPrime(i))
                largestPrime=i;

            if(Thread.interrupted())
                break;
        }

        System.out.println("Largest Prime returned : " + largestPrime);

        return largestPrime;
    }

    private boolean isPrime(int n) {
        for(int i=2;i<n;i++) {
            if(n%i==0)
                return false;
        }
        return true;
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
        map.put("cpuUsage", Math.round(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()) + "%");

        return map;
    }
}
