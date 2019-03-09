package jp.vmi.selenium.runner.model;

/**
 * Command signature interface.
 */
public interface ICommandSignature {

    /**
     * Get id.
     *
     * @return id
     */
    String getId();

    /**
     * Get human readable name.
     *
     * @return name
     */
    String getName();

    /**
     * Get description.
     *
     * @return descripton
     */
    String getDescription();

    /**
     * Get type of target.
     *
     * @return type of target
     */
    ArgTypes getTargetType();

    /**
     * Get type of value.
     *
     * @return type of value.
     */
    ArgTypes getValueType();

    /**
     * Test that target argument is script.
     *
     * @return true if target argument is script
     */
    default boolean isTargetScript() {
        switch (getTargetType()) {
        case SCRIPT:
        case CONDITIONAL_EXPRESSION:
            return true;
        default:
            return false;
        }
    }

    /**
     * Test that value argument is script.
     *
     * @return true if value argument is script
     */
    default boolean isValueScript() {
        return getValueType() == ArgTypes.SCRIPT;
    }
}
