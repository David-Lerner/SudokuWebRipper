import com.ehsunbehravesh.asyncwebreader.AsyncWebReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author David
 */
public class SudokuPuzzler {
    //website base url
    public static final String PUZZLER_URL = "http://www.sudokupuzzler.com/sp.asp?number=";
    public static final String REFERER_URL = "http://www.sudokupuzzler.com/sp.asp";
    //table naming constants
    public static final String SUDOKU = "sudoku";
    public static final String ID = "id";
    public static final String PUZZLE = "puzzle";
    public static final String DIFFICULTY = "difficulty";
    public static final String GIVEN = "given"; 
    public static final String FAILURE = "failures";
    public static final String URL = "url";
    //range (inclusive) of puzzles to read
    public static final int START = 1;
    public static final int END = 1;
    
    public static void main(String[] args) throws Exception {        
        String[] urls;
        int oldFailures = 0;
        
        //recover old urls
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:puzzler.db");
      
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) FROM %s;", FAILURE));
            rs.next();
            urls = new String[rs.getInt(1)];
            rs.close();
            stmt.close();
      
            stmt = c.createStatement();
            rs = stmt.executeQuery(String.format("SELECT * FROM %s;", FAILURE));
            int i = 0;
            while ( rs.next() ) {
                urls[i] = rs.getString("url");
                i++; 
            }
            rs.close();
            stmt.close();
      
            stmt = c.createStatement();
            stmt.executeUpdate(String.format("DELETE FROM %s;", FAILURE));
      
            c.close();
            System.out.println("Attempting "+urls.length+" old failed urls");
        } catch ( Exception e ) {
            urls = new String[0];
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        //use custom defined range if there are no failures from the last execution
        if (urls.length == 0) {
            urls = new String[END-START+1];
            for (int i = START; i <= END; i++) {
                urls[i-START] = PUZZLER_URL+i;
            }
            System.out.println("Attempting "+urls.length+" new urls");
        }
        
        //scrape puzzles from website
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Referer", REFERER_URL);
        AsyncWebReader webReader = new AsyncWebReader(5, urls, headers);
        webReader.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
            if (arg instanceof Exception) {
                Exception ex = (Exception) arg;
                System.out.println(ex.getMessage());
            } else if (arg instanceof Object[]) {
                Object[] objects = (Object[]) arg;
                HashMap<String, String> result = (HashMap<String, String>) objects[0];
                String[] success = (String[]) objects[1];
                String[] fail = (String[]) objects[2];
                System.out.println("Failures: "+fail.length);
                System.out.println("Successes: "+success.length);
                
                Connection c = null;
                Statement stmt = null;
                try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:puzzler.db");

                stmt = c.createStatement();
                String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY NOT NULL," +
                    "%s CHAR(81) NOT NULL," + 
                    "%s CHAR(50)," + 
                    "%s INTEGER" +
                    ");", SUDOKU, ID, PUZZLE, DIFFICULTY, GIVEN);
                stmt.executeUpdate(sql);
                
                stmt = c.createStatement();
                sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                    "%s VARCHAR(255) NOT NULL" + 
                    ");", FAILURE, URL); 
                stmt.executeUpdate(sql);
                stmt = c.createStatement();
                for (String failure: fail) {
                    sql = String.format("INSERT INTO %s (%s) VALUES ('%s');",
                            FAILURE, URL, failure);
                    stmt.executeUpdate(sql);
                }
                
                c.setAutoCommit(false);
                stmt = c.createStatement();
                    for (String succes : success) {
                        String page = result.get(succes);
                        StringBuilder sb = new StringBuilder();
                        int index = page.indexOf("<TABLE");
                        int given = 0;
                        for (int j = 0; j < 81; j++) {
                            index = page.indexOf("value=", index)+7;
                            char cell = page.charAt(index);
                            if (!Character.isDigit(cell))
                                cell = '0';
                            else if (cell != '0')
                                given++;
                            sb.append(cell);
                        }   
                        index = page.indexOf("value=", index)+7;
                        int id = Integer.parseInt(succes.substring(succes.indexOf("=") + 1));
                        String puzzle = sb.toString();
                        String difficulty = page.substring(index, page.indexOf("#", index)-1);
                        sql = String.format("INSERT INTO %s (%s,%s,%s,%s) "
                                + "VALUES (%d,'%s','%s',%d);",
                                SUDOKU, ID, PUZZLE, DIFFICULTY, GIVEN, 
                                id, puzzle, difficulty, given);
                        stmt.executeUpdate(sql);
                    }
                //useful query to update all givens: 
                //UPDATE sudoku SET given = LENGTH(REPLACE(puzzle, '0', ''));
                stmt.close();
                c.commit();
                c.close();
                } catch ( Exception e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                }
                
            }
        }
        });
    Thread t = new Thread(webReader);
    t.start();
    t.join();
    }
    
    private static void writeMatrix(int[] sudoku) {
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0)
                System.out.println(" -----------------------");
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) System.out.print("| ");
                System.out.print(sudoku[i*9 + j] == 0 ? " " : 
                        Integer.toString(sudoku[i*9 + j]));

                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }
}

