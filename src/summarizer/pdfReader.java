/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package summarizer;

import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.*;

/**
 *
 * @author lederp
 */


public class pdfReader {

    public static void main(String[] args) {
        PDDocument pd;
        BufferedWriter wr;
        try {
            File input = new File("Algorithms.pdf");  // The PDF file from where you would like to extract
            File output = new File("SampleText.txt"); // The text file where you are going to store the extracted data
            pd = PDDocument.load(input);
            System.out.println(pd.getNumberOfPages());
            System.out.println(pd.isEncrypted());
            pd.save("CopyOfInvoice.pdf"); // Creates a copy called "CopyOfInvoice.pdf"
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(14); //Start extracting from page 3
            stripper.setEndPage(16); //Extract till page 5
            wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            stripper.writeText(pd, wr);
            if (pd != null) {
                pd.close();
            }
            // I use close() to flush the stream.
            wr.close();
            BufferedReader in = new BufferedReader(new FileReader("SampleText.txt"));
            String s;
            while((s = in.readLine()) != null){
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
