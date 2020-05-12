# Fabflix
#### By Ian Coooperman, #36747912

### Demos
Project 1 demo can be found [here](https://www.youtube.com/watch?v=gSUd3sx91NA&feature=youtu.be).

Project 2 demo can be found [here](https://www.youtube.com/watch?v=IIdI8JKQr2c&feature=youtu.be).

Project 3 demo can be found [here](https://youtu.be/EX0emZpuaIQ).

As of recording each demo, the latest commit is one commit behind updating README.md.

### Deployment Instructions
This project is using Maven. Deployment can be done the same way as instructed.

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

### Member Contribution
I am the only person in this group/team. I did all the work.