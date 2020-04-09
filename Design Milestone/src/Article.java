import edu.stanford.nlp.pipeline.CoreDocument;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An article with its metrics. Can compute its own metrics based on the document passed in.
 */
public class Article implements Serializable {
    private String source, title, author, pov;
    private int sentenceCount, positiveCount, neutralCount, negativeCount, wordCount, characterCount,
            verbCount, nounCount, adjectiveCount, adverbCount, prepositionCount, interjectionCount,
            syllableCount;
    private float readingLevel, vocabularyDensity;
    HashMap<String, Integer> mostWords, mostNames, mostPlaces, mostOrganizations;
    Date date;

    public Article(String source, String title, String author, String date, CoreDocument document) {
        this.source = source;
        this.title = title;
        this.author = author;
//        this.date = date;
        sentenceCount = document.sentences().size();
        wordCount = document.tokens().size();
        characterCount = characterCount(document);
        sentimentCount(document);

        //Parts of Speech counts
        HashMap<String, Integer> posMap = makePOSMap(document);
        String[] verbCodes = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
        String[] nounCodes = {"NN", "NNS", "NNP", "NNPS"};
        String[] adjectiveCodes = {"JJ", "JJR", "JJS"};
        String[] adverbCodes = {"RB", "RBR", "RBS"};
        verbCount = posCounter(posMap, verbCodes);
        nounCount = posCounter(posMap, nounCodes);
        adjectiveCount = posCounter(posMap, adjectiveCodes);
        adverbCount = posCounter(posMap, adverbCodes);
        interjectionCount = posMap.getOrDefault("UH", 0);
        prepositionCount = posMap.getOrDefault("IN", 0);



    }


    /**
     * Goes through each word and catalogs the part of speech of each word
     * @param document annotated document
     * @return Hashmap containing counts of up to 36 different parts of speech labels
     */
    private HashMap makePOSMap(CoreDocument document) {
        HashMap<String, Integer> posMap = new HashMap<>();
        int count = 0;
        String tag;
        for (int i = 0; i < document.tokens().size(); i++) {
            tag = document.tokens().get(i).tag();
            if (posMap.containsKey(tag)){
                count = (int) posMap.get(tag) + 1;
                posMap.put(tag, count);
            }
            else {
                posMap.put(document.tokens().get(i).tag(), 0);
            }

        }
        return posMap;
    }

    /**
     * Sums the counts of a collection of part of speech codes.
     * @param posMap HashMap of parts of speech counts
     * @param posCodes Array of parts of speech codes that you want counted
     * @return sum of the counts of the specified codes
     */
    private int posCounter(HashMap<String, Integer> posMap, String[] posCodes) {
        int count = 0;
        for (String code : posCodes) {
            if (posMap.containsKey(code)) count += (int) posMap.get(code);
        }
        return count;
    }
    /**
     * Counts the number of [a-zA-Z] characters in article
     * @param document annotated article
     * @return character count
     */
    private int characterCount(CoreDocument document) {
        Pattern p = Pattern.compile("(\\w)");
        Matcher match = p.matcher(document.text());
        int counter = 0;

        while (match.find()){
            if (match.group().length() != 0) counter++;
        }
        return counter;
    }

    /**
     * Counts the number of sentences of a given sentiment
     * @param document annotated article
     * @return sentiment count
     */
    private void sentimentCount(CoreDocument document) {
        positiveCount = 0;
        neutralCount = 0;
        negativeCount = 0;
        for (int i = 0; i < document.sentences().size(); i++) {
            try {
                String sent = document.sentences().get(i).sentiment();
                if (sent.equals("Positive")) positiveCount++;
                else if (sent.equals("Neutral")) neutralCount++;
                else if (sent.equals("Negative")) negativeCount++;
            } catch (Exception e) {
                System.out.println("could not get sentiment");
            }
        }
    }


    /**
     * Getter
     * @return News Source
     */
    public String getSource() {
        return source;
    }

    /**
     *
     * @return Title of Article
     */
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public int getVerbCount() {
        return verbCount;
    }

    public int getNounCount() {
        return nounCount;
    }

    public int getAdjectiveCount() {
        return adjectiveCount;
    }

    public int getAdverbCount() {
        return adverbCount;
    }

    public int getPrepositionCount() {
        return prepositionCount;
    }

    public int getInterjectionCount() {
        return interjectionCount;
    }
}
