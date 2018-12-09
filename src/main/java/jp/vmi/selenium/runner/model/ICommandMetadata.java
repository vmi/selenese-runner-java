package jp.vmi.selenium.runner.model;

/**
 * Command metadata.
 */
public interface ICommandMetadata {

    /**
     * Get command signature.
     *
     * @param id id
     * @return command signature.
     */
    ICommandSignature getSignature(String id);
}
