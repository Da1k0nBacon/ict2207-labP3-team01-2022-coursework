package com.danstoakes.fileexplorer.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FileXMLHelper
{
    private static final String FILENAME = "files.xml";
    private static final String ROOT_TAG = "files";
    private static final String LEVEL_TAG = "level";
    private static final String DIR_TAG = "directory";
    private static final String FILE_TAG = "file";

    private static final String ATTRIBUTE_INDEX = "index";
    private static final String ATTRIBUTE_PATH = "path";

    private static String directory;

    private static Document document;
    private static Element root;

    public static void createDocument (String saveDirectory)
    {
        directory = saveDirectory;

        try
        {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            File file = new File(directory, FILENAME);
            if (file.exists())
                document = documentBuilder.parse(file);
            else
                document = documentBuilder.newDocument();

            writeToFile();
        } catch (ParserConfigurationException | IOException | SAXException e)
        {
            e.printStackTrace();
        }
    }

    public static void createRoot ()
    {
        root = document.getDocumentElement();
        if (root == null)
        {
            root = document.createElement(ROOT_TAG);
            document.appendChild(root);
        }
        writeToFile();
    }

    private static Element findLevelByIndex (int index)
    {
        NodeList levels = document.getElementsByTagName(LEVEL_TAG);

        for (int i = 0; i < levels.getLength(); i++)
        {
            Node node = levels.item(i);
            Element element = (Element) node;

            int indexInt = Integer.parseInt(element.getAttribute(ATTRIBUTE_INDEX));
            if (node.getNodeType() == Node.ELEMENT_NODE && indexInt == index)
                return element;
        }
        return null;
    }

    private static void deleteChildNodes (Element level)
    {
        NodeList directories = level.getChildNodes();
        int size = directories.getLength();

        for (int i = size - 1; i >= 0; i--)
            level.removeChild(directories.item(i));

        writeToFile();
    }

    public static void createLevel (int index, String directoryPath, List<File> files)
    {
        Element level = findLevelByIndex(index);
        if (level == null)
        {
            level = document.createElement(LEVEL_TAG);
            root.appendChild(level);
            level.setAttribute(ATTRIBUTE_INDEX, String.valueOf(index));
        } else
        {
            deleteChildNodes (level);
        }

        createDirectory(level, directoryPath, files);
        writeToFile();
    }

    private static void createDirectory (Element root, String path, List<File> files)
    {
        Element directory = document.createElement(DIR_TAG);
        root.appendChild(directory);
        directory.setAttribute(ATTRIBUTE_PATH, path);

        for (int i = 0; i < files.size(); i++)
        {
            Element file = document.createElement(FILE_TAG);
            directory.appendChild(file);
            file.setAttribute(ATTRIBUTE_INDEX, String.valueOf(i));

            Element filePath = document.createElement(ATTRIBUTE_PATH);
            file.appendChild(filePath);
            filePath.appendChild(document.createTextNode(files.get(i).getAbsolutePath()));
        }
    }

    private static void writeToFile ()
    {
        try
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(directory, FILENAME));

            transformer.transform(domSource, streamResult);
        } catch (TransformerException e)
        {
            e.printStackTrace();
        }
    }

    public static int getLevelCount ()
    {
        NodeList levels = document.getElementsByTagName(LEVEL_TAG);
        return levels.getLength();
    }

    public static List<File> getLevels ()
    {
        List<File> files = new ArrayList<>();

        for (int i = 0; i < getLevelCount(); i++)
            files.add(new File(getPathAtLevel(i)));

        return files;
    }

    public static void removeLevel (int index)
    {
        Element level = findLevelByIndex(index);
        if (level != null)
            root.removeChild(level);

        writeToFile();
    }

    public static String getPathAtLevel (int index)
    {
        Element level = findLevelByIndex(index);
        Node directoryNode = level.getChildNodes().item(0);
        Element directoryElement = (Element) directoryNode;

        return directoryElement.getAttribute("path");
    }

    public static List<File> getLevelNodes (int index)
    {
        Element level = findLevelByIndex(index);
        Node directoryNode = level.getChildNodes().item(0);
        Element directoryElement = (Element) directoryNode;

        NodeList files = directoryElement.getChildNodes();

        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < files.getLength(); i++)
        {
            Node pathNode = files.item(i).getChildNodes().item(0);
            Element pathElement = (Element) pathNode;

            fileList.add(new File(pathElement.getTextContent()));
        }
        return fileList;
    }

    public static void clearLevels ()
    {
        for (int i = getLevelCount() - 1; i >= 0; i--)
            removeLevel(i);
    }
}