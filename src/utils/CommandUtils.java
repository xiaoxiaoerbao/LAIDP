package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class is used to control the number of commands run in parallel
 */
public class CommandUtils {

    /**
     *
     * @param callableList
     * @param threadsNum
     * @return the result type of callable task
     * @param <V> the result type of callable task
     */
    public static <V> List<V> run_commands(List<Callable<V>> callableList, int threadsNum){
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
        List<V> results = new ArrayList<>();
        try {
            List<Future<V>> futureList=executorService.invokeAll(callableList);
            for (Future<V> future : futureList){
                results.add(future.get());
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }


}
