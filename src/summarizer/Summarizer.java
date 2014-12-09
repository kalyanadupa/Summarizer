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
import java.util.Arrays;
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
//                System.out.println(s);
            }
        }
        PrintWriter writer = new PrintWriter("graph.wpairs", "UTF-8");
        double[][] similarity =  new double[allStrings.size()][allStrings.size()];
        for (int i = 0; i < (allStrings.size()); i++) {
            for (int j = i; j < (allStrings.size()); j++) {
                double sim = cs.CosineSimilarity_Score(allStrings.get(i), allStrings.get(j));
                double si = sim *100;
                int num = (int) si;
//                System.out.println("num = "+ num+ "  sim "+sim);
                if ((i != j) && (sim != 0) && (!Double.isNaN(sim)) && (sim >= (threshold/100)) && (num > 0)) {
                    writer.println(i+" "+j+" "+ num);
                    similarity[i][j] = sim;
                }
                else{
                    if (i == j) {
                        similarity[i][j] = 1.00;
                    } else {
                        similarity[i][j] = 0.00;
                    }
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
//                System.out.println(line.toString());
            }
        }
        if (f.exists() && !f.isDirectory()) {
            Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l secondRun -c 6");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println(line.toString());
            }
        }
        List<List<Integer>> modules = new ArrayList<List<Integer>>();
        BufferedReader in1 = new BufferedReader(new FileReader("graph-fc_secondRun.groups"));
        int i = 0;
        List<Integer> tempList = new ArrayList<Integer>();
        s = in1.readLine();
        while ((s = in1.readLine()) != null) {    
            if (s.compareTo("") != 0) {
                if((s.charAt(0) == 'G') ){
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
        /*
        for(i = 0;i < allStrings.size();i++)
            for(int j = 0;j < allStrings.size();j++) 
                System.out.println(similarity[i][j]);
        */
        List<DummyItem> items = new ArrayList<DummyItem>();
        for (i = 0; i < similarity.length; ++i) {
            items.add(new DummyItem(i, similarity));
        }
        LexRankResults<DummyItem> results = LexRanker.rank(items, 0.2, true);
        String[] names = {"d1s1", "d2s1", "d2s2", "d2s3", "d3s1", "d3s2",
            "d3s3", "d4s1", "d5s1", "d5s2", "d5s3"};
        double max = results.scores.get(results.rankedResults.get(0));
//        System.out.println("Max is "+max + "similarity "+ similarity.length);
        /*
        for (i = 0; i < similarity.length; ++i) {
            
            System.out.println(i + ": "
                    + (results.scores.get(items.get(i)) / max));
        }   
        */
        //*************************Another Algo for Lex Rank **********************//
        
        double[] sum = new double[similarity.length]; 
        int[] degree = new int[similarity.length];

        for (i = 0; i < similarity.length; i++) {
            sum[i] = 0;
            for (int j = 1; j < similarity.length; j++) {
                if (similarity[i][j] > threshold/100) {
                    sum[i] = similarity[i][j] + sum[i];
                    degree[i]++;
                }
            }
        }/*
        System.out.println("** The ranking is ** ");
        for (i = 0; i < similarity.length; i++) {
            System.out.println("for " + i + ": " + sum[i] / degree[i] + "  sum : " + sum[i] + " degree : " + degree[i]);
        }
        */
        //*************************Generating Output **********************//
        int[] selected = new int[modules.size()];
        i = 0;
        for(List<Integer> tempL : modules){
            selected[i] = tempL.get(0);
            double x = sum[tempL.get(0)]/degree[tempL.get(0)];
            for(Integer y : tempL){
                if((sum[y]/degree[y]) > x){
                    x = (sum[y]/degree[y]);
                    selected[i] = y;
                }
            }
            i++;
        }
        Arrays.sort(selected);
        for(i = 0;i < selected.length;i++){
            System.out.println(allStrings.get(i));
        }
        
    }
    
}
