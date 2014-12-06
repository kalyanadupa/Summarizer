/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package summarizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lederp
 */
public class Summarizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        // TODO code application logic here
        String fileName = "test.txt";  // Input fileName 
        int threshold = 10; // Input percentage threshold 
       
        //*************************File Name as Input**********************//
        
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        //*************************Finding Cosine Similary Graph**********************//
        CosineSimilarity cs = new CosineSimilarity();
        List<String> allStrings = new ArrayList<String>();
        String s = "";
        while ((s = in.readLine()) != null) {
            if (s.compareTo("") != 0) {
                allStrings.add(s);
                System.out.println(s);
            }
        }
        PrintWriter writer = new PrintWriter("graph.wpairs", "UTF-8");
        double[][] similarity =  new double[allStrings.size()][allStrings.size()];
        for (int i = 0; i < (allStrings.size()); i++) {
            for (int j = i; j < (allStrings.size()); j++) {
                double sim = cs.CosineSimilarity_Score(allStrings.get(i), allStrings.get(j));
                double si = sim *100;
                int num = (int) si;
                System.out.println("num = "+ num+ "  sim "+sim);
                if ((i != j) && (sim != 0) && (!Double.isNaN(sim)) && (sim >= (threshold/100)) && (num > 0)) {
                    writer.println(i+" "+j+" "+ num);
                    similarity[i][j] = sim;
                }
                else{
                    similarity[i][j] = 0.00;
                }    
            }
        }
        writer.close();
        //*************************Finding communities**********************//
        File f = new File("FastCommunity_wMH");
        //./FastCommunity_wMH -f current.wpairs -l firstRun
        if (f.exists() && !f.isDirectory()) {
            Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l firstRun");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line.toString());
            }
        }
        if (f.exists() && !f.isDirectory()) {
            Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l secondRun -c 30");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line.toString());
            }
        }
        List<List<Integer>> modules = new ArrayList<List<Integer>>();
        BufferedReader in1 = new BufferedReader(new FileReader("graph-fc_secondRun.groups"));
        int i = 0;
        List<Integer> tempList = new ArrayList<Integer>();
        while ((s = in1.readLine()) != null) {    
            if (s.compareTo("") != 0) {
                if((s.charAt(0) == 'G') && (modules.size() != 0)){
                    modules.add(tempList);
                    tempList = new ArrayList<Integer>();
                }
                else{
                    tempList.add(Integer.valueOf(s));
                }
            }
        }
        modules.add(tempList);
        //*************************Build Lex Rank **********************//
        
    }
    
}
