/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package summarizer;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static summarizer.KeywordsGuesser.guessFromString;

/**
 *
 * @author lederp
 */
public class Summarizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, BoilerpipeProcessingException {
        // TODO code application logic here
        String fileName = "Algorithms.pdf";  // Input fileName 
        int noLines = 50; // output percentage  
        boolean pdfB = false; //If the input is pdf set to true else false
        boolean urlB = true;
        boolean newLine = false; // if we need to chunk it based on new line.
        //URL url1 = new URL("http://en.wikipedia.org/wiki/Paulo_Coelho");
        URL url1 = new URL("http://timesofindia.indiatimes.com/india/Obama-ends-Republic-Day-with-4-billion-pledge/articleshow/46022759.cms?utm_source=facebook.com&utm_medium=referral&utm_campaign=TOI");
        int pdfStartPage = 14; // Starting page of the pdf
        int pdfEndPage = 16;   // till Page of extraction 
        
        List<String> allStrings;
        int threshold = 10;
        BufferedReader in;
        String s = "";

        //*************************File Name as Input**********************//
        if (pdfB) {
            pdfReader pd = new pdfReader();
            allStrings = pd.parsePdf(fileName, pdfStartPage, pdfEndPage, newLine);
        } else if (urlB) {
            
            String boilerExtractor = ArticleExtractor.INSTANCE.getText(url1);

            String regex = "\\[\\w+\\]";
            boilerExtractor = boilerExtractor.replaceAll(regex, "");
            String[] tokenizedStrings;
            if(newLine){
                tokenizedStrings = boilerExtractor.split("\\.");
                tokenizedStrings = boilerExtractor.split("\\r?\\n");
            }    
            else
                tokenizedStrings = boilerExtractor.split("\\.");
            
            allStrings = new ArrayList<String>();
            System.out.println("*** Extracted text");
//            System.out.println(boilerExtractor);
            for (String x : tokenizedStrings) {
                if (x.compareTo("") != 0) {
                    System.out.println(x);
                    allStrings.add(x);
                }
            }

            System.out.println("\n\n **Ended");

        } else {
            in = new BufferedReader(new FileReader(fileName));
            allStrings = new ArrayList<String>();

            while ((s = in.readLine()) != null) {
                if (s.compareTo("") != 0) {
                    allStrings.add(s);
                }
            }
        }

        //*************************Finding Cosine Similary Graph**********************//
        CosineSimilarity cs = new CosineSimilarity();

        PrintWriter writer = new PrintWriter("graph.wpairs", "UTF-8");
        List<String> tempStrings = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        
        for(String str: allStrings){
            if(str.length() >5){
                str = str.replaceAll("[\\p{Punct}&&[^'-.]]+", " ");
                tempStrings.add(str);
                sb.append(" "+str);
            }
        }
        List<KeywordsGuesser.Keyword> xyz = guessFromString(sb.toString());
        for (KeywordsGuesser.Keyword x : xyz) {
            System.out.println(x.terms + "\t" + x.frequency);
        }
        allStrings = tempStrings;
        double[][] similarity = new double[allStrings.size()][allStrings.size()];
        for (int i = 0; i < (allStrings.size()); i++) {
            for (int j = i; j < (allStrings.size()); j++) {
                double sim = cs.CosineSimilarity_Score(allStrings.get(i), allStrings.get(j));
                double si = sim * 100;
                int num = (int) si;
//                System.out.println("num = "+ num+ "  sim "+sim);
                if ((i != j) && (sim != 0) && (!Double.isNaN(sim)) && (sim >= (threshold / 100)) && (num > 0)) {
                    writer.println(i + " " + j + " " + num);
                    similarity[i][j] = sim;
                } else {
                    if (i == j) {
                        similarity[i][j] = 1.00;
                    } else {
                        similarity[i][j] = 0.00;
                    }
                }
            }
        }
        writer.close();
        noLines = (int) Math.floor(allStrings.size() * (float) noLines / 100);
        //*************************Finding communities**********************//
        File f = new File("FastCommunity_wMH");
        //./FastCommunity_wMH -f current.wpairs -l firstRun
        if (f.exists() && !f.isDirectory()) {
            Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l firstRun");
            process.waitFor();

        }
        int i = 0;
        List<List<Integer>> modules = new ArrayList<List<Integer>>();
        int selectedIndex = 0;
        int minModuleSize = 9999;
        for (int k = 1; k < allStrings.size(); k++) {
            if (f.exists() && !f.isDirectory()) {
                Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l secondRun -c " + k);
                process.waitFor();
            }

            BufferedReader in1 = new BufferedReader(new FileReader("graph-fc_secondRun.groups"));

            List<Integer> tempList = new ArrayList<Integer>();
            s = in1.readLine();
            while ((s = in1.readLine()) != null) {
                if (s.compareTo("") != 0) {
                    if ((s.charAt(0) == 'G')) {
                        modules.add(tempList);
                        tempList = new ArrayList<Integer>();
                    } else {
                        tempList.add(Integer.valueOf(s));
                    }
                }
            }
            modules.add(tempList);
            //System.out.println(k+ "-> Size of modules is "+modules.size() + " Minmodulesize = " + minModuleSize + " no Lines = "+ noLines + " selectedIndex " + selectedIndex);
            if (Math.abs(modules.size() - noLines) < minModuleSize) {
                minModuleSize = Math.abs(modules.size() - noLines);
                selectedIndex = k;
            }
            modules = new ArrayList<List<Integer>>();
        }

        System.out.println("no Lines = " + noLines + " minModuleSize " + minModuleSize + " selectedIndex " + selectedIndex);

        if (f.exists() && !f.isDirectory()) {
            Process process = Runtime.getRuntime().exec("./FastCommunity_wMH -f graph.wpairs -l secondRun -c " + selectedIndex);
            process.waitFor();
        }

        BufferedReader in1 = new BufferedReader(new FileReader("graph-fc_secondRun.groups"));

        List<Integer> tempList = new ArrayList<Integer>();
        s = in1.readLine();
        while ((s = in1.readLine()) != null) {
            if (s.compareTo("") != 0) {
                if ((s.charAt(0) == 'G')) {
                    modules.add(tempList);
                    tempList = new ArrayList<Integer>();
                } else {
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
                if (similarity[i][j] > threshold / 100) {
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
        for (List<Integer> tempL : modules) {
            selected[i] = tempL.get(0);
            double x = sum[tempL.get(0)] / degree[tempL.get(0)];
            for (Integer y : tempL) {
                if ((sum[y] / degree[y]) > x) {
                    x = (sum[y] / degree[y]);
                    selected[i] = y;
                }
            }
            i++;
        }
        Arrays.sort(selected);
        for (i = 0; i < selected.length; i++) {
            System.out.println(selected[i] + " -> " + allStrings.get(selected[i]));
        }

    }

}
