import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Homework5 {
}

interface Logger
{
    public void __sysLogData(LogDataType log, String message, String date);

    public void logInfo();

    public void logError();

    public void logComplete();

    public void logWarning();

    public void __sysErrorLog(Exception exception);
}
abstract class FileProcessor
{
    public abstract void readData(String filename);
    public abstract void processData();
    public abstract void writeData(String filename);
}
enum LogDataType
{
    Error,
    Default,
    Warning,
    CompletedTask
}

class TextFileProcessor extends FileProcessor implements Logger
{
    TextFileProcessor(String path_to_logger)
    {
        logger_path = path_to_logger;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();


            Document document = builder.newDocument();


            Element root = document.createElement("log");
            document.appendChild(root);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(path_to_logger);
            transformer.transform(source, result);
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }


    }
    TextFileProcessor(){
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();


            Document document = builder.newDocument();


            Element root = document.createElement("log");
            document.appendChild(root);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(logger_path);
            transformer.transform(source, result);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    String fileData = "";

    String __errDataLog = "";

    String dataOfEvent = "";

    String __normallyDataLog = "";

    String __warningDataLog = "";

    String __completedTaskDataLog = "";

    String logger_path = "logger.log";

    @Override
    public void readData(String filename) {
        __normallyDataLog = "Reading data...";
        logInfo();
        try(FileReader fr = new FileReader(filename))
        {
            boolean isFirst = true;
            int __char;
            while((__char = fr.read()) != 1)
            {
                isFirst = false;
                fileData += __char;
            }
            if(isFirst)
            {
                __warningDataLog = "File is null!";
                logWarning();
            }
            __completedTaskDataLog = "File has read!";
            logComplete();
        }
        catch (Exception ex)
        {
            Date date = new Date();
            dataOfEvent = date.toString();
            __errDataLog = ex.getMessage();
            logError();
        }
    }

    @Override
    public void processData() {
        __normallyDataLog = "Processing data...";
        logInfo();
        try
        {
            String[] Strings = fileData.split("\n");
            Arrays.sort(Strings);
            fileData = "";
            for (String str : Strings) fileData += str;
            __completedTaskDataLog = "Process data has done!";
            logComplete();
        }
        catch (Exception ex)
        {
            Date date = new Date();
            dataOfEvent = date.toString();
            __errDataLog = ex.getMessage();
            logError();
        }

    }

    @Override
    public void writeData(String filename) {
        __normallyDataLog = "Writing data...";
        logInfo();
        try(FileWriter fw = new FileWriter(filename))
        {

            fw.write(fileData);
            if(fileData.isEmpty())
            {
                __warningDataLog = "Writing null data to file!";
                logWarning();
            }
            __completedTaskDataLog = "File has wrote!";
            logComplete();
        }
        catch (Exception ex)
        {
            Date date = new Date();
            dataOfEvent = date.toString();
            __errDataLog = ex.getMessage();
            logError();
        }
    }

    @Override
    public void __sysLogData(LogDataType log, String message, String date) {
        try
        {
            File xmlFile = new File(logger_path);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(xmlFile);

            Element Date = document.createElement("date");
            Date.appendChild(document.createTextNode(date));

            switch (log) {
                case LogDataType.Default -> {
                    Element info = document.createElement("info");
                    info.appendChild(document.createTextNode(message));
                    NodeList nodeList = document.getElementsByTagName("log");

                    Node node = nodeList.item(0);
                    node.appendChild(info);
                }
                case LogDataType.Warning -> {
                    Element warning = document.createElement("warning");
                    warning.appendChild(document.createTextNode(message));
                    NodeList nodeList = document.getElementsByTagName("log");

                    Node node = nodeList.item(0);
                    node.appendChild(warning);
                }
                case LogDataType.Error -> {
                    Element error = document.createElement("error");
                    error.appendChild(document.createTextNode(message));
                    NodeList nodeList = document.getElementsByTagName("log");

                    Node node = nodeList.item(0);
                    node.appendChild(error);
                }
                case LogDataType.CompletedTask -> {
                    Element completedTask = document.createElement("Task.Completed");

                    NodeList nodeList = document.getElementsByTagName("log");

                    Node node = nodeList.item(0);
                    node.appendChild(completedTask);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(logger_path);
            transformer.transform(source, result);
        }
        catch (Exception ex)
        {
            __sysErrorLog(ex);
        }

    }



    @Override
    public void logInfo() {
        __sysLogData(LogDataType.CompletedTask, __normallyDataLog, dataOfEvent);
    }

    @Override
    public void logError() {
        __sysLogData(LogDataType.Error, __errDataLog, dataOfEvent);
    }

    @Override
    public void logComplete() {
        __sysLogData(LogDataType.Default, __normallyDataLog, dataOfEvent);
    }

    @Override
    public void logWarning() {
        __sysLogData(LogDataType.Warning, __warningDataLog, dataOfEvent);
    }

    @Override
    public void __sysErrorLog(Exception exception) {
        try(FileWriter fw = new FileWriter("logging_errors.syslog"))
        {
            fw.write(exception.getMessage());
        }catch(Exception _){}
    }
}
