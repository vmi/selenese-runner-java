package jp.vmi.selenium.selenese.command;

/**
 * Implements {@link #getReachedCounts()} for start loop command.
 */
public abstract class StartLoopImpl extends AbstractCommand implements StartLoop {

    private int reachedCount = 1;

    StartLoopImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void resetReachedCount() {
        reachedCount = 1;
    }

    @Override
    public void incrementReachedCount() {
        reachedCount++;
    }

    @Override
    public String getReachedCounts() {
        String parentReachedCounts = getStartLoop().getReachedCounts();
        if (parentReachedCounts.isEmpty())
            return Integer.toString(reachedCount);
        else
            return parentReachedCounts + REACHED_COUNT_SEPARATOR + reachedCount;
    }

    @Override
    public String toString() {
        return toString(getIndex(), getReachedCounts(), getName(), getArguments());
    }
}
