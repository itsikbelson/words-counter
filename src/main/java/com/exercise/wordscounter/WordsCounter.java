package com.exercise.wordscounter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by itsik on 8/14/17.
 * <p>
 * Words counter
 */
public class WordsCounter {

    private static final int MAX_THREADS_COUNT = 20;
    private static final int INITIAL_CAPACITY = 100;

    private Map<String, LongAdder> wordsCount = new ConcurrentHashMap<>(INITIAL_CAPACITY);

    public static void main(String[] args) {
        WordsCounter wc = new WordsCounter(); // load text files in parallel
        wc.load(args);
        wc.displayStatus();
    }

    public void load(String... fileNames) {
        ForkJoinPool pool = new ForkJoinPool(getParallelism(fileNames.length));
        try {
            pool.submit(() -> Arrays.stream(fileNames).parallel().forEach(this::countWords)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayStatus() {
        System.out.println(getStatusDisplay());
    }

    private String getStatusDisplay() {
        StringBuilder statusDisplayBuilder = new StringBuilder();
        String mapDisplay = wordsCount.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(countEntry -> countEntry.getKey() + " " + countEntry.getValue().sum() + System.lineSeparator())
                .collect(Collectors.joining());
        long totalCount = wordsCount.values().stream().mapToLong(LongAdder::sum).sum();
        statusDisplayBuilder.append(mapDisplay)
                .append(System.lineSeparator())
                .append("** total: ").append(totalCount);
        return statusDisplayBuilder.toString();
    }

    private int getParallelism(int numberOfTasks) {
        return Math.max(numberOfTasks, MAX_THREADS_COUNT);
    }

    private void countWords(String fileName) {
        //It is possible to add parallelism within the file,
        //but several tests showed that it's not improving overall performance,
        //so decided to stay with a single thread parsing the whole file
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            Stream<String> wordsStream = lines.flatMap(line -> Arrays.stream(line.split(" "))).filter(word -> !word.isEmpty()).map(String::toLowerCase);
            wordsStream.forEach(this::incrementWordCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void incrementWordCount(String word) {
        wordsCount.putIfAbsent(word, new LongAdder());
        wordsCount.get(word).increment();
    }
}
