package jp.vmi.selenium.rollup;

import java.util.Map;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.CommandList;

/**
 * Rollup rule interface.
 */
public interface IRollupRule {

    /**
     * Get rollup rule name.
     *
     * @return rollup rule name.
     */
    String getName();

    /**
     * Get exapanded command list.
     *
     * @param context Runner object as Selenese Runner context.
     * @param rollupArgs arguments for rollup.
     * @return command list.
     */
    CommandList getExpandedCommands(Context context, Map<String, String> rollupArgs);
}
