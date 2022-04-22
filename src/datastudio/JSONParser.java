package datastudio;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
* class for saving multiple configuration JSONs into CSVs
*/

public class JSONParser {

    private List<String[]> componentLines;
    private List<String[]> configurationLines;
    private List<String[]> originpartLines;
    private List<String[]> parameterLines;

    private int componentID;
    private int configurationID;
    private int originPartID;
    private int parameterID;
    private boolean isInnerComponent;

    // JSON keys
    private final String FULL_LIST = "fullList";
    private final String ORIGIN_PART = "originPart";
    private final String PER_MAIN_COMPONENT = "perMainComponent";

    private final String ARTICLE_NO = "articleNr";
    private final String COMPONENT_ID = "componentId";
    private final String PARAMETER = "parameters";
    private final String LABEL = "label";
    private final String KEY = "key";
    private final String TYPE = "type";
    private final String VALUE = "value";
    private final String VALUE_LABEL = "valueLabel";
    private final String COUNT = "count";

    // default value
    private final String EMPTY = "";

    // headnames of the columns
    private final String[] COMPONENT_HEADING = new String[] { "ComponentID", "ConfigurationID", "OriginPartID",
            "ArticleNr", "ComponentIDScript", "ComponentLabel", "count" };
    private final String[] ORIGIN_PART_HEADING = new String[] { "OriginPartID", "ConfigurationID", "ArticleNr",
            "ComponentIDScript", "ComponentLabel" };
    private final String[] PARAMETER_HEADING = new String[] { "ParameterID", "ComponentID", "OriginPartID", "KeyScript",
            "ParameterLabel", "ParameterLabelType", "ParameterValue", "ParameterValueLabel" };
    private final String[] CONFIGURATION_HEADING = new String[] { "ConfigurationID", "Model", "Link", "Thumbnail",
            "ConfigurationDate" };

    // names of the output files
    private final String COMPONENT_CSV = "Component.csv";
    private final String ORIGINPARTS_CSV = "Originparts.csv";
    private final String PARAMETER_CSV = "Parameter.csv";
    private final String CONFIGURATION_CSV = "Configuration.csv";

    public JSONParser() {
        componentLines = new ArrayList<>();
        componentLines.add(COMPONENT_HEADING);
        originpartLines = new ArrayList<>();
        originpartLines.add(ORIGIN_PART_HEADING);
        parameterLines = new ArrayList<>();
        parameterLines.add(PARAMETER_HEADING);
        configurationLines = new ArrayList<>();
        configurationLines.add(CONFIGURATION_HEADING);

        componentID = 0;
        configurationID = 0;
        originPartID = 0;
        parameterID = 0;
    }

    public void parseConfiguration(String json, String link, String thumbnail, String configurationDate)
            throws JSONException, IOException {
        isInnerComponent = false;
        JSONObject obj = new JSONObject(new JSONTokener(json));
        parseMainComponent(obj);
        String model = link.substring(link.indexOf("?id=") + 4);
        model = model.substring(0, model.lastIndexOf(":"));
        configurationLines
                .add(new String[] { Integer.toString(configurationID), model, link, thumbnail, configurationDate });
        configurationID++;
    }

    public void parseMainComponent(JSONObject mainComponent) {
        if (isInnerComponent) {
            parseFullList(mainComponent.getJSONArray(FULL_LIST));
        } else {
            isInnerComponent = true;
        }
        parseOriginPart(mainComponent.getJSONObject(ORIGIN_PART));
        JSONArray perMainComponent = mainComponent.optJSONArray(PER_MAIN_COMPONENT);
        if (perMainComponent == null) {
            return;
        }
        for (int i = 0; i < perMainComponent.length(); i++) {
            parseMainComponent(perMainComponent.getJSONObject(i));
        }
    }

    public void parseFullList(JSONArray fullList) {
        for (int i = 0; i < fullList.length(); i++) {
            JSONObject item = fullList.getJSONObject(i);
            componentLines.add(new String[] { Integer.toString(componentID), Integer.toString(configurationID),
                    Integer.toString(originPartID), item.optString(ARTICLE_NO), item.optString(COMPONENT_ID),
                    item.optString(LABEL), item.optString(COUNT) });
            parseParameters(item.getJSONArray(PARAMETER), Integer.toString(componentID), EMPTY);
            componentID++;
        }
    }

    public void parseOriginPart(JSONObject origin) {
        originpartLines.add(new String[] { Integer.toString(originPartID), Integer.toString(configurationID),
                origin.optString(ARTICLE_NO), origin.optString(COMPONENT_ID), origin.optString(LABEL) });
        JSONArray parameterList = origin.getJSONArray(PARAMETER);
        parseParameters(parameterList, EMPTY, Integer.toString(originPartID));
        originPartID++;
    }

    public void parseParameters(JSONArray parameters, String component, String origin) {
        for (int i = 0; i < parameters.length(); i++) {
            JSONObject temp = parameters.getJSONObject(i);
            parameterLines.add(new String[] { Integer.toString(parameterID), component, origin, temp.optString(KEY),
                    temp.optString(LABEL), temp.optString(TYPE), temp.optString(VALUE),
                    temp.optString(VALUE_LABEL) });
            parameterID++;
        }
    }

    public void writeToCSV(String path) throws IOException {
        printToCSV(componentLines, path + COMPONENT_CSV);
        printToCSV(originpartLines, path + ORIGINPARTS_CSV);
        printToCSV(parameterLines, path + PARAMETER_CSV);
        printToCSV(configurationLines, path + CONFIGURATION_CSV);
    }

    public static String convertToCSV(String[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].replaceAll(",", "");
        }
        return String.join(",", data).replaceAll("\r", "").replaceAll("\n", "");
    }

    public static void printToCSV(List<String[]> dataLines, String path) throws IOException {
        File csvOutputFile = new File(path);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (String[] data : dataLines) {
                pw.println(convertToCSV(data));
            }
        }
    }
}