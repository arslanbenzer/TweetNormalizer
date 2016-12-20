


import com.swabunga.spell.engine.Configuration;
import com.swabunga.spell.engine.EditDistance;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class JSpellChecker  {
	protected SpellChecker checker;
	public JSpellChecker(String word) throws FileNotFoundException, IOException{
		checker = new SpellChecker();
		InputStream is = new FileInputStream("libraries/aspell.txt");
		checker.addDictionary(new SpellDictionaryHashMap(new InputStreamReader(is)));
	}
	
	 private String getSuggestionList(List suggestions) {
		    String s = "";
		    for (Iterator i = suggestions.iterator(); i.hasNext();) {
		      com.swabunga.spell.engine.Word element = (com.swabunga.spell.engine.Word) i.next();
		      s += element.getWord() + "|";

		    }
//		        for (int i = 0; i < suggestions.size(); i ++) {
//		      s += ((com.swabunga.spell.engine.Word)suggestions.elementAt(i)).getWord() + "|";
//		    }
		    return s;
	}
	
	public String getSuggestions(String word) {
	    return getSuggestionList(checker.getSuggestions(word, 0));
	 }
	
}
