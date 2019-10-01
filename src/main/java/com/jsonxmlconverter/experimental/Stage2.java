package com.jsonxmlconverter.experimental;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stage2 {
    public static void main(String[] args) {
        Stage2 main = new Stage2();

        main.readFromFile();
    }

    private void readFromFile() {
        File sourceFile = new File("test.txt");
//        File sourceFile = new File("C:\\Users\\kthor\\misc\\NewScraper\\src\\main\\resources\\test.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            String line;

            List<StringBuilder> rawJsonObjects = new ArrayList<>();
            int currentJsonObject = 0;

            String endOfJsonObject = "^}$";
            Pattern jsonTerminal = Pattern.compile(endOfJsonObject);


            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '<') {
                    System.out.println(xmlToJson(line));
                } else if (line.charAt(0) == '{') {
                    rawJsonObjects.add(new StringBuilder());
                    rawJsonObjects.get(currentJsonObject).append(line);
                } else {
                    Matcher jsonTerminalMatcher = jsonTerminal.matcher(line);
                    if (!jsonTerminalMatcher.find()) {
                        rawJsonObjects.get(currentJsonObject).append(line);
                    } else {
                        rawJsonObjects.get(currentJsonObject).append(line);
                        System.out.println(jsonToXml(String.valueOf(rawJsonObjects.get(currentJsonObject))));
                        currentJsonObject++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String xmlToJson(String input) {
        String getXml1 = "(<((\\w*)\\s((\\w*)\\s=\\s\"(\\w*)\")\\s?)*>)(.*)(</(.*)>)";
        Pattern xmlRegex1 = Pattern.compile(getXml1);
        Matcher matcher1 = xmlRegex1.matcher(input);

        String getXml2 = "<(\\w*)\\s(((\\w*)\\s=\\s)(\"(\\w*)\")\\s((\\w*)\\s=\\s)(\"(\\w*)\")\\s?)*\\s?/>";
        Pattern xmlRegex2 = Pattern.compile(getXml2);
        Matcher matcher2 = xmlRegex2.matcher(input);

        String getXml3 = "<(\\w*)\\s(\\w*)\\s=\\s\"(\\w*)\">(\\w*)</(\\w*)>";
        Pattern xmlRegex3 = Pattern.compile(getXml3);
        Matcher matcher3 = xmlRegex3.matcher(input);

        if (matcher1.find()) {
            return "{\n\t\"" + matcher1.group(3) + "\" : {\n\t\t\"@" + matcher1.group(5) + "\" : \"" + matcher1.group(6) + "\",\n\t\t\"#" + matcher1.group(9) + "\" : \"" + matcher1.group(7) + "\"\n\t}\n}";
        } else if (matcher2.find()) {
            return "{\n\t\"" + matcher2.group(1) + "\" : {\n\t\t\"@" + matcher2.group(4) + "\" : \"" + matcher2.group(6) + "\",\n\t\t\"@" + matcher2.group(8) + "\" : \"" + matcher2.group(10) + "\",\n\t\t\"#" + matcher2.group(1) + "\" : null\n\t}\n}";
        } else if (matcher3.find()) {
            return "{\n\t\"" + matcher3.group(1) + "\" : {\n\t\t\"@" + matcher3.group(2) + "\" : " + matcher3.group(3) + ",\n\t\t\"#" + matcher3.group(1) + "\" : \"" + matcher3.group(4) + "\n\t}\n}";
        } else {
            return null;
        }
    }

    private static String jsonToXml(String input) {
        String getJson1 = "\\{\\s*\"(\\w*)\"\\s*:\\s*\\{\\s*\"@(\\w*)\"\\s*:\\s*\"(\\w*)\",\\s*\"#(\\w*)\"\\s*:\\s*\"(\\w*\\s*\\w*)\"\\s*}\\s*}";
        Pattern jsonRegex1 = Pattern.compile(getJson1, Pattern.MULTILINE);
        Matcher matcher1 = jsonRegex1.matcher(input);

        String getJson2 = "\\{\\s*\"(\\w*)\"\\s*:\\s*\\{\\s*\"@(\\w*)\"\\s*:\\s*(\"(\\w*)\"|\\d),\\s*\"@(\\w*)\"\\s*:\\s*\"(\\w*)\",\\s*\"#(\\w*)\"\\s*:\\s*null\\s*}\\s*}";
        Pattern jsonRegex2 = Pattern.compile(getJson2, Pattern.MULTILINE);
        Matcher matcher2 = jsonRegex2.matcher(input);

        String getJson3 = "\\{\\s*\"(\\w*)\"\\s:\\s*\\{\\s*\"@(\\w*)\"\\s*:\\s*(\\w*),\\s*\"#(\\w*)\"\\s*:\\s*(\\w*)\\s*}\\s*}";
        Pattern jsonRegex3 = Pattern.compile(getJson3, Pattern.MULTILINE);
        Matcher matcher3 = jsonRegex3.matcher(input);

        if (matcher1.find()) {
            return "<" + matcher1.group(1) + " " + matcher1.group(2) + " = \"" + matcher1.group(3) + "\">" + matcher1.group(5) + "</" + matcher1.group(4) + ">";
        } else if (matcher2.find()) {
            return "<" + matcher2.group(1) + " " + matcher2.group(2) + " = \"" + matcher2.group(3) + "\"" + " " + matcher2.group(5) + " = \"" + matcher2.group(6) + "\"" + " />";
        } else if (matcher3.find()) {
            return "<" + matcher3.group(1) + " " + matcher3.group(2) + " = \"" + matcher3.group(3) + "\">" + matcher3.group(5) + "</" + matcher3.group(1) + ">";
        } else {
            return null;
        }
    }
}
