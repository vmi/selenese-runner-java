package jp.vmi.selenium.runner.model.side;

import jp.vmi.selenium.runner.model.ArgTypes;
import jp.vmi.selenium.runner.model.ICommandSignature;

/**
 * Side command information.
 */
public class SideCommandSignature implements ICommandSignature {

    private final String id;
    private final String name;
    private final String description;
    private final ArgTypes targetType;
    private final ArgTypes valueType;

    /**
     * Constructor.
     *
     * @param id id
     * @param name human readable name
     * @param description description
     * @param targetType type of target argument
     * @param valueType type of value argument
     */
    public SideCommandSignature(String id, String name, String description, ArgTypes targetType, ArgTypes valueType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.targetType = targetType;
        this.valueType = valueType;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ArgTypes getTargetType() {
        return targetType;
    }

    @Override
    public ArgTypes getValueType() {
        return valueType;
    }
}
