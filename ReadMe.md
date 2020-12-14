# File-Ripper

A small lightweight library to parse your flat files and deliver the data inside.

## Install

File-Ripper is available on Maven Central.

## FileDefinition and FieldDefinition

File-Ripper provides multiple ways for you to parse your files.  Using File-Ripper's FileDefinition and FieldDefinition contracts, you decide how to persist your file configurations:

```java
public class FileDefinitionBuilder {
    public FileDefinition buildDelimitedFileDefinition(String[] args) {
        List<FieldDefinition> fieldDefs = new ArrayList<>();
        fieldDefs.add(buildFieldDefinition("name", null, null));
        fieldDefs.add(buildFieldDefinition("age", null, null));
        fieldDefs.add(buildFieldDefinition("dob", null, null));

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setFileType(FileType.DELIMITED);
        fileDefinition.setDelimiter("|");
        fileDefinition.setHasHeader(true);
        fileDefinition.setFieldDefinitions(fieldDefs);        

        return fileDefinition;
    }
    
    public FileDefinition buildFixedFileDefinition() {
        List<FieldDefinition> fieldDefs = new ArrayList<>();
        fieldDefs.add(buildFieldDefinition("name", 0, 20));
        fieldDefs.add(buildFieldDefinition("age", 20, 5));
        fieldDefs.add(buildFieldDefinition("dob", 25, 10));

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setFileType(FileType.FIXED);
        fileDefinition.setHasHeader(true);
        fileDefinition.setFieldDefinitions(fieldDefs);        

        return fileDefinition;
    }

    public FileDefinition buildXmlFileDefinition() {
        List<FieldDefinition> fieldDefs = new ArrayList<>();
        fieldDefs.add(buildFieldDefinition("name", null, null));
        fieldDefs.add(buildFieldDefinition("age", null, null));
        fieldDefs.add(buildFieldDefinition("dob", null, null));

        FileDefinition fileDefintion = new FileDefinition();
        fileDefinition.setFileType(FileType.FIXED);
        fileDefinition.setFieldDefinitions(fieldDefs);
        fileDefintion.setRecordXmlElement("person");
        //inputDirectory and fileMask only required if using File-Ripper's findAndRipFiles method     
        fileDefinition.setInputDirectory(args[0]);
        fileDefinition.setFileMask("Valid-*.xml");  

        return fileDefinition;
    }

    private FieldDefinition buildFieldDefinition(String fieldName, Integer fieldLength, Integer startPosition) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setFieldName(fieldName);
        fieldDefinition.setFieldLength(fieldLength);  //only required for fields in FileType.FIXED
        fieldDefinition.setStartPosition(startPosition);  //only required for fields in FileType.FIXED
        return fieldDefinition;
    }
}
```


## Ripping Files

Using create an instance of the com.ohgnarly.fileripper.FileRipper class to get started ripping your files.  Your file can be parsed into a com.ohgnarly.fileripper.FileOutput object which contains the data in a Map<String, String>.  This is perfect for turning your data into a json object for export to an API.

Alternatively, you can receive your file as a com.ohgnarly.fileripper.FileResult and have the data parsed into a list of generic objects by passing in a builder function.  

```java
public class FileProcessor {
    public void processFile(File file) {
        FileDefinition fileDefinition = fileDefinitionBuilder.buildDelimitedFileDefinition();
        FileRipper fileRipper = new FileRipper();
        
        FileOutput fileOutput = fileRipper.ripFile(file, fileDefinition);
        com.ohgnarly.fileripper.FileResult<Person> fileResult = fileRipper.ripFile(file, fileDefinition, this::buildPerson);
    }
    
    private Person buildPerson(Map<String, String> fields) {
        Person person = new Person();
        person.setName(fields["name"]);
        person.setAge(fields["age"]);
        person.setDob(fields["dob"]);
        return person;
    }
}
```

The com.ohgnarly.fileripper.FileRipper class also supports ripping multiple files using both output types.

```java
public class FileProcessor {
    public void processFiles(List<File> files) {
        FileDefinition fileDefinition = fileDefinitionBuilder.buildDelimitedFileDefinition();
        FileRipper fileRipper = new FileRipper();
        
        List<FileOutput> fileOutputList = fileRipper.ripFile(files, fileDefinition);
        List<com.ohgnarly.fileripper.FileResult<Person>> fileResultList = fileRipper.ripFile(files, fileDefinition, this::buildPerson);
    }
    
    private Person buildPerson(Map<String, String> fields) {
        Person person = new Person();
        person.setName(fields["name"]);
        person.setAge(fields["age"]);
        person.setDob(fields["dob"]);
        return person;
    }
}
```


## Finding and Ripping Files

com.ohgnarly.fileripper.FileRipper also supports finding your files on the file system and ripping them for you.  This saves you the time of looking up your files first.

```java
public class FileProcessor {
    public void processFiles(String filePath) {
        FileDefinition fileDefinition = fileDefinitionBuilder.buildDelimitedFileDefinition();
        //Input directory and file mask are required for using findAndRipFiles
        fileDefinition.setInputDirectory(filePath);
        fileDefinition.setFileMask("Valid-*.txt");
        
        //As with other file ripping methods, both return types are supported
        List<FileOutput> fileOutputList = new FileRipper().findAndRipFiles(fileDefinition);
        List<com.ohgnarly.fileripper.FileResult<Person>> fileResultList = new FileRipper().findAndRipFiles(fileDefinition, PersonBuilder::buildPerson);
    }
}
```


## Optional Functionality

### Create your own com.ohgnarly.fileripper.FileRepository

By default, com.ohgnarly.fileripper.FileRipper uses glob pattern wildcards for looking up your files.  By creating your own instance of the com.ohgnarly.fileripper.FileRepository interface, you can define your own file lookup.  Here is an example of a regex repository instead.


```java
import com.ohgnarly.fileripper.FileRepository;public class RegexFileRepository implements FileRepository {
    @NotNull
    @Override
    public List<File> getFiles(@NotNull String inputDirectory, @NotNull String fileMask) {
        FileFilter fileFilter = new RegexFileFilter(fileMask);
        File[] files = new File(inputDirectory).listFiles(fileFilter);
        return files ==  null ? emptyList() : asList(files);
    }
}

public class FileProcessor {
    public void processFiles(String filePath) {
        FileDefinition fileDefinition = fileDefinitionBuilder.buildDelimitedFileDefinition();
        fileDefinition.setInputDirectory(filePath);
        fileDefinition.setFileMask("Valid-*.txt");

        RegexFileRepository regexFileRepository = new RegexFileRepository();
        FileRipper fileRipper = new FileRipper(regexFileRepository);
        
        List<FileOutput> fileOutputList = fileRipper.findAndRipFiles(fileDefinition);
    }
}
```


### Moving Files to a completed directory

The FileDefinition contains a field for completedDirectory.  If you set this field, the com.ohgnarly.fileripper.FileRipper will attempt to move any files processed under that definition to the path specified.

```java
public class FileDefinitionBuilder {
    public FileDefinition buildFileDefinition(String completedPath) {
        FileDefinition fileDefinition = new FileDefinition();
        fileDefintion.setCompletedDirectory(completedPath);
        return fileDefintion;
    }   
}
```

Additionally, if needed, you can provide your own implementation of the com.ohgnarly.fileripper.FileMover interface to the com.ohgnarly.fileripper.FileRipper to define specifically how the files should be moved.

```java
import com.ohgnarly.fileripper.FileMover;import com.ohgnarly.fileripper.FileRipper;public class MyFileMover implements FileMover {
    public void moveFiles(List<File> files, String completedDirectory) {
        //code to define custom file move operation
    }
}

public class FileProcessor {
    public void processFiles(List<File> files, FileDefinition fileDefinition) {
        FileRipper fileRipper = new FileRipper(new MyFileMover());
        fileRipper.ripFiles(files, fileDefinition);
    }
}
```