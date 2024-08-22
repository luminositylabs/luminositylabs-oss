package co.luminositylabs.oss.testing.nio2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Tests various file walking functions.
 *
 * @author Phillip Ross
 */
public class FileWalkTest {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(FileWalkTest.class);

    /**
     * The default directory scan exclusion list filename to search for within the class path.
     */
    private static final String DEFAULT_TEST_DIRECTORY_SCAN_EXCLUSIONS = "directoryScanExclusions.lst";

    /**
     * The default directory scan inclusion list filename to search for within the class path.
     */
    private static final String DEFAULT_TEST_DIRECTORY_SCAN_INCLUSIONS = "directoryScanInclusions.lst";


    /**
     * Walks the filesystem according to exclusion/inclusion lists and prints basic metrics to the log.
     *
     * @throws IOException when an IOException occurs
     * @throws URISyntaxException when a URISyntaxException occurs
     */
    @Test
    public void testFileWalk() throws IOException, URISyntaxException {
        logger.trace("void testFileWalk()");

        URL directoryScanExclusionsListURL = Thread.currentThread()
                .getContextClassLoader()
                .getResource(DEFAULT_TEST_DIRECTORY_SCAN_EXCLUSIONS);
        Assert.assertNotNull(directoryScanExclusionsListURL);
        Path exclusionsListPath = Paths.get(
                directoryScanExclusionsListURL.toURI()
        );
        List<String> exclusionsList = Files.readAllLines(exclusionsListPath)
                .stream()
                .map(s -> {
                    String replacementString = s;
                    for (String propertyName : System.getProperties().stringPropertyNames()) {
                        String propertyValue = System.getProperty(propertyName);
                        String replacementExpression = "${" + propertyName + "}";
                        if (replacementString.contains(replacementExpression)) {
                            replacementString = replacementString.replace(replacementExpression, propertyValue);
                        }
                    }
                    return replacementString;
                })
                .collect(Collectors.toList());

        URL directoryScanInclusionsListURL = Thread.currentThread()
                .getContextClassLoader()
                .getResource(DEFAULT_TEST_DIRECTORY_SCAN_INCLUSIONS);
        Assert.assertNotNull(directoryScanInclusionsListURL);
        Path inclusionsListPath = Paths.get(
                directoryScanInclusionsListURL.toURI()
        );
        List<String> inclusionsList = Files.readAllLines(inclusionsListPath)
                .stream()
                .map(s -> {
                    String replacementString = s;
                    for (String propertyName : System.getProperties().stringPropertyNames()) {
                        String propertyValue = System.getProperty(propertyName);
                        String replacementExpression = "${" + propertyName + "}";
                        if (replacementString.contains(replacementExpression)) {
                            replacementString = replacementString.replace(replacementExpression, propertyValue);
                        }
                    }
                    return replacementString;
                })
                .collect(Collectors.toList());

        EnumSet<FileVisitOption> options = EnumSet.noneOf(FileVisitOption.class);
        int maxDepth = Integer.MAX_VALUE;

        int totalTraversalTimeMillis = 0;
        int totalFileCount = 0;
        int totalDirectoryCount = 0;
        int totalErrors = 0;
        for (String inclusion : inclusionsList) {
            Path inclusionPath = Paths.get(inclusion);
            if (Files.exists(inclusionPath)) {
                logger.debug("Scanning {}", inclusionPath.toAbsolutePath());
                FileWalker fileIndexer = new FileWalker(exclusionsList);
                long traverseStartTime = System.currentTimeMillis();
                Files.walkFileTree(inclusionPath, options, maxDepth, fileIndexer);
                long traverseFinishTime = System.currentTimeMillis();

                logger.debug("Traverse time in millis: {}", (traverseFinishTime - traverseStartTime));
                logger.debug("Number of files/directories/errors ({} / {} / {})",
                        fileIndexer.getFileCount(),
                        fileIndexer.getDirectoryCount(),
                        fileIndexer.getErrorCount()
                );

                totalTraversalTimeMillis += (traverseFinishTime - traverseStartTime);
                totalFileCount += fileIndexer.getFileCount();
                totalDirectoryCount += fileIndexer.getDirectoryCount();
                totalErrors += fileIndexer.getErrorCount();
            } else {
                logger.error("Skipping {} (non-existent?)", inclusion);
            }
        }

        logger.debug("Total traverse time in millis: {}", totalTraversalTimeMillis);
        logger.debug(
                "Total number of files/directories/errors ({} / {} / {})",
                totalFileCount,
                totalDirectoryCount,
                totalErrors
        );
    }


}
