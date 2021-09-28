package api.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author HuanJu
 * @date 2021/9/27 22:54
 */
public class SleepUtils {

    public static void sleep(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
