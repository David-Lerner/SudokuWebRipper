import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

/**
 *
 * @author David
 */
public class ToText {
    //database name
    public static final String DB_NAME = "puzzler.db";
    //table naming constants
    public static final String SUDOKU = "sudoku";
    public static final String ID = "id";
    public static final String PUZZLE = "puzzle";
    public static final String DIFFICULTY = "difficulty";
    public static final String GIVEN = "given"; 
    //number of puzzles to send to text file
    public static final int NUM = 500; 
    //name of text file
    public static final String FILE_NAME = "puzzler.txt";
    
    public static void main(String[] args) {        
        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:puzzler.db");
      
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s LIMIT %d;", SUDOKU, NUM));
            BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME));
            while ( rs.next() ) {
                out.write("Grid "+rs.getInt(ID)+": "+rs.getString(DIFFICULTY)+"\n");
                char[] puzzle = rs.getString(PUZZLE).toCharArray();
                for (int i = 0; i < 81; i++) {
                    out.write(puzzle[i]);
                    if ((i + 1) % 9 == 0)
                        out.write("\n");
                }
            }
            out.close();
            rs.close();
            stmt.close();
      
            c.close();
            System.out.println("Complete");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
