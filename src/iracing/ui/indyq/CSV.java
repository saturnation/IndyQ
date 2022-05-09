package iracing.ui.indyq;

import java.io.*;

public class CSV {

    static void dump(final Data d) {
        boolean new_file = false;
        File file = new File("laps.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
                new_file = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, true);
            PrintStream ps = new PrintStream(fos);
            if (new_file) {
                ps.println("lap1,lap2,lap3,lap4,avg");
            }
            for (Float l : d.laps) {
                output(ps, l);
            }
            ps.println(d.avg_lap_time);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void output(PrintStream ps, float lap) {
        ps.print(lap);
        ps.print(',');
    }
}
