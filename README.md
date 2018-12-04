# Books-Database-Schema
## Instructions to run locally 
### Mac0S

0) Clone repository 

```
git clone https://github.com/LiangJoshua/Books-Database-Schema.git
```

1) Setting environtment correctly
```
CLASSPATH=$CLASSPATH:/usr/share/java/mysql-connector-java.jar
```

### Then (absolute Unix pathname)
```
/Users/HTENYWGGG/Documents/CS157A_Project/Books-Database-Schema/CS157A-project
```

2) Download Java and MySQL 

````
https://www.oracle.com/technetwork/java/index.html
https://dev.mysql.com/downloads/mysql/
````

3) Open Terminal and run JDBC.java in correct directory (FOR MAC)
````
MacBookAIr:~ macadmin$ cd /Users/macadmin/Desktop/GitHub/Books-Database-Schema/CS157A-project/src 
MacBookAIr:src macadmin$ javac *.java
MacBookAIr:src macadmin$ java -cp mysql-connector-java-8.0.13.jar:. JDBC
````

4) Open Terminal and run JDBC.java in correct directory (FOR WINDOWS)
````
Windows:~ macadmin$ cd /Users/macadmin/Desktop/GitHub/Books-Database-Schema/CS157A-project/src 
Windows:src macadmin$ javac JDBC.java
Window:src macadmin$ java -cp .;mysql-connector-java-8.0.13.jar JDBC
````