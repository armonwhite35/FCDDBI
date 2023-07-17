import java.sql.*; // required for using JDBC classes
import java.util.Scanner;
import java.lang.String;



public class Main {
    public static void main(String[] args) {

        try { // try to connect
            Connection conn =
                    DriverManager.getConnection("jdbc:mysql://falcon.cs.wfu.edu/FriendlyCars",
                            "S06587833", "whitac19!");
            System.out.println("Connected to server.");
            System.out.println();
            System.out.println("Welcome to Friendly Cars Dealership!");
            System.out.println();


            Scanner uInput = new Scanner(System.in);
            int option = 0;//menu option that loops until input is 7↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            System.out.println("Please select an option:");
            do {
                System.out.println("1. List a Table");
                System.out.println("2. Add Customer");
                System.out.println("3. Add Vehicle");
                System.out.println("4. Print Bill of Sale");
                System.out.println("5. Print Customer Report [P]"); //customers with purchases
                System.out.println("6. Print Customer Report [NP]"); //customers with no purchases
                System.out.println("7. Print Monthly Report");
                System.out.println("8. Exit");
                option = uInput.nextInt();
                System.out.println();

                switch (option){
                    case 1:
                        System.out.println("Which table would you like to print?");
                        list_table(conn, uInput.next());
                        break;
                    case 2:
                        add_customer(conn);
                        break;
                    case 3:
                        add_vehicle(conn);
                        break;
                    case 4:
                        System.out.println("For which customer would you like a BOS? (First name, Last name)");
                        System.out.print("First name: ");
                        String fn = uInput.next();
                        System.out.print("Last name: ");
                        String ln = uInput.next();
                        bill_of_sale(conn, fn, ln);
                        break;
                    case 5:
                        customer_report_purchased(conn);
                        break;
                    case 6:
                        customer_report_not_purchased(conn);
                        break;
                    case 7:
                        System.out.println("Please type a month and a year:");
                        System.out.print("Month: ");
                        int m = uInput.nextInt();
                        System.out.print("Year: ");
                        int y = uInput.nextInt();
                        monthly_sales_report(conn, m, y);
                        break;
                }
            }while (option != 8) ;
            conn.close();
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }

    static void list_table(Connection conn, String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        // create the query string
        String query = "select * from " + tableName;
        ResultSet rset = stmt.executeQuery(query);
        printResults(rset);
    }
    static void add_vehicle(Connection conn) throws SQLException{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the following information for a new vehicle on a single line separated by commas: ");
        System.out.println("VIN (17 chars), license_num (6-7 chars), manufacturer, model, list_price, date_manufacture (yr-month-date), date_delivery, num_doors, weight, capacity, color, milage, tradein_status, warranty");
        String lineIn = in.nextLine();
        String[] fields = lineIn.split(",");

        // create the query string
        String query = "insert into Cars(VIN, license_num, manufacturer, model, list_price, " +
                "date_manufacture, date_delivery, num_doors, weight, capacity, " +
                "color, milage, tradein_status, standard_warranty) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);

        for(int i=0; i<14; i++){
            pstmt.setString(i+1, fields[i].trim());
        }
        pstmt.executeUpdate();
        System.out.println("Added Successfully");
        System.out.println();
    }

    static void add_customer(Connection conn) throws SQLException{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the following information for a new customer on a single line separated by commas: ");
        System.out.println("CustomerID, SalespersonID, First Name, Last Name, Phone Number");
        String lineIn = in.nextLine();
        System.out.println(lineIn);
        String[] fields = lineIn.split(",");

        // create the query string
        String query = "insert into Customers(CID, SID, first_name, last_name, phone_num) values(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);

        for(int i=0; i<5; i++){
            pstmt.setString(i+1, fields[i].trim());
        }
        pstmt.executeUpdate();
        System.out.println("Added Successfully");
        System.out.println();
    }

    static void customer_report_purchased(Connection conn) throws SQLException{
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM Customers NATURAL JOIN Sales WHERE EXISTS(SELECT CID FROM Customers\n" +
                "WHERE Customers.CID = Sales.CID);";
        ResultSet rset = stmt.executeQuery(query);
        printResults(rset);
    }

    static void customer_report_not_purchased(Connection conn) throws SQLException{
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM Customers LEFT JOIN Sales ON Customers.CID = Sales.CID\n" +
                "WHERE Sales.CID IS NULL;";
        ResultSet rset = stmt.executeQuery(query);
        printResults(rset);
    }

    static void bill_of_sale(Connection conn, String fn, String ln) throws SQLException{
        Statement stmt = conn.createStatement();
        String query = "SELECT saleID, CID, SID, first_name, last_name, VIN, license_num, milage\n" +
                "FROM Customers NATURAL JOIN Sales NATURAL JOIN Cars WHERE " +
                "first_name = \"" + fn + "\" AND last_name = \"" + ln + "\";";
        ResultSet rset = stmt.executeQuery(query);
        printResults(rset);
    }

    static void monthly_sales_report(Connection conn, int month, int year) throws SQLException{
        Statement stmt = conn.createStatement();
        String query = "SELECT Salespeople.SID, first_name, last_name, SUM((price_negotiated + fees)* commisison) AS tot_sales\n" +
                "FROM Sales NATURAL JOIN Salespeople\n" +
                "WHERE MONTH(date_sale) = " + month + " AND YEAR(date_sale) = " + year + "\nGROUP BY SID;";
        ResultSet rset = stmt.executeQuery(query);
        printResults(rset);
    }

    private static void printResults(ResultSet rs){
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            System.out.println("\n" + metaData.getTableName(2) + ":");

            int colCount = metaData.getColumnCount();
            for(int i=1; i<=colCount; i++){
                String format = "%-25s";
                System.out.printf(format, metaData.getColumnName(i));
            }
            System.out.println();

            while (rs.next ()) {
                for(int i=1; i<=colCount; i++){
                    String format = "%-25s";
                    System.out.printf(format, rs.getString(i));
                }
                System.out.println();
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

}

