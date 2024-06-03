package org.example.lexical;

import java.io.InputStream;
import java.util.*;

public class Java_LexAnalysis
{
    private static StringBuffer prog = new StringBuffer();
    private static Map<String,Integer> map = new HashMap<>();
    private static List<String> tokens = new ArrayList<>();

    /**
     * This method is to read the standard input
     */
    private static void read_prog()
    {
        // Scanner sc = new Scanner(System.in);
        // while(sc.hasNextLine())
        // {
        //     prog.append(sc.nextLine());
        // }
        prog.append("printf(\"HelloWorld!\")");
    }

    // add your method here!!
    /**
     * This method is to read the c_keys.txt file in the same directory
     */
    private static void read_c_keys()
    {
        // Read file
        InputStream inputStream = Java_LexAnalysis.class.getResourceAsStream("/org/example/lexical/c_keys.txt");
        if (inputStream == null) {
            System.err.println("File not found: c_keys.txt");
            return;
        }

        Scanner sc = new Scanner(inputStream);
        while(sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] words = line.split(" {4}");
            // 如果words[0]包含中文字符，跳过
            if (words[0].matches(".*[\\u4E00-\\u9FA5]+.*")) {
                continue;
            }
            map.put(words[0], Integer.parseInt(words[1]));
        }
        map.put("/*",79);
        map.put("*/",79);
        sc.close();
        System.out.println("Loaded " + map.size() + " keys from c_keys.txt");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    /**
     * This method is to analyze the program
     */
    private static void analyzeProg()
    {
        int currentIndex = 0;
        int n = prog.length();
        String stack = "";
        while (currentIndex < n) {
            char c = prog.charAt(currentIndex);
            //
            currentIndex++;
        }
    }



    /**
     * You should add some code in this method to achieve this lab
     */
    private static void analysis()
    {
        read_prog();
        read_c_keys();
    }

    /**
     * This is the main method
     * @param args
     */
    public static void main(String[] args) {
        analysis();
    }
}