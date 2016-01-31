package com.tws.iqfeed.feed;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by admin on 1/30/2016.
 */
public class Test {
    public static void main(String[] args){
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("iqfeed-spring.xml");
        Level1Feed feed = (Level1Feed) ctx.getBean("level1Feed");
        feed.startFeed();
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
