package com.exercise.wordscounter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by itsik on 8/14/17.
 * <p>
 * Test Words Counter
 */
public class WordsCounterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private WordsCounter wordsCounter = new WordsCounter();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void testFilesFromExercise() throws Exception {
        checkWordCount("sample1");
    }

    @Test
    public void testRepeatingTheSameFile() throws Exception {
        checkWordCount("sample2", 8);
    }

    @Test
    public void testSameWord1MillionTimes() throws Exception {
        checkWordCount("sample3", 100);
    }

    @Test
    public void testRealLifeContent() throws Exception {
        checkWordCount("sample4");
    }

    @Test
    public void testLargeContent() throws Exception {
        checkWordCount("sample5");
    }

    @Test
    public void testNoFiles() throws Exception {
        checkWordCount("sample6");
    }

    private void checkWordCount(String testFilesPath) throws IOException, URISyntaxException {
        checkWordCount(testFilesPath, 1);
    }

    private void checkWordCount(String testFilesPath, int copies) throws IOException, URISyntaxException {

        String[] inputFiles = getInputFilesAbsolutePaths(testFilesPath, copies);

        String expectedOutput = getExpectedOutput(testFilesPath);

        wordsCounter.load(inputFiles);
        wordsCounter.displayStatus();

        Assert.assertEquals(expectedOutput, outContent.toString());

    }

    private String[] repeatFiles(String[] inputFiles, int count) {
        String[] inputFilesRepeated = new String[count * inputFiles.length];
        for (int i = 0; i < inputFiles.length; i++) {
            Arrays.fill(inputFilesRepeated, i * count, (i + 1) * count, inputFiles[i]);
        }
        return inputFilesRepeated;
    }

    private String[] getInputFilesAbsolutePaths(String testFilesPath, int copies) throws URISyntaxException, IOException {
        String inputPath = testFilesPath + "/input";
        URI resource = getClass().getResource("/" + inputPath).toURI();
        String[] inputFilesAbsolutePaths = Files.list(Paths.get(resource)).map(path -> path.toAbsolutePath().toString()).toArray(String[]::new);
        return repeatFiles(inputFilesAbsolutePaths, copies);
    }

    private String getExpectedOutput(String testFilesPath) throws IOException, URISyntaxException {
        String outputPath = testFilesPath + "/output";
        URI resource = getClass().getResource("/" + outputPath + "/expected_output.txt").toURI();
        return new String(Files.readAllBytes(Paths.get(resource)));
    }

}