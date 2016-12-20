	private static void graphCandidates(HashSet<Neighbor> neighbors,String tag) {
		MongoDB mongo=new MongoDB();
		HashSet<Document> candsFrom = new HashSet<Document>(); //edges from neighbors
		nodeFreq=mongo.getNodeFreq(tag);  //store freqs to not to access db all the time
		for (Neighbor neighbor : neighbors) {
			candsFrom.addAll(mongo.getCandidatesFrom(neighbor,tag));
		}
		for (Document document : candsFrom) {    //calculate edgeWeightScore for nodes that has common edges with neighbors
			String node=(String) document.get("to");
			Double weight=document.getDouble("weight");
			if(hs.contains(node)&&nodeFreq.containsKey(node)){
				int freq= nodeFreq.get(node);
				if(freq!=0){
					if(cndSet.containsKey(node)){
						cndSet.put(node, cndSet.get(node)+weight/nodeFreq.get(node));	
					}else {
						cndSet.put(node, weight/freq); //adding candidate and calculating freq score	
					}
				}
			}
		}
		HashSet<Document> candsTo = new HashSet<Document>();   //edges to neighbors
		for (Neighbor neighbor : neighbors) {
			candsTo.addAll(mongo.getCandidatesTo(neighbor,tag));
		}
		for (Document document : candsTo) {
			String node=(String) document.get("from");
			Double weight=document.getDouble("weight");
			if(hs.contains(node)&&nodeFreq.containsKey(node)){
				int freq= nodeFreq.get(node);
				if(cndSet.containsKey(node)){
					cndSet.put(node, cndSet.get(node)+weight/nodeFreq.get(node)); 	
				}else {
					cndSet.put(node, weight/freq); //adding node and calculating freq score		
				}
				
			}
		}

		//System.out.println("cand to: "+candsTo.size()+"cand from: " + candsFrom.size());
	}
	