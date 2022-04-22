package datastudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please enter the path for your analytics csv as first parameter");
            return;
        }

        String source = args[0];
        String destination = null;
        if (args.length == 1) {
            destination = new File(source).getParent() + "/";
        } else {
            destination = args[1];
        }

        JSONParser parser = new JSONParser();
        BufferedReader br = new BufferedReader(new FileReader(
                new File(source)));
        String line;
        String json;

        br.readLine();
        while ((line = br.readLine()) != null) {
            // we need to extract the json first, then we can split by ',' otherwise the
            // json will also be splited
            json = line.substring(line.indexOf("{"), line.length() - 1).replaceAll("\"\"", "\"");
            String[] cols = line.split(",");
            parser.parseConfiguration(json, cols[0], cols[1], cols[2]);
        }
        parser.writeToCSV(destination);
    }
}
