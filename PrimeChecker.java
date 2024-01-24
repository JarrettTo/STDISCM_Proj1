import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class PrimeChecker {
    private static List<Integer> primes = Collections.synchronizedList(new ArrayList<>());
    private static int LIMIT;
    private static int numThreads;
    
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        /*System.out.print("Enter the upper bound: ");
        LIMIT = scanner.nextInt();
        System.out.print("Enter the number of threads: ");
        numThreads = scanner.nextInt();*/
        for(int x=0;x<8;x++){
        LIMIT=10000000;
        numThreads=65536*2;
        //scanner.close();
        if (LIMIT < 2 || numThreads < 1) {
            System.out.println("Invalid input. Please enter a limit >= 2 and number of threads >= 1.");
            return;
        }

        long startTime = System.currentTimeMillis();
        List<int[]> ranges = partitionRange(LIMIT, numThreads);

        
        
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int start = ranges.get(i)[0];
            int end = ranges.get(i)[1];
            threads[i] = new Thread(() -> findAndAddPrimes(start, end));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = endTime - startTime;

        System.out.println("Runtime: " + elapsedTimeMillis + " milliseconds");
        System.out.println("Number of primes: " + primes.size());
        primes= Collections.synchronizedList(new ArrayList<>());
        }
    }
    private static List<int[]> partitionRange(int limit, int numThreads) {
        List<int[]> ranges = new ArrayList<>();
  
        int chunkSize = limit / numThreads;
        int remaining = limit % numThreads;
        
        int start = 2;
        for (int i = 0; i < numThreads; i++) {
            int end = start + chunkSize - 1 + (i < remaining ? 1 : 0); // Distribute the remainder
            if (end > limit) end = limit;
            ranges.add(new int[]{start, end});
            start = end + 1;
        }
    
        return ranges;
    }
    private static void findAndAddPrimes(int start, int end) {
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                synchronized (primes) {
                    primes.add(i);
                }
            }
        }
    }
    private static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

}
