package org.flhy.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.sxdata.jingwei.dao.CommonDao;
import org.sxdata.jingwei.service.Impl.CommonServiceImpl;

import java.sql.SQLOutput;



public class newTest {
    private int i = 10;
    private Object object = new Object();

    public static void main(String[] args) {

        System.out.println("111111111111");

        Thread thread = new Thread();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        Thread thread3 = new Thread();
        Thread thread4 = new Thread();

        thread.start();





    }


    class MyThread extends Thread{
        @Override
        public void run() {

            synchronized (object) {
                i++;
                System.out.println("i:"+i);
                try {
                    System.out.println("线程"+Thread.currentThread().getName()+"进入睡眠状态");
                    Thread.currentThread().sleep(10000);
                } catch (InterruptedException e) {
                    // TODO: handle exception
                }
                System.out.println("线程"+Thread.currentThread().getName()+"睡眠结束");
                i++;
                System.out.println("i:"+i);
            }
        }
    }
}
