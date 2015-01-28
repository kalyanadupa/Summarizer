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


/**
 *
 * @author lederp
 */
public class URLextractor {

    
    List<String> textFromURL(URL u){
        
        List<String> outputStrings = new ArrayList<String>();
        String text = "";
        System.out.println(text);
        String[] tokenizedStrings = text.split("\\.");
        for (String x : tokenizedStrings) {
            if (x.compareTo("") != 0) {
                outputStrings.add(x);
            }
        }
        return outputStrings;
    }
    
}




