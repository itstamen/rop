package com.rop;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * 
 * @version 1.0, 13-12-12 下午3:24
 * @user: Angus
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 5 * 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());

       threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run......");
            }
        });

//        if (!future.isDone())
//            future.get(5, TimeUnit.SECONDS);

        System.out.println("aaa");
    }
}
