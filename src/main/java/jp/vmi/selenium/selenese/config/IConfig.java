package jp.vmi.selenium.selenese.config;

/**
 * Configuration information.
 */
public interface IConfig {

    /**
     * Get command line arguments without parsed options.
     *
     * @return command line arguments.
     */
    String[] getArgs();

    /**
     * Test whether the option exists or not.
     *
     * @param opt option name.
     * @return true if exists.
     */
    boolean hasOption(String opt);

    /**
     * Get option value.
     *
     * @param opt option name.
     * @return option value.
     */
    String getOptionValue(String opt);

    /**
     * Get option value.
     *
     * @param opt option name.
     * @param defaultValue default value.
     * @return option value, or defaultvalue if option does not exist.
     */
    String getOptionValue(String opt, String defaultValue);

    /**
     * Get option values.
     *
     * @param opt option name.
     * @return array of option values.
     */
    String[] getOptionValues(String opt);

    /**
     * Get option value as boolean.
     *
     * @param opt option name.
     * @return true if the option is exists or the entry in configuration file is "true"/"on"/"yes".
     */
    boolean getOptionValueAsBoolean(String opt);
}
