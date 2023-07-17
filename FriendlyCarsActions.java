public class FriendlyCarsActions {

    public FriendlyCarsActions() {
    }

    // retrieve and list the data from a table identified as an argument
    static void list_table(Connection conn, String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        // create the query string
        String query = "select * from " + tableName;
        ResultSet rset = stmt.executeQuery(query);
    }
    static void add_vehicle(Connection conn, Scanner in) throws SQLException{
        System.out.println("Enter the following information for a new vehicle on a single line separated by commas: ");
        System.out.println("VIN, license_num, manufacturer, list_price, model, date_manufacture, date_delivery, num_doors, weight, capacity, color, milage, tradein_status, standard_warranty");
        String lineIn = in.nextLine();
        String[] fields = lineIn.split(",");

        // create the query string
        String query = "insert into customers(VIN, license_num, manufacturer, list_price, model, " +
                "date_manufacture, date_delivery, num_doors, weight, capacity, " +
                "color, milage, tradein_status, standard_warranty) values(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);

        for(int i=0; i<14; i++){
            pstmt.setString(i+1, fields[i].strip());
        }
        pstmt.executeUpdate();
    }


}
