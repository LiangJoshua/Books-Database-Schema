
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Java project for CS 157A
 * Group Number: 9
 * Professor: Ahmed Ezzat
 *
 * @author Joshua Liang 010380383
 * Goal: Use JDBC to create a Books database, populate it, and then execute different
 * SQL statements to query or manipulate the Books database.
 */

public class JDBC {
    public String username = "root";
    public String password = "password";

    static Connection connection = null;
    static Statement statement = null;

    public void loginToDB() {
        // Enter user and password for sql connection. Change this to make it work locally.
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?useSSL=false", username, password);
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("Username or password incorrect.");
        }
    }

    public void populateTables() {

        // Initialize authorScanners to parse data files containing information to populate into tables

        try {

            // If the books database already exists from a previous run, drop it so a new one can be created.
            ResultSet result = connection.getMetaData().getCatalogs();
            while (result.next()) {
                String databases = result.getString(1);
                if (databases.contains("BOOKS")) {
                    statement.executeUpdate("DROP DATABASE BOOKS;");
                }
            }

            Scanner authorScan = new Scanner(new File("authorList.txt"));
            Scanner publisherScan = new Scanner(new File("publishers.txt"));
            Scanner isbnScan = new Scanner(new File("authorISBN.txt"));
            Scanner titleScan = new Scanner(new File("titleTable.txt"));

            // ---------- CREATE DATABASE BOOKS AND INITIALIZE TABLES ----------
            // Initialization of database "books" and tables
            statement.executeUpdate("CREATE DATABASE BOOKS");
            String useDB = "USE books";
            String authors = "CREATE TABLE authors (authorID INTEGER NOT NULL auto_increment, first CHAR(20) NOT NULL, last CHAR(20) NOT NULL, PRIMARY KEY (authorID))";
            String publishers = "CREATE TABLE publishers( publisherID INTEGER NOT NULL auto_increment, publisherName CHAR(100) NOT NULL, PRIMARY KEY (publisherID))";
            String title = "CREATE TABLE title (isbn CHAR(10) NOT NULL, title VARCHAR(500) NOT NULL, editionNumber INTEGER NOT NULL, Year CHAR(4) NOT NULL, publisherID INTEGER NOT NULL, price FLOAT NOT NULL, PRIMARY KEY (isbn), FOREIGN KEY (publisherID) REFERENCES publishers(publisherID))";
            String authorISBN = "CREATE TABLE authorISBN (authorID INTEGER NOT NULL, isbn CHAR(10) NOT NULL, FOREIGN KEY (isbn) REFERENCES title(isbn), FOREIGN KEY (authorID) REFERENCES authors(authorID))";
            statement.execute(useDB);

            // Print out the queries and the creation of the database
            System.out.println("CREATE DATABASE BOOKS");

            // Print out the execution of the create statements
            System.out.println(useDB + ";\n" +
                    authors + ";\n" +
                    authorISBN + ";\n" +
                    title + ";\n" +
                    publishers + ";\n");

            // execute all statements define for creating the books database and empty tables
            statement.execute(authors);
            statement.execute(publishers);
            statement.execute(title);
            statement.execute(authorISBN);

            // ---------- INITIALIZE TABLES WITH AT LEAST 15 ENTRIES ----------
            // insert all authors(first and last name) read from a file and inserted into the authors table
            while (authorScan.hasNextLine()) {
                String next = authorScan.nextLine();
                String[] hold = next.split(" ");
                String firstN = hold[0]; // hold first name
                String lastN = hold[1]; // hold last name
                statement.execute("Insert INTO authors(first, last) VALUES ('" + firstN + "','" + lastN + "')");
                System.out.println("Insert INTO authors(first, last) VALUES ('" + firstN + "','" + lastN + "');");
            }
            System.out.println();

            ResultSet rs1 = statement.executeQuery("Select * from authors;");
            System.out.println("Select * from authors;\n");
            System.out.printf("%-10s %-10s %-10s \n", "authorID", "first", "last");

            while (rs1.next()) {
                System.out.printf("%-10s %-10s %-10s \n", rs1.getString("authorID"), rs1.getString("first"), rs1.getString("last"));
            }

            System.out.println();

            // insert all publishers read from a file and inserted into the publishers table
            while (publisherScan.hasNextLine()) {
                String next = publisherScan.nextLine();
                statement.execute("Insert INTO publishers(publisherName) VALUES ('" + next + "');");
                System.out.println("Insert INTO publishers(publisherName) VALUES ('" + next + "');");
            }
            System.out.println();

            ResultSet rs3 = statement.executeQuery("SELECT * FROM publishers;");
            System.out.println("SELECT * FROM publishers;\n");
            System.out.printf("%-20s %-20s \n", "publisherID", "publisherName");
            System.out.println();

            // iterate through author IDs and add them to array list
            while (rs3.next()) {
                System.out.printf("%-20s %-20s \n", rs3.getString("publisherID"), rs3.getString("publisherName"));
            }
            System.out.println();

            // Create author ID and arrayList of author IDs
            int AIDHold = 0; // author ID hold
            ArrayList<Integer> AID = new ArrayList<Integer>();

            // Select all author IDs from authors table
            ResultSet publisherResultSet = statement.executeQuery("SELECT * FROM publishers;");
            // iterate through author IDs and add them to array list
            while (publisherResultSet.next()) {
                AID.add(publisherResultSet.getInt("publisherID"));
//                System.out.println(publisherResultSet.getString("authorID"));
            }
            System.out.println();


            // instantiate arrayList and temp variable for publisher IDs
            int PIhold = 0;
            ArrayList<Integer> PID = new ArrayList<Integer>();

            // iterate through the publisher ID list file and insert them into the publisher ID list
            ResultSet rs = statement.executeQuery("SELECT publisherID FROM publishers;");

            while (rs.next()) {
                PID.add(rs.getInt("publisherID"));

            }
            System.out.println();

            // iterate through the file, break up the string and get specific variables to insert into table
            while (titleScan.hasNextLine()) {
                String next = titleScan.nextLine();
                String[] hold = next.split("_"); // array of values read in from file
                String isbn = hold[0]; // isbn
                String title2 = hold[1]; // title of book
                int edition = Integer.parseInt(hold[2]); // edition of the book
                String year = hold[3]; // year the book was created
                int pid = Integer.parseInt(hold[4]); // publisher id
                float price = Float.parseFloat(hold[5]); // price of the book

                statement.execute("INSERT INTO title(isbn, title, editionNumber, Year, publisherID, price)VALUES ('" + isbn + "','" + title2 +
                        "','" + edition + "','" + year + "','" + PID.get(pid - 1) + "','" + price + "')");

                System.out.println("INSERT INTO title(isbn, title, editionNumber, Year, publisherID, price)VALUES ('" + isbn + "','" + title2 +
                        "','" + edition + "','" + year + "','" + PID.get(pid - 1) + "','" + price + "');");

                PIhold++;
            }

            System.out.println();


            ResultSet rs4 = statement.executeQuery("select * from title;");
            System.out.println("select * from title;\n");
            System.out.printf("%-15s %-40s %-20s %-20s %-20s %-20s \n", "isbn", "title", "editionNumber", "Year", "publisherID", "price");

            while (rs4.next()) {
                System.out.printf("%-15s %-40s %-20s %-20s %-20s %-20s \n", rs4.getString("isbn"), rs4.getString("title"),
                        rs4.getString("editionNumber"), rs4.getString("Year"), rs4.getString("publisherID"), rs4.getString("price"));

            }

            System.out.println();

            // iterate through the isbn list file and insert them into the author isbn table
            while (isbnScan.hasNextLine()) {
                String next = isbnScan.nextLine();
                statement.execute("Insert INTO authorISBN(authorID,isbn) VALUES ('" + AID.get(AIDHold) + "','" + next + "');");
                System.out.println("Insert INTO authorISBN(authorID,isbn) VALUES ('" + AID.get(AIDHold) + "','" + next + "');");
                AIDHold++;
            }
            System.out.println();

            ResultSet rs2 = statement.executeQuery("select * from authorISBN;");
            System.out.println("select * from authorISBN;\n");
            System.out.println();
            System.out.printf("%-20s %-20s \n", "authorID", "isbn");

            while (rs2.next()) {
                System.out.printf("%-20s %-20s \n", rs2.getString("authorID"), rs2.getString("isbn"));
            }

            System.out.println();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found! Please check your file paths.");
        } catch (SQLException ex) {
            System.out.println("Query for initializing tables found. Please check your file formatting.");
        }

    }


    public void selectAllauthors() {
        // Step 1
        System.out.println("select all authors from the authors table alphabetically\n");
        try {
            ResultSet authorPrint = statement.executeQuery("Select last, first from authors order by last, first;");
            System.out.println("Select last, first from authors order by last, first;\n");
            System.out.printf("%-20s %s\n", "LastName", "FirstName");
            System.out.println();

            while (authorPrint.next()) {
                System.out.printf("%-20s %s\n", authorPrint.getString("last"), authorPrint.getString("first"));
            }
        } catch (SQLException E) {
            E.printStackTrace();
        }

    }

    public void selectPublishers() {
        // Step 2
        System.out.println("Select all publishers from the publishers table\n");
        try {
            ResultSet publisherList = statement.executeQuery("Select publisherName from publishers;");
            System.out.println();
            System.out.println("Select publisherName from publishers;\n");
            System.out.println("publisherName\n");
            while (publisherList.next()) {
                System.out.println(publisherList.getString("publisherName"));
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void specificPublisher() {
        // Step 3
        System.out.println("select a specific publisher and list all books published by the publisher (include the title, year, and ISBN number. order the information alphabetically by title)");
        try {
            ResultSet publisherID = statement.executeQuery("select publisherID from publishers Where publisherName = 'Lulu';");
            System.out.println("select publisherID from publishers Where publisherName = 'Lulu';\n");
            System.out.println("publisherID\n");
            int PID2 = 0;
            while (publisherID.next()) {
                PID2 = publisherID.getInt("publisherID");
                System.out.println(publisherID.getInt("publisherID"));
            }
            System.out.println();

            // Step 3 (continued)
            // using the publisher id, get titles made by the publishers, along with other information
            ResultSet titleSet = statement.executeQuery("select title, year, isbn from title where publisherID = " + PID2 + " order by title;");
            System.out.println("select title, year, isbn from title where publisherID = " + PID2 + " order by title;");
            System.out.println();
            System.out.printf("%-30s %-5s %s \n", "title", "year", "isbn");

            while (titleSet.next()) {
                System.out.printf("%-30s %-5s %s \n", titleSet.getString("title"), titleSet.getInt("Year"), titleSet.getString("isbn"));
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addAuthor() {
        try {
            // Step 4
            System.out.println("adds new author and other information related to other tables");
            statement.execute("Insert INTO authors(first, last) VALUES ('" + "Joshua" + "','" + "Liang" + "')");
            System.out.println("Insert INTO authors(first, last) VALUES ('" + "Joshua" + "','" + "Liang" + "')\n");
            ResultSet updateID = statement.executeQuery("select authorID from authors where first = 'Joshua' AND last = 'Liang';");

            int insertID = 0;
            while (updateID.next()) {
                insertID = updateID.getInt("authorID");
            }


            // get author name form specified id number
            ResultSet beforeUpdate = statement.executeQuery("Select * from authors where authorID = 16;");
            System.out.println("Select * from authors where authorID = 16;\n");

            while (beforeUpdate.next()) {
                System.out.println();
                System.out.println("Before Edit/Update the existing information about an author");
                System.out.printf("%-10s %-10s %s \n", "authorID", "first", "last");
                System.out.printf("%-10s %-10s %s \n", beforeUpdate.getInt("authorID"), beforeUpdate.getString("first"), beforeUpdate.getString("last"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void editAuthor() {
        try {
            // Step 5
            System.out.println();
            System.out.println("edit/update the existing information about an author i..e the name");
            statement.execute("UPDATE authors SET last='Leinbach', first='Justin' where authorID=16;");
            System.out.println();
            System.out.println("UPDATE authors SET last='Leinbach', first='Justin' where authorID=16;\n");

            // Step 5 (continued)
            // check to make sure the update happened
            ResultSet afterUpdate = statement.executeQuery("Select * from authors where authorID = 16;");
            System.out.println("Select * from authors where authorID = 16;\n");

            while (afterUpdate.next()) {
                System.out.println();
                System.out.println("After Edit/Update the existing information about an author");
                System.out.printf("%-10s %-10s %s \n", "authorID", "first", "last");
                System.out.printf("%-10s %-10s %s \n", afterUpdate.getInt("authorID"), afterUpdate.getString("first"), afterUpdate.getString("last"));
            }
            ResultSet bu = statement.executeQuery("Select * from authorisbn where authorID = 16;");

            // print the entire result set from the authors isbn table where the author id is 16
            while (bu.next()) {
                System.out.println();
                System.out.println("Before Edit/Update the existing information about an authorisbn");
                System.out.printf("%-10s %-10s \n", "authorID", "isbn");
                System.out.printf("%-10s %-10s \n", bu.getString("authorID"), bu.getString("isbn"));
            }

            // get all isbns depending on author id
            ResultSet au = statement.executeQuery("Select * from authorisbn where authorID = 16;");

            while (au.next()) {
                System.out.println();
                System.out.println("After Edit/Update the existing information about an authorisbn");
                System.out.printf("%-10s %-10s \n", "authorID", "isbn");
                System.out.printf("%-10s %-10s \n", au.getString("authorID"), au.getString("isbn"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTitle() {
        try {
            // Step 6
            System.out.println();
            ResultSet old = statement.executeQuery("select * from title where publisherID = 15;");
            System.out.println("Before Add new info to title");
            System.out.println("select * from title where publisherID = 15;");

            while (old.next()) {
                System.out.println();
                System.out.printf("%-10s %-30s %-15s %-5s %-20s %-10s \n", "isbn", "title", "editionNumber", "Year", "publisherID", "price");
                System.out.printf("%-10s %-30s %-15s %-5s %-20s %-10s \n", old.getString("isbn"), old.getString("title"), old.getString("editionNumber"), old.getString("Year"), old.getString("publisherID"), old.getString("price"));
            }
            System.out.println();

            System.out.println("add a new title for an author");
            System.out.println();
            System.out.println("INSERT INTO title(isbn, title, editionNumber, Year, publisherID, price)VALUES ('9789655171', 'CS157A' ,'1' ,'2018' ,15 ,'9.99');");
            statement.execute("INSERT INTO title(isbn, title, editionNumber, Year, publisherID, price)VALUES ('9789655171', 'CS157A' ,'1' ,'2018' ,15 ,'9.99')");
            System.out.println();

            ResultSet addNew = statement.executeQuery("select * from title where publisherID = 15;");
            System.out.println("select * from title where publisherID = 15;");

            while (addNew.next()) {
                System.out.println();
                System.out.printf("%-10s %-30s %-15s %-5s %-20s %-10s \n", "isbn", "title", "editionNumber", "Year", "publisherID", "price");
                System.out.printf("%-10s %-30s %-15s %-5s %-20s %-10s \n", addNew.getString("isbn"), addNew.getString("title"), addNew.getString("editionNumber"), addNew.getString("Year"), addNew.getString("publisherID"), addNew.getString("price"));
            }
            System.out.println();

            statement.execute("INSERT INTO authorISBN(authorID, isbn)VALUES ('16', '9789655171');");
            ResultSet newAuthorISBN = statement.executeQuery("select * from authorISBN;");
            System.out.println("Select * from authorISBN;\n");
            System.out.printf("%-10s %-10s %-10s \n", "authorID", "first", "last");
            while (newAuthorISBN.next()) {
                System.out.printf("%-20s %-20s \n", newAuthorISBN.getString("authorID"), newAuthorISBN.getString("isbn"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPublisher() {
        try {
            // Step 7
            System.out.println("add new publish (adds new publisher and update new information for other tables)");
            statement.execute("Insert INTO publishers(publisherName) VALUES ('" + "CS157A Group 9" + "');");
            System.out.println();
            System.out.println("Insert INTO publishers(publisherName) VALUES ('" + "CS157A Group 9" + "');");

            ResultSet beforeUp = statement.executeQuery("select * from publishers;");
            System.out.println("select * from publishers;");

            while (beforeUp.next()) {
                System.out.println();
                System.out.println("Before Edit/Update the existing information about an publishers");
                System.out.printf("%-15s %-15s \n", "publisherID", "publisherName");
                System.out.printf("%-15s %-15s \n", beforeUp.getString("publisherID"), beforeUp.getString("publisherName"));
            }

            ResultSet AfterUp = statement.executeQuery("select * from publishers where publisherID = 16;");
            System.out.println("select * from publishers where publisherID = 16;");

            while (AfterUp.next()) {
                System.out.println();
                System.out.println("After Edit/Update the existing information about an publishers");
                System.out.printf("%-15s %-15s \n", "publisherID", "publisherName");
                System.out.printf("%-15s %-15s \n", AfterUp.getString("publisherID"), AfterUp.getString("publisherName"));
            }

            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editPublisher() {
        try {
            // Step 8
            System.out.println("edit/update the existing information about a publisher");
            System.out.println("UPDATE publishers SET publisherName = 'Kate Dinh' where publisherID = '16';");
            statement.execute("UPDATE publishers SET publisherName = 'Kate Dinh' where publisherID = '16';");

            ResultSet afterUp = statement.executeQuery("select * from publishers where publisherID = 16;");
            System.out.println("select * from publishers where publisherID = 16;");

            while (afterUp.next()) {
                System.out.println();
                System.out.println("After Edit/Update the existing information about an publishers");
                System.out.printf("%-15s %-15s \n", "publisherID", "publisherName");
                System.out.printf("%-15s %-15s \n", afterUp.getString("publisherID"), afterUp.getString("publisherName"));
            }


            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Establish a process ( i.e. connection to local mysql database )
        JDBC JDBC = new JDBC();


        try {

            System.out.println("Attempting to log into localhost!");
            // Enter user and password for sql connection. Change this to make it work locally.
            JDBC.loginToDB();

            JDBC.populateTables();


            // ---------- IMPLEMENTING SQL QUERIES TO MANIPULATE THE DATABASE ----------
            JDBC.selectAllauthors();
            JDBC.selectPublishers();
            JDBC.specificPublisher();
            JDBC.addAuthor();
            JDBC.editAuthor();
            JDBC.addTitle();
            JDBC.addPublisher();
            JDBC.editPublisher();


            // catch any exceptions that might occur
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}