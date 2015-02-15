package jp.vmi.selenium.selenese.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class CommandSequenceTest {

    private static class DummyCommand extends AbstractCommand {

        private static final ArgumentType[] EMPTY_ARGUMENT_TYPE = new ArgumentType[0];

        public DummyCommand(int index, String name, StartLoop startLoop) {
            super(index, name, ArrayUtils.EMPTY_STRING_ARRAY, EMPTY_ARGUMENT_TYPE);
            setStartLoop(startLoop);
        }

        @Override
        protected Result executeImpl(Context context, String... curArgs) {
            return SUCCESS;
        }
    }

    private static class DummyStartLoop extends DummyCommand implements StartLoop {

        public DummyStartLoop(int index, String name, StartLoop startLoop) {
            super(index, name, startLoop);
        }

        @Override
        public void setEndLoop(EndLoop endLoop) {
        }
    }

    private static final char C = 'c';
    private static final char W = 'w';
    private static final char E = 'e';

    private static List<ICommand> build(char... cmds) {
        List<ICommand> result = new ArrayList<ICommand>();
        Deque<StartLoop> startLoopStack = new ArrayDeque<StartLoop>();
        startLoopStack.addFirst(StartLoop.NO_START_LOOP);
        for (char cmd : cmds) {
            switch (cmd) {
            case C:
                result.add(new DummyCommand(result.size(), String.valueOf(cmd), startLoopStack.peekFirst()));
                break;
            case W:
                DummyStartLoop w = new DummyStartLoop(result.size(), String.valueOf(cmd), startLoopStack.peekFirst());
                startLoopStack.addFirst(w);
                result.add(w);
                break;
            case E:
                DummyCommand e = new DummyCommand(result.size(), String.valueOf(cmd), startLoopStack.peekFirst());
                startLoopStack.pollFirst();
                result.add(e);
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + cmd);
            }
        }
        return result;
    }

    private static String[] RESULTS = {
        "1",
        "2-1",
        "2-2",
        "2-3",
        "3",
        "4-1",
        "4-2-1",
        "4-2-2",
        "4-2-3",
        "4-3-1",
        "4-3-2",
        "4-3-3",
        "4-4",
        "5"
    };

    @Test
    public void test() {
        CommandSequence sut = new CommandSequence(null);
        List<ICommand> cl = build(C, W, C, E, C, W, W, C, E, W, C, E, E, C);
        int i = 0;
        for (ICommand c : cl) {
            String r = RESULTS[i++];
            sut.increment(c);
            assertThat(sut.toString(), is(r));
        }
        sut.increment(cl.get(2));
        assertThat(sut.toString(), is("6-1"));
        sut.increment(cl.get(7));
        assertThat(sut.toString(), is("7-1-1"));
        sut.increment(cl.get(2));
        assertThat(sut.toString(), is("8-1"));
    }
}
