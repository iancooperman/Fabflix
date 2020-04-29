# Fabflix
#### By Ian Coooperman, #36747912

### Demos
Project 1 demo can be found [here](https://www.youtube.com/watch?v=gSUd3sx91NA&feature=youtu.be).

Project 2 demo can be found [here](https://www.youtube.com/watch?v=IIdI8JKQr2c&feature=youtu.be).

As of recording each demo, the latest commit is one commit behind updating README.md.

### Deployment Instructions
This project is using Maven. Deployment can be done the same way as instructed.

1. Clone the repo.
    ```bash
    git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-132.git
    ```
2. Inside the repo, where  pom.xml is, build the .war file.
    ```bash
    mvn package
    ```
3. Copy the newly built .war file to the tomcat webapps folder.

### Substring Matching Design
Alpha-numeric character options in the "Browse" section correspond to `LIKE` patterns of the form `{character}%`. For example, "A" corresponds to `a%`. "*" uses a specific line of SQL,
```SQL
WHERE ...
AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$'
```

In the "Search" section, the title, director, and star text boxes translate to `LIKE` patterns of the form `%{input}%`. For example, "Tom H" translates to
`%tom h%`.
### Member Contribution
I am the only person in this group/team. I did all the work.