
import com.david.completesudoku.Sudoku;
import com.david.completesudoku.SudokuSolver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 *
 * @author David
 */
public class UpdatePuzzler {
    public static final String DB_NAME = "puzzler.db";
    //table naming constants
    public static final String SUDOKU = "sudoku";
    public static final String ID = "id";
    public static final String PUZZLE = "puzzle";
    
    //sudoku size
    public static final int SIZE = 9;
    //which ids do you want to start updating from
    public static final int OFFSET = 2730;
    
    public static void main(String[] args) throws Exception { 
        Connection c = null;
        Statement stmt = null;
        int count = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);
            
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) FROM %s;", SUDOKU));
            rs.next();
            count = rs.getInt(1);
            rs.close();
            stmt.close();
            System.out.println("Updating "+(count-OFFSET)+" rows...");
            for (int id = 1+OFFSET; id <= count; id++) {
                stmt = c.createStatement();
                String sql = String.format("SELECT %s FROM %s WHERE %s=%d;", PUZZLE, SUDOKU, ID, id);
                rs = stmt.executeQuery(sql);
                char[] puzzle = rs.getString(PUZZLE).toCharArray();
                rs.close();
                int[][] array = new int[SIZE][SIZE];
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        array[i][j] = puzzle[i*SIZE+j] - '0'; // char to int
                    }
                }
                SudokuSolver s = new SudokuSolver(new Sudoku(array));
                s.solve();
                StringBuilder sb = new StringBuilder();
                sb.append("UPDATE ");
                sb.append(SUDOKU);
                sb.append(" SET ");
                for (int i = 1; i < SudokuSolver.STRATEGY_NUMBER; i++) {
                    sb.append(SudokuSolver.getStrategyName(i).replace(' ', '_'));
                    sb.append('=');
                    sb.append(s.getStrategyCount(i));
                    sb.append(',');
                }
                for (int i = 1; i < SudokuSolver.STRATEGY_NUMBER; i++) {
                    sb.append(i == 1 ? " Cell_" : ", Cell_");
                    sb.append(SudokuSolver.getStrategyName(i).replace(' ', '_'));
                    sb.append('=');
                    sb.append(s.getStrategyCountByCell(i));
                    
                }
                sb.append(" WHERE ");
                sb.append(ID);
                sb.append('=');
                sb.append(id);
                sb.append(';');
                stmt.executeUpdate(sb.toString());
                stmt.close();
            }
      
            c.close();
            System.out.println("Complete.");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
