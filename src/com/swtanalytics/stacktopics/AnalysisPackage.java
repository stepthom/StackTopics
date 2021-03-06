package com.swtanalytics.stacktopics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SaveDataInSource;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequenceWithBigrams;
import cc.mallet.pipe.TokenSequenceNGrams;
import cc.mallet.topics.TopicalNGrams;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;

public class AnalysisPackage {
	
	public ArrayList<Post> posts = new ArrayList<Post>();
	
	public int numTopics 	= 40;
	public double alphaSum 	= 40;
	public double beta 		= 0.01;
	public double gamma 	= 0.02;
	public double delta 	= 0.03;
	public double delta1 	= 0.2;
	public double delta2 	= 100;
	
	public int iterations   = 100;
	public int showTopicsInterval   = 50;
	public int outputModelInterval  = 2000;
	public String outputModelFilename = "model.out";
	public Randoms r = new Randoms();
	
	public int[] gramSizes = {1,2};
	
	InstanceList instances =  null;
	
	
	public void buildInstanceList(){
        // Create a list of pipes that will be added to a SerialPipes object later
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        pipeList.add( new SaveDataInSource() );
        pipeList.add( new Input2CharSequence("STRING") );
        pipeList.add (new CharSequence2TokenSequence());
        pipeList.add( new TokenSequenceNGrams(gramSizes) );
        pipeList.add( new TokenSequence2FeatureSequenceWithBigrams() );
       
        Pipe instancePipe = new SerialPipes(pipeList);
        this.instances = new InstanceList (instancePipe);
        
        ArrayList<Instance> ai = new ArrayList<Instance>();
        for(Post p: posts){
        	Instance i = new Instance(p.preprocessedText, null, p.id, null);
        	i.setSource(p.id);
        	ai.add(i);
        }
        this.instances.addThruPipe(ai.iterator());
        
        // DEBUG OUTPUT
        //for (int i=0; i< this.instances.size(); ++i){
        //	System.out.printf("\nName: %s\n", this.instances.get(i).getName());
        //	System.out.printf("Label: %s\n", this.instances.get(i).getLabeling());
        //	System.out.printf("Data: %s", this.instances.get(i).getData());
        //	System.out.printf("Source: %s\n", this.instances.get(i).getSource());
        //}
	}
	
	
	public void runLDA(){
        
		TopicalNGrams tng = new TopicalNGrams(numTopics, alphaSum, beta, gamma, delta, delta1, delta2);
        tng.estimate(instances, iterations, showTopicsInterval, iterations, outputModelFilename, r);
        
        //tng.docTopicCounts[0][0];
        
        
        tng.printTopWords(10, true);
        try {
			tng.printDocumentTopics(new PrintWriter( new FileWriter (new File("dt.dat"))), 0.1, numTopics);
			tng.printState(new File("soda.out"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
