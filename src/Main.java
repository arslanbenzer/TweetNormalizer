
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.GenericArrayType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.bson.Document;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.swabunga.spell.engine.*;
import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

class Neighbor
{
	public String id; 
	public String tag;  
	public int position; 
};

public class Main {
	public static final double LAMBDA = 0.5;
	public static final double BETA = 0.5;
	public static final int MULTP = 1;
	static HashSet<String> hs2;
	static HashSet<String> hs;
	static HashMap<String,String> noslang = new HashMap<String,String>();
	static HashMap<String,Double> cndSet;
	static HashMap<String,Integer> nodeFreq= new HashMap<String,Integer>();
	static String [][]taggedTweet;
	static boolean [] ifToNormalize;
	static ArrayList<String> words = new ArrayList<String>();
	static ArrayList<String> answers = new ArrayList<String>();
	static ArrayList<String> answers2 = new ArrayList<String>();
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		Locale locale = new Locale("EN", "GB");
		String line;
		hs2 = new HashSet<String>();
		hs = new HashSet<String>();
		InputStream fis2 = new FileInputStream("libraries/aspell.txt");   //read dictionary
		InputStreamReader isr2 = new InputStreamReader(fis2, Charset.forName("UTF-8"));
		BufferedReader br2 = new BufferedReader(isr2);
		while ((line = br2.readLine()) != null) {
			hs2.add(line.toLowerCase(locale));
		}
		hs2=removeLetters(hs2);
		InputStream fis3 = new FileInputStream("libraries/noslang.txt");   //read dictionary
		InputStreamReader isr3 = new InputStreamReader(fis3, Charset.forName("UTF-8"));
		BufferedReader br3 = new BufferedReader(isr3);
		while ((line = br3.readLine()) != null) {
			line = br3.readLine();
			String [] words = line.split("  ");
			noslang.put(words[0], words[2]);
			hs2.add(line);
		}
		br3.close();
		MongoDB mmm = new MongoDB();
		hs=mmm.getWords(hs2);
		br2.close();
		System.out.println(hs.size());
		//System.out.println(normalize("fucking stalker gettn everything from"));
		String s1 ="soooo";    //im aim i'm   wat wait what     shawty shitty shorty      jus  j's just   boi bi boy
		String s2 ="zoo";  //perfered - performed - preferred  confrims - conforms - confirms
		String s3 ="so";
		System.out.println(s2+": "+simCost(s1,s2)+" - "+LAMBDA*Editex.editDistanceScore(s1,s2)+" lscr: "+1.0*getLongestCommonSubsequence(s1, s2)/Math.max(s1.length(), s2.length()));
		System.out.println(s3+": "+simCost(s1,s3)+" - "+LAMBDA*Editex.editDistanceScore(s1,s3)+" lscr: "+1.0*getLongestCommonSubsequence(s1, s3)/Math.max(s1.length(), s3.length()));
		//testTrigram();         // tam haliyle bi dene skorlarý not et
		testLexNorm();		//aynýsý bunun için de
	}
	private static void testTrigram() throws IOException, ClassNotFoundException{
		String line;
		double corNor =0;
		double nor=0;
		double reqNor=0;
		int cnt=0;
		double precision =0.0;
		double recall =0.0;
		double f_measure= 0.0;
		int margin=1758;
		ArrayList <String> reqqq = new ArrayList<String>();
		InputStream fis = new FileInputStream("libraries/trigram_data/ann4.trigrams.hyp");   //read tweets
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		ArrayList<String> trigram = new ArrayList<String>();
		while ((line = br.readLine()) != null&&cnt<margin) {
			System.out.println(line);
			line=line.replaceAll("<s>", ",,,");
			line= line.replaceAll("</s>", ",,,");
			trigram.add(line);
			cnt++;
		}
		cnt=0;
		InputStream fis2 = new FileInputStream("libraries/trigram_data/ann4.trigrams.ref");   //read normalized forms
		InputStreamReader isr2 = new InputStreamReader(fis2, Charset.forName("UTF-8"));
		BufferedReader br2 = new BufferedReader(isr2);
		ArrayList<String> trigram2 = new ArrayList<String>();
		while ((line = br2.readLine()) != null&&cnt<margin) {
			trigram2.add(line);
			cnt++;
		}
		br.close();
		br2.close();
		
		for (int i = 0; i < margin; i++) {
			String curTweet = trigram.get(i);
			String [] tweet = curTweet.split(" ");

			String [] tweet3 = trigram2.get(i).split(" ");
			ifToNormalize = new boolean [tweet.length];
			for(int j=0;j<tweet.length;j++){
				if(!tweet[j].equals(tweet3[j])&&!tweet[j].equals(",,,")){
					reqqq.add(tweet[j]+" - "+tweet3[j]);
					reqNor++;
					ifToNormalize[j]=true;
				}
				else ifToNormalize[j]=false;
				
			}			
			String result=normalize(curTweet);
			String [] tweet2 = result.split(" ");
			for(int j=0;j<tweet.length;j++){
				if(!tweet[j].equals(tweet2[j])){
					nor++;
					words.add(tweet[j]);
					answers.add(tweet2[j]);
					answers2.add(tweet3[j]);
					if(tweet2[j].equals(tweet3[j])){
						corNor++;
					}
				}
			}			
		}
		for (int j= 0;j<words.size();j++) {
			System.out.println(words.get(j)+" - "+answers.get(j)+" - "+answers2.get(j));
		}
		for (int i = 0; i < reqqq.size(); i++) {
			System.out.println(i+": "+reqqq.get(i));
		}
		System.out.println("nor: "+nor);
		System.out.println("cor nor: "+corNor);
		System.out.println("req nor: "+reqNor);
		precision=corNor/nor;
		recall =corNor/reqNor;
		System.out.println("precision: "+precision);
		System.out.println("recall: "+ recall);
		System.out.println("f-measure: "+2*recall*precision/(recall+precision));
		
	}
	
	private static void testLexNorm() throws NumberFormatException, ClassNotFoundException, IOException {
		String line;
		double corNor =0;
		double nor=0;
		double reqNor =0;
		double precision =0.0;
		double recall =0.0;
		double f_measure= 0.0;
		InputStream fis = new FileInputStream("libraries/lexnorm_data/corpus.v1.2.tweet");   //read dictionary
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		int cnt =0;
		int margin = 100;
		while ((line = br.readLine()) != null) {//&&cnt<margin
				int no=Integer.parseInt(line);
				System.out.println(no);
				String [] tweet = new String [no];
				String data="";
				String [] isoov = new String [no];
				String [] IVforms = new String [no];
				ifToNormalize=new boolean [no];
				for(int i=0;i<no;i++){
					line=br.readLine();
					String [] sss=line.split("\t");
					tweet[i]=sss[0];
					data=data.concat(sss[0]+" ");
					isoov[i]=sss[1];
					if(!sss[0].equals(sss[2])){
						ifToNormalize[i]=true;
						reqNor++;
					} else 
						ifToNormalize[i]=false;
					IVforms[i]=sss[2];
				}
				String [] resu=new String[no];
				String res = normalize(data);
				System.out.println(res);
				System.out.println(cnt);
				resu=res.split(" ");
				for(int i =0;i<no;i++){
					//System.out.println(tweet[i]+" --- "+resu[i]+" --- "+no);
					if(!tweet[i].equals(resu[i])){
						words.add(tweet[i]);
						answers.add(resu[i]);
						answers2.add(IVforms[i]);
						nor++;
						if(resu[i].equals(IVforms[i])){
							corNor++;
						}
					}					
				}
			cnt++;
		}
		for (int j= 0;j<words.size();j++) {
			System.out.println(words.get(j)+" - "+answers.get(j)+" - "+answers2.get(j));
		}
		System.out.println("nor: "+nor);
		System.out.println("cor nor: "+corNor);
		System.out.println("reqnor: "+reqNor);
		precision=corNor/nor;
		recall =corNor/reqNor;
		System.out.println("precision: "+precision);
		System.out.println("recall: "+ recall);
		System.out.println("f-measure: "+2*recall*precision/(recall+precision));
		System.out.println(hs.size());

		
	}
	public static String normalize (String s) throws ClassNotFoundException, IOException{
		taggedTweet = tagTweet(s);
		String resultTweet="";
		for (int i = 0; i < taggedTweet[0].length; i++) {
			String word=taggedTweet[0][i];
			if(!hs.contains(word)&&checkChars(word)&&ifToNormalize[i]){//
				String suggestion=getSuggestion(word, i);
				if(suggestion.isEmpty())
					resultTweet=resultTweet.concat(word+" ");
				else resultTweet=resultTweet.concat(suggestion+" ");
			}
			else{
				resultTweet=resultTweet.concat(word+" ");
			}
		}
		return resultTweet ;
	}
	
	private static boolean checkChars (String s){
		for (int i=0; i<s.length(); i++) {
	        char c = s.charAt(i);
	        //!Character.isDigit(c) && 
	        if (!Character.isDigit(c)&&!Character.isLetter(c)&&c!='\'' && c!= '-')
                return false;
		}
		return true;
	}
	private static double simCost (String s1, String s2){
		double max=Math.max(s1.length(),s2.length());
		double lcsr=getLongestCommonSubsequence(s1, s2)/max;
		String str1=s1.replaceAll("[AEIOUaeiou]", "");
		str1=removeDuplicates(str1);
		String str2=s2.replaceAll("[AEIOUaeiou]", "");
		str2=removeDuplicates(str2);
		//System.out.print(" str1: "+str1);
		//System.out.print(" str2: "+str2);
		double sim=0;
		double edDis=Editex.editexCorrect(str1, str2);//editDistance(str1, str2);//
		//System.out.print(" editdis: "+edDis+"\n");
		//if(edDis!=0)
			sim=lcsr*(edDis);
		//else sim=lcsr*1.25;
		return sim;
	}
	
	private static String getSuggestion(String oov, int pos){
		cndSet= new HashMap<String,Double>();
		HashSet<Neighbor> neighbors = new HashSet<Neighbor>();   //creating neighbor list
		for(int i=0;i<taggedTweet[0].length;i++){
			if(!taggedTweet[0][i].equals(oov)){
				Neighbor ne=new Neighbor();
				ne.id=taggedTweet[0][i];
				ne.tag=taggedTweet[1][i];
				ne.position=i-pos;
				neighbors.add(ne);
				//System.out.println("neighbor "+ne.id+" "+ne.position+" "+ne.tag);
			}
		}
		//graphCandidates(neighbors,taggedTweet[1][pos]);  //find candidates and calculate their contextual similarity score
		lexicalCandidates(oov);
		for(Iterator<HashMap.Entry<String, Double>>it=cndSet.entrySet().iterator(); it.hasNext(); ) {
		      HashMap.Entry<String, Double> entry = it.next();
			if(editDistance(entry.getKey(), oov)>2&&getDoubleMetaphoneDistance(entry.getKey(), oov)>1) //düzeltildi
				it.remove();
		}


		boolean isCont=false;
		if(noslang.containsKey(oov))
			isCont=true;										///////////WINDOW SIZE AYARLA MUTLAKA <3 OLACAK////////////

		for(String s : cndSet.keySet()){
			double externalScore=0;
			double freqScore=0;
			double simCost=simCost(oov,s);
			double editDistScore = LAMBDA*Editex.editDistanceScore(oov, s);
			double lexSimScore =simCost+editDistScore;
			freqScore=getFreqScore(s);
			double contSimScore=cndSet.get(s)+freqScore*BETA;
			if(isCont&&noslang.get(oov).equals(s))
				externalScore=1;
			double lastScore=contSimScore+externalScore+lexSimScore;
			cndSet.put(s,lastScore);
			if(lastScore>0.8)
				System.out.println(s+"- cont: "+contSimScore+" simcost: "+simCost+" editDistScore: "+editDistScore+" ext: "+externalScore);
		}
		/*for(String s : cndSet.keySet()){
			System.out.println(s+" : "+cndSet.get(s));
		}*/
		
		double maximum = 0.0;
		String suggestion="";
		for(String s : cndSet.keySet()){
			double aa= cndSet.get(s);
			if(aa>maximum){
				suggestion=s;
				maximum=aa;
			}
		}
		if(maximum>0.8)
			return suggestion;
		else return oov;
	}
	
	private static double getFreqScore(String s){
		
		int freq=0;
		if(nodeFreq.containsKey(s))
			freq=nodeFreq.get(s);
		if (freq >= 715) 
			return 1;
		else if (freq >= 327)
			return 0.8;
		else if (freq >= 205)
			return 0.6;
		else if (freq >= 100)
			return 0.4; 
		else if (freq >= 9)
			return 0.2;
		else return 0;
			
	}
	
	private static void lexicalCandidates(String word){
		for(String s : hs){
			if(editDistance(word, s)<=2){
				if(!cndSet.containsKey(s))
					cndSet.put(s, 0.0);
			}
			else if(getDoubleMetaphoneDistance(word, s)<=1){
				if(!cndSet.containsKey(s))
					cndSet.put(s, 0.0);
			}
		}
	}
	private static void graphCandidates(HashSet<Neighbor> neighbors,String tag) {
		MongoDB mongo=new MongoDB();
		nodeFreq=MongoDB.getNodeFreq(tag);  //store freqs to not to access db all the time
		HashSet<Document> candsFrom = new HashSet<Document>(); //edges from neighbors
		HashSet<Document> candsTo = new HashSet<Document>();   //edges to neighbors
		for (Neighbor neighbor : neighbors) {
			if(neighbor.position<0&&neighbor.position>-4)
				candsFrom.addAll(mongo.getCandidatesFrom(neighbor,tag));
			if(neighbor.position>0&&neighbor.position<4)
				candsTo.addAll(mongo.getCandidatesTo(neighbor,tag));
		}
		int mumu=0;
		for (Document document : candsFrom) {    //calculate edgeWeightScore for nodes that has common edges with neighbors
			String node=document.getString("to");
			Double weight=document.getDouble("weight");
			if(hs.contains(node)&&nodeFreq.containsKey(node)){
				int freq= nodeFreq.get(node);
				if(freq!=0){
					if(cndSet.containsKey(node)){
						cndSet.put(node, cndSet.get(node)+weight/freq*MULTP);	
					}else {
						cndSet.put(node, weight/freq*MULTP); //adding candidate and calculating freq score	
					}
				}
			}
		}
		for (Document document : candsTo) {
			String node=(String) document.get("from");
			Double weight=document.getDouble("weight");
			if(hs.contains(node)&&nodeFreq.containsKey(node)){
				int freq= nodeFreq.get(node);
				if(cndSet.containsKey(node)){
					cndSet.put(node, cndSet.get(node)+weight/freq*MULTP); 	
				}else {					
					cndSet.put(node, weight/freq*MULTP); //adding node and calculating freq score		
				}
				
			}
		}
	}
	
	public static int getDoubleMetaphoneDistance(String s1,String s2){
		DoubleMetaphone mumu = new DoubleMetaphone();		
		String dm1=mumu.doubleMetaphone(s1);
		String dm2=mumu.doubleMetaphone(s2);
		return editDistance(dm1, dm2);
	}
	
	public static int getLongestCommonSubsequence(String a, String b){
		int m = a.length();
		int n = b.length();
		int[][] dp = new int[m+1][n+1];
	 
		for(int i=0; i<=m; i++){
			for(int j=0; j<=n; j++){
				if(i==0 || j==0){
					dp[i][j]=0;
				}else if(a.charAt(i-1)==b.charAt(j-1)){
					dp[i][j] = 1 + dp[i-1][j-1];
				}else{
					dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
				}
			}
		}
	 
		return dp[m][n];
	}
	
	public static String[][] tagTweet(String tweet) throws ClassNotFoundException, IOException{
		RunTagger rtagger = new RunTagger();
		String [] args =new String [3];
		args [0]="RunTagger";
		args [1]="--naber";
		args [2] = tweet;
		return rtagger.runTweet(args);
	}
	
	
	public static void tagFile() throws ClassNotFoundException, IOException{
		RunTagger rtagger = new RunTagger();
		String [] args =new String [3];
		args [0]="RunTagger";
		args [1]="--quite";
		args [2] = "C:/Users/arslanbenzer/workspace/deneme/libraries/tweets.txt";
		rtagger.run(args);
	}
	
	public static String phytonEditex(String str1,String str2){
		/*String[] arguments = {"/libraries/stringcmp-master/stringcmp/__init__.py", str1, str2};
		PythonInterpreter.initialize(System.getProperties(), System.getProperties(), arguments);
		PythonInterpreter interpreter = new PythonInterpreter();
		StringWriter out = new StringWriter();
		interpreter.setOut(out);
		interpreter.execfile("/libraries/stringcmp-master/stringcmp/__init__.py");*/
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import sys\n"
				+ "sys.path.append('C:/Users/arslanbenzer/workspace/Tweet Normalizer/libraries/stringcmp-master/stringcmp')"
				+ "\nimport mumu");
		// execute a function that takes a string and returns a string
		PyObject someFunc = interpreter.get("editex");
		PyObject result = someFunc.__call__(new PyString(str1),new PyString(str2));
		String realResult = (String) result.__tojava__(String.class);
		return realResult;
	}
	
	public static int editDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}	 
		return dp[len1][len2];
	}
	
	private static HashSet<String> removeLetters(HashSet<String> set){   //accept letters other than "a" and "i" as oov
		for (int i = 66; i < 91; i++) {
			char up=(char) i;
			char down=(char) (i+32);
			String upp=up+"";
			String downn=down+"";
			set.remove(upp);
			set.remove(downn);
		}
		set.add("I");
		set.add("i");
		return set;		
	}
    
    public static String removeDuplicates(String strr) {
    	char[] str=strr.toCharArray();
    	if (str == null) return "";
    	int len = str.length;
    	if (len < 3) return strr;
    	for (int i = 2; i < len; ++i) {
    		if(str [i]==str[i-1]&&str[i]==str[i-2]){
    			str[i-2]=0;
    		}
    	}
    	
    	return new String(str).replaceAll("\u0000", "");
    }
}
