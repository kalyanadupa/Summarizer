/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package summarizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.*;

/**
 *
 * @author lederp
 */


public class pdfReader {

    
    
    List<String> parsePdf(String filePath,int startPage, int endPage){
        PDDocument pd;
        BufferedWriter wr;
        List<String> outputStrings = new ArrayList<String>();
        try {
            File input = new File(filePath);  // The PDF file from where you would like to extract
            File output = new File("SampleText.txt"); // The text file where you are going to store the extracted data
            pd = PDDocument.load(input);
            System.out.println(pd.getNumberOfPages());
            if(pd.isEncrypted()){
                System.out.println("Error PDF is encrypted, cannot Parse"); 
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage); //Start extracting from page 14
            stripper.setEndPage(endPage); //Extract till page 16
            wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            stripper.writeText(pd, wr);
            if (pd != null) {
                pd.close();
            }
            wr.close();
            BufferedReader in = new BufferedReader(new FileReader("SampleText.txt"));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = in.readLine()) != null) {
                sb.append(" ");
                sb.append(s);
            }
            s = sb.toString();
            String[] tokenizedStrings = s.split("\\.");
            for (String x : tokenizedStrings) {
                if(x.compareTo("") != 0)
                    outputStrings.add(x);
            }
            //System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStrings;
    }
}
