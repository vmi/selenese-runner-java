package jp.vmi.selenium.selenese;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Interface of tree structure object with generating a file.
 */
public interface ITreedFileGenerator {

    /**
     * Get parent object.
     *
     * @return parent object.
     */
    ITreedFileGenerator getParent();

    /**
     * Set parent object.
     *
     * @param parent parent object.
     */
    default void setParent(ITreedFileGenerator parent) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get index of this object in the parent object.
     *
     * @return index.
     */
    int getIndex();

    /**
     * Get indexes of this object from top level object.
     *
     * @return array of the index.
     */
    default int[] getIndexes() {
        ITreedFileGenerator parent = getParent();
        if (parent == null)
            return ArrayUtils.EMPTY_INT_ARRAY;
        else
            return ArrayUtils.add(parent.getIndexes(), getIndex());
    }

    /**
     * Set index of this object in the parent object.
     *
     * @param index index.
     */
    void setIndex(int index);

    /**
     * Get base name.
     *
     * @return base name.
     */
    String getBaseName();

    /**
     * Get name.
     *
     * @return name.
     */
    String getName();

    /**
     * Get file base name.
     *
     * @return file base name.
     */
    default String getFileBaseName() {
        return getFileBaseName(null);
    }

    /**
     * Get file base name with extension.
     *
     * @param ext file extension.
     * @return file base name with extension.
     */
    default String getFileBaseName(String ext) {
        ITreedFileGenerator parent = getParent();
        String baseName = getBaseName();
        StringBuilder buf = new StringBuilder("TEST-").append(baseName);
        if (parent != null && baseName.equals(parent.getBaseName())) {
            for (int index : getIndexes())
                buf.append(String.format("_%04d", index));
            String name = getName();
            if (!baseName.equals(name))
                buf.append('_').append(name);
        }
        if (ext != null)
            buf.append('.').append(ext);
        return buf.toString();
    }
}
