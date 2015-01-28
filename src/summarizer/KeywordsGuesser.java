/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package summarizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.apache.lucene.util.Version;


/**
 *
 * @author lederp
 */
public class KeywordsGuesser {

    private static Version LUCENE_VERSION = Version.LUCENE_4_10_3;

    public static class Keyword implements Comparable<Keyword> {

        private String stem;
        private Integer frequency;
        private Set<String> terms;

        public Keyword(String stem) {
            this.stem = stem;
            terms = new HashSet<String>();
            frequency = 0;
        }

        private void add(String term) {
            terms.add(term);
            frequency++;
        }

        public String getStem() {
            return stem;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public Set<String> getTerms() {
            return terms;
        }

        @Override
        public int compareTo(Keyword o) {
            return o.frequency.compareTo(frequency);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Keyword && obj.hashCode() == hashCode();
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{stem});
        }

        @Override
        public String toString() {
            return stem + " x" + frequency;
        }

    }

    private static String stemmize(String term) throws IOException {

        TokenStream tokenStream = new ClassicTokenizer(LUCENE_VERSION, new StringReader(term));

        tokenStream = new PorterStemFilter(tokenStream);

        Set<String> stems = new HashSet<String>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {

            stems.add(token.toString());
        }

        if (stems.size() != 1) {
            return null;
        }

        String stem = stems.iterator().next();

        if (!stem.matches("[\\w-]+")) {
            return null;
        }

        return stem;
    }

    private static <T> T find(Collection<T> collection, T example) {
        for (T element : collection) {
            if (element.equals(example)) {
                return element;
            }
        }
        collection.add(example);
        return example;
    }

    public static List<Keyword> guessFromUrl(String url) throws IOException {
        String content = "Java is a general-purpose computer programming language that is concurrent, class-based, object-oriented,[11] and specifically designed to have as few implementation dependencies as possible. It is intended to let application developers \"write once, run anywhere\" (WORA),[12] meaning that code that runs on one platform does not need to be recompiled to run on another.[13] Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM) regardless of computer architecture. Java is, as of 2015, one of the most popular programming languages in use, particularly for client-server web applications, with a reported 9 million developers.[14][15] Java was originally developed by James Gosling at Sun Microsystems (which has since merged into Oracle Corporation) and released in 1995 as a core component of Sun Microsystems' Java platform. The language derives much of its syntax from C and C++, but it has fewer low-level facilities than either of them.\n";

        return guessFromString(content);
    }

    public static List<Keyword> guessFromString(String input) throws IOException {

        input = input.replaceAll("-+", "-0");
        input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
        input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");
        TokenStream tokenStream = new ClassicTokenizer(LUCENE_VERSION, new StringReader(input));
        tokenStream = new LowerCaseFilter(LUCENE_VERSION, tokenStream);
        tokenStream = new ClassicFilter(tokenStream);
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        tokenStream = new StopFilter(LUCENE_VERSION, tokenStream, EnglishAnalyzer.getDefaultStopSet());
        List<Keyword> keywords = new LinkedList<Keyword>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = token.toString();
            String stem = stemmize(term);
            if (stem != null) {
                Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
                keyword.add(term.replaceAll("-0", "-"));
            }
        }
        Collections.sort(keywords);
        return keywords;
    }

    public static void main(String[] args) throws IOException {
        List<Keyword> xyz = guessFromUrl("https://en.wikipedia.org/wiki/Java_%28programming_language%29");
        for (Keyword x : xyz) {
            System.out.println(x.terms + "\t" + x.frequency);
        }
        String abc = "hey. how are you\".doing tonight! \n when we meet?";
        abc = abc.replaceAll("[\\p{Punct}&&[^'-.]]+", " ");
        System.out.println(abc);
    }

}
