

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Java + MongoDB in Secure Mode
 *
 */
public class MongoDB {
	static MongoDatabase db;
	static int count =0;
	static double result=0.0;
    static double cnt =0;
    static String nodeId="",nodeText="";
    public static void main(String[] args) {
	
	    MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
	    db = mongoClient.getDatabase( "twitter_data" );
	    FindIterable<Document> iterable = db.getCollection("edges").find(new Document("from","february"));//.append("to", "big").append("to_tag", "A").append("dis", 0));

		iterable.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	System.out.println(document);
	        	cnt+=document.getDouble("weight");
	        }
	        
	    });
		System.out.println(cnt/5600);
    }
    
	public HashSet<Document> getCandidatesFrom(Neighbor neighbor,String tag) {   //edges
		HashSet<Document> cands = new HashSet<Document>();
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		System.out.println(neighbor.id);
		FindIterable<Document> iterable = db.getCollection("edges").find(new Document("from",neighbor.id).append("from_tag",neighbor.tag)
				.append("to_tag", tag).append("dis", Math.abs(neighbor.position)-1)); //POS filtering with tag and distance
		iterable.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	cands.add(document);       
	        }
	        
	    });
		mongoClient.close();
		return cands;
	}
	
	

	public HashSet<Document> getCandidatesTo(Neighbor neighbor,String tag) {
		HashSet<Document> cands = new HashSet<Document>();
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		FindIterable<Document> iterable2 = db.getCollection("edges").find(new Document("to",neighbor.id).append("to_tag",neighbor.tag)
				.append("from_tag", tag).append("dis", Math.abs(neighbor.position)-1));  //POS filtering with tag and distance
		iterable2.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	cands.add(document);
	        }
	        
	    });
		mongoClient.close();
		return cands;
	}
	
	/*public HashSet<Document> getCandidatesFrom(Neighbor neighbor,String tag) {   //edges 2
		HashSet<Document> cands = new HashSet<Document>();
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		System.out.println(neighbor.id);
		String fromId=getID(neighbor.id,tag);
		FindIterable<Document> iterable = db.getCollection("edges2").find(new Document("from",fromId).append("from_tag",neighbor.tag)
				.append("to_tag", tag).append("dis", Math.abs(neighbor.position)-1)); //POS filtering with tag and distance
		iterable.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	cands.add(document);       
	        }
	        
	    });
		mongoClient.close();
		return cands;
	}

	public HashSet<Document> getCandidatesTo(Neighbor neighbor,String tag) {
		HashSet<Document> cands = new HashSet<Document>();
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		String toId=getID(neighbor.id,tag);
		FindIterable<Document> iterable2 = db.getCollection("edges2").find(new Document("to",toId).append("to_tag",neighbor.tag)
				.append("from_tag", tag).append("dis", Math.abs(neighbor.position)-1));  //POS filtering with tag and distance
		iterable2.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	cands.add(document);
	        }
	        
	    });
		mongoClient.close();
		return cands;
	}*/
	
	public static HashMap<String,Integer> getNodeFreq(String tag){

		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		HashMap<String,Integer> nodes =new HashMap<String,Integer>();
		FindIterable<Document> iterable = db.getCollection("nodes").find(new Document("tag",tag));
		iterable.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	nodes.put(document.getString("node"), document.getInteger("freq"));
	        }
	        
	    });
		mongoClient.close();
		return nodes;
	}

	public HashSet<String> getWords(HashSet<String> hs) {
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		HashSet<String> nodes =new HashSet<String>();
		FindIterable<Document> iterable = db.getCollection("nodes").find();
		iterable.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	String word = document.getString("node");
	        	boolean isOOV= document.getBoolean("ovv");
	        	int freq=document.getInteger("freq");
	        	if(!isOOV&&hs.contains(word)&&freq>20)
	        		nodes.add(word);
	        }
	        
	    });
		return nodes;
	}
	public static String getID (String node,String tag){
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		FindIterable<Document> iterable2  = db.getCollection("nodes").find(new Document("node",node).append("tag", tag));
		iterable2.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	nodeId=document.getString("_id");
	        }
	        
	    });
		return nodeId;
	}
	
	public static String getText (String id){
		MongoClient mongoClient = new MongoClient();// "localhost" , 27017 );
		db = mongoClient.getDatabase( "twitter_data" );
		FindIterable<Document> iterable2  = db.getCollection("nodes").find(new Document("_id",id));
		iterable2.forEach(new Block<Document>() {
	        @Override	        
	        public void apply(final Document document) {
	        	nodeText=document.getString("node");
	        }
	        
	    });
		return nodeText;
	}
}