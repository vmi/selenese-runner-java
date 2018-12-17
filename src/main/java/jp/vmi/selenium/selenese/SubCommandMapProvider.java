package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.subcommand.SubCommandMap;

/**
 * Sub command map provider.
 */
public interface SubCommandMapProvider {

    /**
     * Get SubCommandMap instance.
     *
     * @return SubCommandMap instance.
     */
    SubCommandMap getSubCommandMap();
}
