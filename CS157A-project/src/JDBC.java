
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
    public String username = "root"; //username of the MySQL Server
    public String password = "password"; //password of the MySQL Server

    static Connection connection = null;
    static Statement statement = null;

    /**
     * Method that will login and connect to your MySQL Server
     */
    public void loginToDB() {

        try {
            // Connects to MySQL Database... make sure url, username, and password are all correct for this to work
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false", username, password);
            statement = connection.createStatement();
        } catch (SQLException ex) {
            // Error statement if you have connection issues
            System.out.println("Connection Error. Check username, password, or localhost connection.");
        }
    }

    /**
     * Method that creates a database with four tables and populates them by scanning in text files.
     * Tables:
     * 1. authors
     * 2. authorISBN
     * 3. titles
     * 4. publishers
     */
    public void populateTables() {

        try {

            // Connects to MySQL Server and drops "BOOKS" database if it already exists
            ResultSet result = connection.getMetaData().getCatalogs();
            while (result.next()) {
                String databases = result.getString(1);
                if (databases.contains("BOOKS")) {
                    statement.executeUpdate("DROP DATABASE BOOKS;");
                }
            }
            // Files that will be scanned to populate the proper tables
            Scanner authorScan = new Scanner(new File("authorList.txt"));
            Scanner publisherScan = new Scanner(new File("publishers.txt"));
            Scanner isbnScan = new Scanner(new File("authorISBN.txt"));
            Scanner titleScan = new Scanner(new File("titleTable.txt"));

            // Creates Database "BOOKS" and initialize the tables
            statement.executeUpdate("CREATE DATABASE BOOKS");
            String useDB = "USE books";
            String authors = "CREATE TABLE authors (authorID INTEGER NOT NULL auto_increment, first CHAR(20) NOT NULL, last CHAR(20) NOT NULL, PRIMARY KEY (authorID))";
            String publishers = "CREATE TABLE publishers( publisherID INTEGER NOT NULL auto_increment, publisherName CHAR(100) NOT NULL, PRIMARY KEY (publisherID))";
            String title = "CREATE TABLE title (isbn CHAR(10) NOT NULL, title VARCHAR(500) NOT NULL, editionNumber INTEGER NOT NULL, Year CHAR(4) NOT NULL, publisherID INTEGER NOT NULL, price FLOAT NOT NULL, PRIMARY KEY (isbn), FOREIGN KEY (publisherID) REFERENCES publishers(publisherID))";
            String authorISBN = "CREATE TABLE authorISBN (authorID INTEGER NOT NULL, isbn CHAR(10) NOT NULL, FOREIGN KEY (isbn) REFERENCES title(isbn), FOREIGN KEY (authorID) REFERENCES authors(authorID))";
            statement.execute(useDB);
            statement.execute(authors);
            statement.execute(publishers);
            statement.execute(title);
            statement.execute(authorISBN);

            // Print out the queries and the creation of the database/tables to console
            System.out.println("CREATE DATABASE BOOKS");
            System.out.println(useDB + ";\n" +
                    authors + ";\n" +
                    authorISBN + ";\n" +
                    title + ";\n" +
                    publishers + ";\n");


            // Populate authors table by scanning the first and last names from authorList.txt
            while (authorScan.hasNextLine()) {
                String next = authorScan.nextLine();
                String[] hold = next.split(" "); // Parses the file by " " space to separate first and last name
                String firstN = hold[0]; // hold first name
                String lastN = hold[1]; // hold last name
                statement.execute("Insert INTO authors(first, last) VALUES ('" + firstN + "','" + lastN + "')");
                System.out.println("Insert INTO authors(first, last) VALUES ('" + firstN + "','" + lastN + "');");
            }
            // Prints new line to console for formatting purposes
            System.out.println();

            // SQL statement that selects all authors from authors table
            ResultSet rs1 = statement.executeQuery("Select * from authors;");

            // Print out the query to console
            System.out.println("Select * from authors;\n");
            System.out.printf("%-10s %-10s %-10s \n", "authorID", "first", "last");

            // Prints all the authors with their ID, first name, last name, from the authors table to the console
            while (rs1.next()) {
                System.out.printf("%-10s %-10s %-10s \n", rs1.getString("authorID"), rs1.getString("first"), rs1.getString("last"));
            }

            // Prints new line to console for formatting purposes
            System.out.println();

            // Populate publishers table by scanning the publisher names from publishers.txt
            while (publisherScan.hasNextLine()) {
                String next = publisherScan.nextLine();
                statement.execute("Insert INTO publishers(publisherName) VALUES ('" + next + "');");
                System.out.println("Insert INTO publishers(publisherName) VALUES ('" + next + "');");
            }

            // Prints new line to console for formatting purposes
            System.out.println();

            // SQL statement that selects all publishers from publishers table
            ResultSet rs3 = statement.executeQuery("SELECT * FROM publishers;");

            // Print out the query to console
            System.out.println("SELECT * FROM publishers;\n");
            System.out.printf("%-20s %-20s \n", "publisherID", "publisherName");
            System.out.println();

            // Prints all the publishers with their ID and name from the publishers table to the console
            while (rs3.next()) {
                System.out.printf("%-20s %-20s \n", rs3.getString("publisherID"), rs3.getString("publisherName"));
            }

            // Printes new line to console for formatting purposes
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

            // Formatting purposes
            System.out.println();

            //Select all columns from title 
            ResultSet rs4 = statement.executeQuery("select * from title;");
            // Print out the query to console
            System.out.println("select * from title;\n");
            System.out.printf("%-15s %-40s %-20s %-20s %-20s %-20s \n", "isbn", "title", "editionNumber", "Year", "publisherID", "price");
            // Prints all the titles with isbn, title, edition name, year, publisher ID, and price
            while (rs4.next()) {
                System.out.printf("%-15s %-40s %-20s %-20s %-20s %-20s \n", rs4.getString("isbn"), rs4.getString("title"),
                        rs4.getString("editionNumber"), rs4.getString("Year"), rs4.getString("publisherID"), rs4.getString("price"));

            }

            System.out.println();
            // Create author ID and arrayList of author IDs
            int AIDHold = 0; // author ID hold
            ArrayList<Integer> AID = new ArrayList<Integer>();

            // SQL Statement that selects all author IDs from authors table
            ResultSet publisherResultSet = statement.executeQuery("SELECT * FROM publishers;");
            // iterate through author IDs and add them to array list
            while (publisherResultSet.next()) {
                AID.add(publisherResultSet.getInt("publisherID"));

            }

            // Prints new line to console for formatting purposes
            System.out.println();

            // iterate through the isbn list file and insert them into the author isbn table
            while (isbnScan.hasNextLine()) {
                String next = isbnScan.nextLine();
                statement.execute("Insert INTO authorISBN(authorID,isbn) VALUES ('" + AID.get(AIDHold) + "','" + next + "');");
                System.out.println("Insert INTO authorISBN(authorID,isbn) VALUES ('" + AID.get(AIDHold) + "','" + next + "');");
                AIDHold++;
            }
            System.out.println();
            //Select all columns from authorISBN
            ResultSet rs2 = statement.executeQuery("select * from authorISBN;");
            // Print out the query to console
            System.out.println("select * from authorISBN;\n");
            System.out.println();
            System.out.printf("%-20s %-20s \n", "authorID", "isbn");
            // Prints all the authorID, isbn
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

    /**
    *Method that alphabetically selects and the prints authors by last name first. 
    */
    public void selectAllauthors() {

        System.out.println("select all authors from the authors table alphabetically\n");
        try {
        	// SQL statement that selects all authors from authors table by last name 
            ResultSet authorPrint = statement.executeQuery("Select last, first from authors order by last, first;");
            System.out.println("Select last, first from authors order by last, first;\n");
            System.out.printf("%-20s %s\n", "LastName", "FirstName");
            System.out.println();
            // Prints all the authors by last name, to the console
            while (authorPrint.next()) {
                System.out.printf("%-20s %s\n", authorPrint.getString("last"), authorPrint.getString("first"));
            }
        } catch (SQLException E) {
            E.printStackTrace();
        }

    }
    /**
    *Method that selects and prints list of publishers names.  
    *
    */
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
    /**
    *Method lists information of specified publisher based on publisherName. 
    */
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
    /**
    *Method that inserts author into database of matching authorID, then looks for specified authorID
    */
    public void addAuthor() {
        try {

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

   /**
    *Method updates author based on specified authorID.
    */
    public void editAuthor() {
        try {

            System.out.println();
            System.out.println("edit/update the existing information about an author i..e the name");
            statement.execute("UPDATE authors SET last='Leinbach', first='Justin' where authorID=16;");
            System.out.println();
            System.out.println("UPDATE authors SET last='Leinbach', first='Justin' where authorID=16;\n");


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
    /**
    *Method adds book title and corresponding info for specified publisherID
    */
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
    /**
    *Method adds publisher and corresponding data to database. 
    */
    public void addPublisher() {
        try {

            System.out.println("add new publish (adds new publisher and update new information for other tables)");
            statement.execute("Insert INTO publishers(publisherName) VALUES ('" + "CS157A Group 1" + "');");
            System.out.println();
            System.out.println("Insert INTO publishers(publisherName) VALUES ('" + "CS157A Group 1" + "');");

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
    /**
    *Method updates publishers name of specified publisherID 
    */
    public void editPublisher() {
        try {
            // Step 8
            System.out.println("edit/update the existing information about a publisher");
            System.out.println("UPDATE publishers SET publisherName = 'CS157A Group 9' where publisherID = '16';");
            statement.execute("UPDATE publishers SET publisherName = 'CS157A Group 9' where publisherID = '16';");

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

            System.out.println("Logging into MySQL...");
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