package com.system.assessment;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.temporal.Temporal;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;



@Slf4j
public class Test {
    public static void main(String[] args) {

        byte[] data = new byte[]{
                (byte) 0xac, (byte) 0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x0d,
                0x6a, 0x61, 0x76, 0x61, 0x2e, 0x74, 0x69, 0x6d, 0x65, 0x2e,
                0x53, 0x65, (byte) 0x95, (byte) 0x5d, (byte) 0x84, (byte) 0xba,
                (byte) 0x1b, 0x22, 0x48, (byte) 0xb2, 0x0c, 0x00, 0x00, 0x78,
                0x70, 0x77, 0x0a, 0x03, 0x00, 0x00, 0x0a, (byte) 0xe8, 0x0a,
                0x1f, 0x78
        };

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            // 反序列化
            Object obj = ois.readObject();

            // 如果反序列化的对象是时间类（例如 LocalDateTime 或 ZonedDateTime）
            if (obj instanceof Temporal) {
                Temporal temporal = (Temporal) obj;
                System.out.println("Deserialized time: " + temporal);
            } else {
                System.out.println("Object is not of type Temporal.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}



