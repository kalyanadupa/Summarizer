/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package summarizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import static org.apache.lucene.util.Constants.LUCENE_VERSION;
import org.apache.lucene.util.Version;

/**
 *
 * @author lederp
 */
public class test {
    
    public static String removeComments(String code) {
        StringBuilder newCode = new StringBuilder();
        try (StringReader sr = new StringReader(code)) {
            boolean inBlockComment = false;
            boolean inLineComment = false;
            boolean out = true;

            int prev = sr.read();
            int cur;
            for (cur = sr.read(); cur != -1; cur = sr.read()) {
                if (inBlockComment) {
                    if (prev == '*' && cur == '/') {
                        inBlockComment = false;
                        out = false;
                    }
                } else if (inLineComment) {
                    if (cur == '\r') { // start untested block
                        sr.mark(1);
                        int next = sr.read();
                        if (next != '\n') {
                            sr.reset();
                        }
                        inLineComment = false;
                        out = false; // end untested block
                    } else if (cur == '\n') {
                        inLineComment = false;
                        out = false;
                    }
                } else {
                    if (prev == '/' && cur == '*') {
                        sr.mark(1); // start untested block
                        int next = sr.read();
                        if (next != '*') {
                            inBlockComment = true; // tested line (without rest of block)
                        }
                        sr.reset(); // end untested block
                    } else if (prev == '/' && cur == '/') {
                        inLineComment = true;
                    } else if (out) {
                        newCode.append((char) prev);
                    } else {
                        out = true;
                    }
                }
                prev = cur;
            }
            if (prev != -1 && out && !inLineComment) {
                newCode.append((char) prev);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newCode.toString();
    }
    
    public static void main(String[] args){
        String x = "";
        System.out.println(removeComments(x));
        
        
        String abc = "hey. how are you.doing tonight! when we meet?";
        abc = abc.replaceAll("[\\p{Punct}&&[^'-.]]+", " ");
        //System.out.println(abc);
    }
          
    //abc = abc.replaceAll("[^\\\\w\\\\.\\\\s\\\\-]","");
    
}
