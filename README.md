# Fabflix
#### By Ian Coooperman, #36747912

### Demos
Project 1 demo can be found [here](https://www.youtube.com/watch?v=gSUd3sx91NA&feature=youtu.be).

Project 2 demo can be found [here](https://www.youtube.com/watch?v=IIdI8JKQr2c&feature=youtu.be).

Project 3 demo can be found [here](https://youtu.be/EX0emZpuaIQ).

Project 4 demo can be found [here](https://www.youtube.com/watch?v=B6YfYBYagEw).

As of recording each demo, the latest commit is one commit behind updating README.md.

### Deployment Instructions
This Webapp and XML Parser portions of this project are using Maven. Deployment can be done the same way as instructed.

1. Clone the repo.
    ```bash
    git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-132.git
    ```

#### Webapp

2. Inside the repo, where `pom.xml` for the webapp is, build the .war file.
    ```bash
    mvn package
    ```
3. Copy the newly built .war file to the tomcat webapps folder.

#### XML Parser
1a. Ensure you have manually created or copied a `DBLoginInfo.java` to the `/parser` source directory, as this repository does not provide it.

2. Inside the repo, where pom.xml for the XML Parser is, build the parser.

    ```bash
    mvn clean package
    ```
3. Run the parser.
    ```bash
    mvn exec:java -Dexec.mainClass="parser.MainParser"
    ```

#### Android
2. Open the Android project (located under /Android) in Android Studio and compile it.

### Substring Matching Design
Alpha-numeric character options in the "Browse" section correspond to `LIKE` patterns of the form `{character}%`. For example, "A" corresponds to `a%`. "*" uses a specific line of SQL,
```SQL
WHERE ...
AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$'
```

In the "Search" section, the title, director, and star text boxes translate to `LIKE` patterns of the form `%{input}%`. For example, "Tom H" translates to
`%tom h%`.

### XML Parsing Performance Tuning
My parser utilizes a few in-memory hash tables, specifically for storing mappings. The first mapping is between XMl-file-given film ids and newly genreated movieIds for the database. The second mapping is between star names from casts124.xml and starIds genreated during the parsing of actors63.xml.

It is unknown how much this improved performance, as I used this from the get-go. However, as seen in the Project 3 demo video, it ran in less than two minutes and gave the expected results.

### Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.

    - #### Explain how Connection Pooling is utilized in the Fabflix code.

    - #### Explain how Connection Pooling works with two backend SQL.


### Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

#### How read/write requests were routed to Master/Slave SQL?
The IP address of the master server was hardcoded into the Webapp. All servlets involving writing to the master MySQL server have been recoded to use this specific IP address.


### JMeter TS/TJ Time Logs
#### Instructions of how to use the `log_processing.py` script to process the JMeter logs.
`log_processing.py` is a Python file. It's usage requires Python 3.6+, as it uses f-strings for output. As seen in the Project 5 demo video, **the most convenient way to run it is to drag and drop the log files onto `log_processing.py`**. Alternatively, you can run `log_processing.py` from a command line using:
```bash
python log_processing.py timelog1.csv timelog2.csv ...
```


### JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 150                         | 67.18116470124882                                  | 66.69322895351982                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

### Member Contribution
I am the only person in this group/team. I did all the work.