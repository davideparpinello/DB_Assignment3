import java.sql.*;
import java.util.*;

public class A3_201494 {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    public static void main(String[] args) throws SQLException {

        // Connection to the database
        String url = "jdbc:postgresql://127.0.0.1/a3";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "");
        props.setProperty("ssl", "false");
        Connection conn = DriverManager.getConnection(url, props);

        Statement st = conn.createStatement();
        // Time variables
        long startTime, endTime;

        // First step
        startTime = System.nanoTime();
        st.execute("DROP TABLE IF EXISTS \"Professor\", \"Course\" CASCADE");
        endTime = System.nanoTime();

        System.out.println("Step 1 needs " + (endTime - startTime) + " ns");

        // Second step
        startTime = System.nanoTime();
        st.execute("CREATE TABLE \"Professor\"\n" +
                "(\n" +
                "    id integer NOT NULL,\n" +
                "    name character(50) NOT NULL,\n" +
                "    address character(50) NOT NULL,\n" +
                "    age integer NOT NULL,\n" +
                "    height real NOT NULL,\n" +
                "    CONSTRAINT \"Professor_pkey\" PRIMARY KEY (id)\n" +
                ")");

        st.execute("CREATE TABLE \"Course\"\n" +
                "(\n" +
                "    cid character(25) NOT NULL,\n" +
                "    title character(50) NOT NULL,\n" +
                "    area character(30) NOT NULL,\n" +
                "    instructor integer NOT NULL,\n" +
                "    CONSTRAINT \"Course_pkey\" PRIMARY KEY (cid),\n" +
                "    CONSTRAINT \"Course_instructor_fkey\" FOREIGN KEY (instructor)\n" +
                "        REFERENCES public.\"Professor\" (id) MATCH SIMPLE\n" +
                "        ON UPDATE NO ACTION\n" +
                "        ON DELETE NO ACTION" +
                ")");

        endTime = System.nanoTime();

        System.out.println("Step 2 needs " + (endTime - startTime) + " ns");
        st.close();


        // Step 3
        startTime = System.nanoTime();
        Set<Float> heights = new HashSet<Float>();
        for (int i = 0; i < 999999; i++) {
            Float height;
            do{
                do {
                    height = (float)(Math.random() * 210);
                }while (height == (float)185);
            }while(!heights.add(height));
        }
        List<Float> v_heights = new ArrayList<Float>(heights);

        Set<Integer> ids = new LinkedHashSet<Integer>();
        for (int i = 0; i < 1000000; i++) {
            int id;
            do{
                id = (int)(Math.random() * ((1000000 - 1) + 1)) + 1;
            }while(!ids.add(id));
        }
        List<Integer> v_ids = new ArrayList<Integer>(ids);

        /*List<Integer> v_id = new ArrayList<Integer>();
        for (int i = 1; i < 1000000; i++) {
            v_id.add(i);
        }

        List<String> v_name = new ArrayList<String>();
        for (int i = 1; i < 1000000; i++) {
            v_name.add("Prof"+i);
        }

        List<String> v_addr = new ArrayList<String>();
        for (int i = 1; i < 1000000; i++) {
            v_addr.add("AddrProf"+i);
        }

        List<Integer> v_age = new ArrayList<Integer>();
        for (int i = 1; i < 1000000; i++) {
            v_age.add((int)(Math.random() * ((65 - 23) + 1)) + 23);
        }
        */
        int count = 0;
        String q = "INSERT INTO \"Professor\"(id, name, address, age, height) VALUES(?,?,?,?,?)";
        PreparedStatement prepSt = conn.prepareStatement(q);
        for (int i = 0; i < 999999; i++) {
            prepSt.setInt(1, v_ids.get(i));
            prepSt.setString(2, randomAlphaNumeric(50));
            prepSt.setString(3, randomAlphaNumeric(50));
            prepSt.setInt(4, (int)(Math.random() * ((65 - 23) + 1)) + 23);
            prepSt.setFloat(5, v_heights.get(i));

            prepSt.addBatch();
            count++;
            if (count % 100 == 0 || count == heights.size()) {
                prepSt.executeBatch();
            }
        }

        prepSt.setInt(1, v_ids.get(999999));
        prepSt.setString(2,randomAlphaNumeric(50));
        prepSt.setString(3, randomAlphaNumeric(50));
        prepSt.setInt(4, (int)(Math.random() * ((65 - 23) + 1)) + 23);
        prepSt.setFloat(5, (float)185);
        prepSt.execute();
        prepSt.close();

        endTime = System.nanoTime();

        System.out.println("Step 3 needs " + (endTime - startTime) + " ns");


        // Step 4

        startTime = System.nanoTime();

        Set<String> cids = new HashSet<String>();
        for (int i = 0; i < 1000000; i++) {
            String cid;
            do{
                cid = randomAlphaNumeric(25);
            }while(!cids.add(cid));
        }


        List<String> v_cids = new ArrayList<String>(cids);

        count = 0;
        q = "INSERT INTO \"Course\"(cid, title, area, instructor) VALUES(?,?,?,?)";
        prepSt = conn.prepareStatement(q);
        for (int i = 0; i < 1000000; i++) {
            prepSt.setString(1, v_cids.get(i));
            prepSt.setString(2, randomAlphaNumeric(50));
            prepSt.setString(3, randomAlphaNumeric(30));
            prepSt.setInt(4, (int)(Math.random() * ((1000000 - 1) + 1)) + 1);

            prepSt.addBatch();
            count++;
            if (count % 100 == 0 || count == 1000000) {
                prepSt.executeBatch();
            }
        }

        prepSt.close();

        endTime = System.nanoTime();
        System.out.println("Step 4 needs " + (endTime - startTime) + " ns");

        // Step 5

        startTime = System.nanoTime();
        q = "SELECT id FROM \"Professor\"";
        st = conn.createStatement();
        ResultSet rs = st.executeQuery(q);

        while (rs.next()) {
            System.err.println(rs.getString(1));
        }

        endTime = System.nanoTime();
        System.out.println("Step 5 needs " + (endTime - startTime) + " ns");

        // Step 6

        startTime = System.nanoTime();
        st.execute("UPDATE \"Professor\"\n" +
                "SET height = 200\n" +
                "WHERE height = 185");
        endTime = System.nanoTime();
        System.out.println("Step 6 needs " + (endTime - startTime) + " ns");


        // Step 7

        q = "SELECT id, address FROM \"Professor\" WHERE height = 200";
        rs = st.executeQuery(q);

        while (rs.next()) {
            System.err.println(rs.getString(1) + " " + rs.getString(2));
        }

        endTime = System.nanoTime();
        System.out.println("Step 7 needs " + (endTime - startTime) + " ns");

        // Step 8

        startTime = System.nanoTime();
        st.execute("CREATE INDEX height_idx ON \"Professor\" (height)");
        endTime = System.nanoTime();
        System.out.println("Step 8 needs " + (endTime - startTime) + " ns");


        // Step9

        startTime = System.nanoTime();
        q = "SELECT id FROM \"Professor\"";
        rs = st.executeQuery(q);

        while (rs.next()) {
            System.err.println(rs.getString(1));
        }

        endTime = System.nanoTime();
        System.out.println("Step 9 needs " + (endTime - startTime) + " ns");

        // Step 10

        startTime = System.nanoTime();
        st.execute("UPDATE \"Professor\"\n" +
                "SET height = 210\n" +
                "WHERE height = 200");
        endTime = System.nanoTime();
        System.out.println("Step 10 needs " + (endTime - startTime) + " ns");

        // Step 11

        q = "SELECT id, address FROM \"Professor\" WHERE height = 210";
        rs = st.executeQuery(q);

        while (rs.next()) {
            System.err.println(rs.getString(1) + " " + rs.getString(2));
        }

        endTime = System.nanoTime();
        System.out.println("Step 11 needs " + (endTime - startTime) + " ns");

        st.close();
        conn.close();


    }
}


