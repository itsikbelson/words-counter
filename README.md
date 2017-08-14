# Words counter
This program calculates words count from multiple files in multiple trheads

## Build
In order to build the application, run:
`./gradlew clean build`

## Run
In order to run the application, run:<br>
`java -jar build/libs/words-counter.jar <input-file-name1> <input-file-name2> ...`, where:<br>
words will be counted on all files together

Example:
* `java -jar build/libs/words-counter.jar build/resources/test/sample1/input/File1.txt build/resources/test/sample1/input/File2.txt build/resources/test/sample1/input/File3.txt`

