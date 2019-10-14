package com.ohgnarly.fileripper.services;

import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.models.FieldDefinition;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class XmlFileService extends FileService {
    public XmlFileService(FileDefinition fileDefinition) {
        super(fileDefinition);
    }

    @Override
    public FileOutput processFile(File file) throws FileRipperException {
        try {
            FileOutput fileOutput = new FileOutput();
            fileOutput.setFileName(file.getName());
            List<Map<String,Object>> records = new ArrayList<>();

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(file);
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(fileDefinition.getRecordXmlElement());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element person = (Element)nodes.item(i);
                Map<String, Object> record = new LinkedHashMap<>();
                for (FieldDefinition fieldDefinition : fileDefinition.getFieldDefinitions()) {
                    NodeList fieldNodes = person.getElementsByTagName(fieldDefinition.getFieldName());
                    if (fieldNodes.getLength() == 0) {
                            throw new FileRipperException(format("Field %s is does not exist in file",
                                fieldDefinition.getFieldName()));
                    }

                    String fieldName = fieldNodes.item(0).getNodeName();
                    String fieldValue = fieldNodes.item(0).getTextContent();
                    record.put(fieldName, fieldValue);
                }
                records.add(record);
            }
            fileOutput.setRecords(records);
            return fileOutput;
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new FileRipperException(ex);
        }
    }
}
