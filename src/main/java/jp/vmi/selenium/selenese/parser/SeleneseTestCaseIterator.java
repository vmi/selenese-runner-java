package jp.vmi.selenium.selenese.parser;

import java.util.NoSuchElementException;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.utils.PathUtils;

/**
 * Iterator and iterable of test suite of Selenese format.
 */
public class SeleneseTestCaseIterator extends AbstractTestElementIterator<TestCaseEntry> implements TestCaseIterator {

    private final NodeList nodeList;
    private int index = 0;

    /**
     * Constructor.
     *
     * @param filename selenese script file. (Don't use to open a file. It is used as a label and is used to generate filenames based on it)
     * @param document Test suite DOM.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public SeleneseTestCaseIterator(String filename, Document document) throws InvalidSeleneseException {
        super(filename);
        setName(ParserUtils.getNameFromFilename(filename));
        setId(filename);
        try {
            this.nodeList = XPathAPI.selectNodeList(document, "//TBODY/TR/TD/A/@href");
        } catch (TransformerException e) {
            throw new InvalidSeleneseException(e, filename, null);
        }
    }

    @Override
    public boolean hasNext() {
        return index < nodeList.getLength();
    }

    @Override
    public TestCaseEntry next() {
        if (!hasNext())
            throw new NoSuchElementException();
        String tcFilename = nodeList.item(index++).getNodeValue();
        return new TestCaseEntry(false, tcFilename, tcFilename);
    }

    private String getParentDir() {
        String filename = PathUtils.normalize(getFilename());
        if (filename == null)
            return null;
        return FilenameUtils.getFullPathNoEndSeparator(filename);
    }

    @Override
    public TestElementIteratorFactory<CommandIterator, TestCaseEntry> getCommandIteratorFactory() throws InvalidSeleneseException {
        String parentDir = getParentDir();
        return caseEntry -> {
            String filename = caseEntry.id;
            if (FilenameUtils.getPrefixLength(filename) == 0 && parentDir != null)
                filename = PathUtils.concat(parentDir, filename);
            else
                filename = PathUtils.normalize(filename);
            TestElementIterator<?> iter = SeleneseIteratorFactory.newInstance(filename).newIterator();
            if (!(iter instanceof CommandIterator))
                throw new InvalidSeleneseException(filename + " is not test-case file.", filename, caseEntry.name);
            return (CommandIterator) iter;
        };
    }
}
