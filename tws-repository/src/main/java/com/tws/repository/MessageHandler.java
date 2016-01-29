package com.tws.repository;

import com.google.common.util.concurrent.Striped;
import com.tws.activemq.CassandraMessageListener;
import com.tws.cassandra.repository.TickRepository;
import com.tws.shared.MsgType;
import com.tws.shared.marshall.Marshaller;
import com.tws.shared.model.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Created by chris on 1/18/16.
 */
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private CassandraMessageListener messageListener;
    private Striped<Lock> striped = Striped.lock(1024);

    @Autowired
    private TickRepository tickRepository;

    public void handle() {
        Lock lock = null;
        TextMessage msg = messageListener.take();
        int type;
        try {
            type = msg.getIntProperty("type");
            lock = striped.get(type);
            lock.lock();
            MsgType msgType = MsgType.get(type);
            Marshaller marshaller = (Marshaller) msgType.marshaller();
            Tick tick = (Tick) marshaller.unmarshal(msg.getText());
            tickRepository.save(tick);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }


    @Required
    public void setMessageListener(CassandraMessageListener messageListener) {
        this.messageListener = messageListener;
    }
}