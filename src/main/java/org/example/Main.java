package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        Thread textGenerator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    for (int j = 0; j < "abc".length(); j++) {
                        queue1.put(text);
                        queue2.put(text);
                        queue3.put(text);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread[] threads = new Thread[3];

        threads[0] = new Thread(() -> {
            char letter = 'a';
            int maxA = findMaxCharCount(queue1, letter);
            System.out.println("Максимальное количество " + letter + ": " + maxA);
        });
        threads[1] = new Thread(() -> {
            char letter = 'b';
            int maxB = findMaxCharCount(queue2, letter);
            System.out.println("Максимальное количество " + letter + ": " + maxB);
        });
        threads[2] = new Thread(() -> {
            char letter = 'c';
            int maxC = findMaxCharCount(queue3, letter);
            System.out.println("Максимальное количество " + letter + ": " + maxC);
        });

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        long finish = System.currentTimeMillis();
        System.out.println("Потребовалось " + (finish - start) + " сек.");

    }

    private static int findMaxCharCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10_000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted");
            return -1;
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();

    }

}
//    Создайте в статических полях три потокобезопасные блокирующие очереди;
//    Создайте поток, который наполнял бы эти очереди текстами;
//    Создайте по потоку для каждого из трёх символов 'a','b' и 'c',
//    которые разбирали бы свою очередь и выполняли подсчёты.