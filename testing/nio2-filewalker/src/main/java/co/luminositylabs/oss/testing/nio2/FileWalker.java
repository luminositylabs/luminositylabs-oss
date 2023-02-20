package co.luminositylabs.oss.testing.nio2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


/**
 * A file visitor demonstrating minimal functionality of Java7+ NIO2 {@code Simple File Visitor}.
 *
 * @author Phillip Ross
 */
public class FileWalker extends SimpleFileVisitor<Path> {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(FileWalker.class);

    /** The number of errors to occur before printing any logging statements for errors. */
    private static final int ERROR_COUNT_REPORTING_CHECKPOINT = 100000;

    /** The number of files visited. */
    private long fileCount;

    /** The maximum number of files to be visited. */
    private long fileCountCutoff;

    /** The number of directories visited. */
    private long directoryCount;

    /** The number of errors that have occurred. */
    private long errorCount;

    /** True if an error should short-circuit file walking. */
    private boolean failFast;

    /** A list of path names to skip while file walking. */
    private List<String> skipDirectories;


    /**
     * Initializes the file walker defaults.
     *
     * @param skipDirectories the directory skip list
     */
    public FileWalker(final List<String> skipDirectories) {
        setFileCount(0);
        setFileCountCutoff(-1);
        setDirectoryCount(0);
        setErrorCount(0);
        setFailFast(false);
        setSkipDirectories(skipDirectories);
    }


    /**
     * Get the file count.
     *
     * @return the file count.
     */
    public long getFileCount() {
        return fileCount;
    }


    /**
     * Set the file file count.
     *
     * @param fileCount the file count.
     */
    public void setFileCount(final long fileCount) {
        this.fileCount = fileCount;
    }


    /**
     * Get the file count cutoff.
     *
     * @return the file count cutoff.
     */
    public long getFileCountCutoff() {
        return fileCountCutoff;
    }


    /**
     * Set the file count cutoff.
     *
     * @param fileCountCutoff the file count cutoff.
     */
    public void setFileCountCutoff(final long fileCountCutoff) {
        this.fileCountCutoff = fileCountCutoff;
    }


    /**
     * Get the directory count.
     *
     * @return the directory count
     */
    public long getDirectoryCount() {
        return directoryCount;
    }


    /**
     * Set the directory count.
     *
     * @param directoryCount the directory count
     */
    public void setDirectoryCount(final long directoryCount) {
        this.directoryCount = directoryCount;
    }


    /**
     * Get the error count.
     *
     * @return the error count
     */
    public long getErrorCount() {
        return errorCount;
    }


    /**
     * Set the error count.
     *
     * @param errorCount the error count
     */
    public void setErrorCount(final long errorCount) {
        this.errorCount = errorCount;
    }


    /**
     * Get the fail-fast flag.
     *
     * @return the fail-fast flag
     */
    public boolean isFailFast() {
        return failFast;
    }


    /**
     * Set the fail-fast flag.
     *
     * @param failFast the fail-fast flag
     */
    public void setFailFast(final boolean failFast) {
        this.failFast = failFast;
    }


    /**
     * Get the directory skip list.
     *
     * @return the directory skip list
     */
    public List<String> getSkipDirectories() {
        return skipDirectories;
    }


    /**
     * Set the directory skip list.
     *
     * @param skipDirectories the directory skip list
     */
    public void setSkipDirectories(final List<String> skipDirectories) {
        this.skipDirectories = skipDirectories;
    }



    /**
     * Invoked for a directory before entries in the directory are visited.
     *
     * <p> Unless overridden, this method returns {@link java.nio.file.FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult preVisitDirectory(final Path path,
                                             final BasicFileAttributes basicFileAttributes) throws IOException {
        FileVisitResult fileVisitResult = FileVisitResult.CONTINUE;
        if (isExcluded(path)) {
            fileVisitResult = FileVisitResult.SKIP_SUBTREE;
        } else {
            directoryCount++;
        }
        return fileVisitResult;
    }


    /**
     * Invoked for a file in a directory.
     *
     * <p> Unless overridden, this method returns {@link java.nio.file.FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult visitFile(final Path path,
                                     final BasicFileAttributes basicFileAttributes) throws IOException {
        FileVisitResult fileVisitResult = FileVisitResult.CONTINUE;
        if (!isExcluded(path)) {
            fileCount++;
            if ((fileCountCutoff != -1) && (fileCount > fileCountCutoff)) {
                fileVisitResult = FileVisitResult.TERMINATE;
            }
        }
        return fileVisitResult;
    }


    /**
     * Invoked for a file that could not be visited.
     *
     * <p> Unless overridden, this method re-throws the I/O exception that prevented
     * the file from being visited.
     */
    @Override
    public FileVisitResult visitFileFailed(final Path path,
                                           final IOException exc) throws IOException {
        errorCount++;
        if ((errorCount % ERROR_COUNT_REPORTING_CHECKPOINT) == 0) {
            logger.debug("currentErrorCount: {}", errorCount);
        }
        logger.error("Failed to visit path: {} ({}) ({})", path, exc.getClass().getName(), exc.getMessage());

        if (failFast) {
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }


    /**
     * Check if the path is to be excluded.
     *
     * @param path the path to be checked.
     * @return true if the path is to be excluded.
     */
    public boolean isExcluded(final Path path) {
        boolean skip = false;
        for (String skipDirectory : skipDirectories) {
            if (path.startsWith(skipDirectory)) {
                logger.info("Skipping " + path);
                skip = true;
                break;
            }
        }
        return skip;
    }


}
