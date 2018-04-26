package com.ugoodtech.umi.core;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CodeTest {
    public static void main(String[] args) throws IOException {
//        parseCountry();
        String filepath = "/Users/stone/Documents/projects/company/backend/UMI/doc/country_dailing_number.json";
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(new FileReader(new File(filepath)), Map.class);
        List<Map> list = (List) map.get("Country");
        StringBuilder builder = new StringBuilder();
        for (Map map1 : list) {
            Object dialingCode = map1.get("DialingCode");
            if (dialingCode != null) {
                builder.append("(\"" + map1.get("NumberCode") + "\",\"" + dialingCode + "\"),\n");
            }
        }
        System.out.println("builder = " + builder);
    }

    private static void parseCountry() {
        Path path = Paths.get("/Users/stone/Documents/projects/company/backend/UMI/doc/country.txt");
        StringBuilder builder = new StringBuilder();
        try (Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {

            lines.forEachOrdered(line -> {
                System.out.println("line = " + line);
                if (line.length() > 2) {
                    String[] keys = line.split("\\t");
                    builder.append("(" + keys[0] + ",\"" + keys[1] + "\",\"" + keys[2] + "\",\"" + keys[3] + "\",\""
                            + keys[4] + "\",\"" + keys[5] + "\",\"" + keys[6] + "\"),\n");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("builder = " + builder);
    }
}
