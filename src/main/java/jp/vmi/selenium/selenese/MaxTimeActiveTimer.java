package jp.vmi.selenium.selenese;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interrupt main thread after the seconds specified by <code>--max-time</code> option.
 */
public class MaxTimeActiveTimer extends TimerTask implements MaxTimeTimer, Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MaxTimeActiveTimer.class);

    private final long startTime;
    private final long maxTime;
    private final Thread target;
    private final Timer timer;
    // original UncaughtExceptionHandler of the "target" thread.
    private final Thread.UncaughtExceptionHandler originalUncaughtExceptionHandler;

    MaxTimeActiveTimer(long maxTime) {
        this.timer = new Timer(getClass().getSimpleName());
        this.target = Thread.currentThread();
        this.startTime = System.currentTimeMillis();
        this.maxTime = maxTime;
        this.originalUncaughtExceptionHandler = target.getUncaughtExceptionHandler();
    }

    /**
     * Test specified thread is interrupted.
     *
     * @param thread thread object.
     * @return true if the thread is interrupted.
     */
    public static boolean isInterruptedByMaxTimeTimer(Thread thread) {
        return thread.getUncaughtExceptionHandler() instanceof MaxTimeActiveTimer
            && ((MaxTimeActiveTimer) thread.getUncaughtExceptionHandler()).isTarget(thread);
    }

    private boolean isTarget(Thread thread) {
        return target.equals(thread);
    }

    /**
     * Schedule timer task.
     */
    @Override
    public void start() {
        long elapsed = System.currentTimeMillis() - startTime;
        long delay = maxTime - elapsed;
        if (delay < 0) {
            log.warn("Maximum execution time has already been exceeded.");
            delay = 0;
        }
        timer.schedule(this, delay);
    }

    /**
     * stop timer and remove scheduled task.
     */
    @Override
    public void stop() {
        timer.cancel();
        timer.purge();
        //restore original UncaughtExceptionHandler
        target.setUncaughtExceptionHandler(originalUncaughtExceptionHandler);
    }

    @Override
    public void run() {
        log.error(String.format("Maximum execution time of %d seconds exceeded.", maxTime / 1000));
        // Use UncaughtExceptionHadler to distinguish interruption made by MaxTimeActiveTimer or not
        // and to avoid failing to trap interruption.
        target.setUncaughtExceptionHandler(this);
        target.interrupt();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // delegate to default handler. java.lang.ThreadGroup is default.
        target.getThreadGroup().uncaughtException(t, e);
    }
}
