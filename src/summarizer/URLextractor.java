/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package summarizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import org.jsoup.select.Elements;

/**
 *
 * @author lederp
 */
public class URLextractor {

    
    List<String> textFromURL(URL u){
        Document doc;
        List<String> outputStrings = new ArrayList<String>();
        try {

            // need http protocol
            String text = Jsoup.parse(u, 10000).text();
            System.out.println(text);

            String[] tokenizedStrings = text.split("\\.");
            for (String x : tokenizedStrings) {
                if (x.compareTo("") != 0) {
                    outputStrings.add(x);
                }
            }
            // get page title
//            doc = Jsoup.connect("http://timesofindia.indiatimes.com/india/Modis-drug-speech-puts-Punjab-government-in-a-spot/articleshow/45516872.cms").get();
//            String title = doc.title();
//            System.out.println("title : " + title);
//
//            // get all links
//            Elements links = doc.select("a[href]");
//            
//            for (Element link : links) {
//
//                // get the value from href attribute
//                System.out.println("\nlink : " + link.attr("href"));
//                System.out.println("text : " + link.text());
//
//            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return outputStrings;
    }
    
}




