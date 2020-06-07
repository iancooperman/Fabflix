# Fabflix
#### By Ian Coooperman, #36747912

### Demos
Project 1 demo can be found [here](https://www.youtube.com/watch?v=gSUd3sx91NA&feature=youtu.be).

Project 2 demo can be found [here](https://www.youtube.com/watch?v=IIdI8JKQr2c&feature=youtu.be).

Project 3 demo can be found [here](https://youtu.be/EX0emZpuaIQ).

Project 4 demo can be found [here](https://www.youtube.com/watch?v=B6YfYBYagEw).

Project 5 demo can be found [here]();

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
#### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
Servlets involving JDBC connections can be found in `Webapp/src/main/java`. The `context.xml` file, modified for connection pooling can be found in `Webapp/web/META-INF`.

#### Explain how Connection Pooling is utilized in the Fabflix code.
All database connections have had connection pooling enabled. As seen in the JMeter test data, it has yielded a significant performance improvement by keeping connections open and reusing them.

#### Explain how Connection Pooling works with two backend SQL.
Connection Pooling improves Master/Slave database read/write performance by saving open connections to be used later. It's enabled within the Webapp, so it's enabled on the Master and Slave server instances.


### Master/Slave
#### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
Servlets involving JDBC connections can be found in `Webapp/src/main/java`.


#### How read/write requests were routed to Master/Slave SQL?
The IP address of the master server was hardcoded into the Webapp. All servlets involving writing to the master MySQL server have been recoded to use this specific IP address. MySQL reads use the local server via localhost.


### JMeter TS/TJ Time Logs
#### Instructions of how to use the `log_processing.py` script to process the JMeter logs.
`log_processing.py` is a Python file. It's usage requires Python 3.6+, as it uses f-strings for output. As seen in the Project 5 demo video, **the most convenient way to run it is to drag and drop the log files onto `log_processing.py`**. Alternatively, you can run `log_processing.py` from a command line using:
```bash
python log_processing.py timelog1.csv timelog2.csv ...
```
The logs themselves can be found in the `JMeter` folder, as can the graph images and JMeter tests.
`log_processing.py` can be found under `Webapp`.


### JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](./JMeter/GraphImages/SingleInstanceHTTP1Thread.png)   | 92                         | 8.83                                  | 8.31                        | It makes sense for this test to be the fastest of the single-instance tests. There was only 1 thread.           |
| Case 2: HTTP/10 threads                        | ![](JMeter/GraphImages/SingleInstanceHTTP10Threads.png)   | 113                         | 31.1                                  | 30.7                        | A greater amount of time is taken due to having to serve 10 "users" at once.          |
| Case 3: HTTPS/10 threads                       | ![](./JMeter/GraphImages/SingleInstanceHTTPS10Threads.png)   | 110                         | 29.2                                  | 28.7                        | Marginally lower than the previous case. Perhaps not enough of a difference to be statistically significant. This might have something to do with HTTP/2.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](./JMeter/GraphImages/SingleInstanceHTTPnoCP10Threads.png)   | 150                         | 67.2                                  | 66.7                        | Having to open and close a new database connection for every request eats up a huge amount of time.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](./JMeter/GraphImages/ScaledInstanceHTTP1Thread.png)   | 93                         | 9.82                                  | 9.11                        | Slightly higher then the equivalent test on the single instance. This could be because some extra time is taken by the load balancer to serve content from either the Master server or the Slave server.          |
| Case 2: HTTP/10 threads                        | ![](./JMeter/GraphImages/ScaledInstanceHTTP10Threads.png)   | 99                         | 16.1                               | 15.5                        | Load balancing comes in handy as half of the "users" are routed to the Master server and half to the Slave. This is significantly faster than the equivalent case on the single instance.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](./JMeter/GraphImages/ScaledInstanceHTTPnoCP10Threads.png)   | 120                         | 33.2                                  | 32.4                        | Creating and closing a database connection for every request once again takes its toll.          |

### Member Contribution
I am the only person in this group/team. I did all the work.