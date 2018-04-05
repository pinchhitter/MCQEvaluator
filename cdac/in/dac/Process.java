package cdac.in.dac;

/*
 * @author Chandra Shekhar
 * @date  2018/04/04  
 * @about This program evalute the MCQ with given Key and Responses This
 *        also generate the key analysis and difficulty level of the keys
 *
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import java.text.DecimalFormat;

class Paper{
	
	String paperName;
	Map<Integer,Question> questions;
	Map<Integer,KeyAnalysis> keyAnalysis;
	List<Candidate> candidates;
	int DL1, DL2, DL3, DL4, DL5;

	Paper(String paperName){

		this.paperName = paperName;
		this.questions = new TreeMap<Integer, Question>();
		this.candidates = new ArrayList<Candidate>();
		this.keyAnalysis = new TreeMap<Integer, KeyAnalysis>();

		this.DL1 = 0;
		this.DL2 = 0;
		this.DL3 = 0;
		this.DL4 = 0;
		this.DL5 = 0;
	}

	void process(Double scale){

		for(Candidate candidate: candidates){

			for( Integer quid: candidate.responses.keySet() ){
				Response response = candidate.responses.get( quid ); 
				Question question = questions.get( quid );
				Double marks = question.eval( response );
				candidate.totalMarks += marks;
				candidate.marks.put( quid, marks );
			}
			if( scale != null )
				candidate.scaleMarks = new Double(  new DecimalFormat("#0.0#").format( ( candidate.totalMarks / questions.keySet().size() ) * scale) );
			else
				candidate.scaleMarks = candidate.totalMarks;
		}
	}

	boolean isUnattempted(Response response){
		for(String res: response.responses ){
			if( res.equals("-") )
				return true;
		}
		return false;
	}

	void keyAnalysis(int topAnalysis ){

		int top = Math.max( topAnalysis, ( candidates.size() * topAnalysis )/ 100 );

		for( Candidate candidate: candidates ){

			for(Integer quid: questions.keySet() ){

				if( questions.get( quid ).isMTA || questions.get( quid ).isMTN ){
					continue;
				}

				KeyAnalysis ka = keyAnalysis.get( quid );

				if( ka == null ){
					ka = new KeyAnalysis( quid, questions.get( quid ).answer );
				}
				
				if( top > ( ka.correct + ka.wrong ) ) {	

					boolean isAttempt = false;

					if( candidate.marks.get( quid ) > 0 ){
						ka.correct++;
						isAttempt = true;
					}else if ( !isUnattempted( candidate.responses.get( quid ) ) ){
						ka.wrong++;
						isAttempt = true;
					}
					if( isAttempt ){
						ka.add( candidate.responses.get( quid ), quid );
						keyAnalysis.put(quid, ka);
					}
				}
			}
		}
	}

	void ranking(){
		Collections.sort( candidates, new SortbyMarks() );
		Integer rank = 0;
		Double  prevMarks = new Double(-1.0);
		int count = 0;
		for(Candidate candidate: candidates){
			count++;
			if( candidate.totalMarks.compareTo( prevMarks ) != 0 )
			rank = count;
			candidate.rank = rank;
			prevMarks = candidate.totalMarks;
		}
	}

	void print(){
		Candidate.printHeader();
		for(Candidate candidate: candidates){
			candidate.print();	
		}
	}

	void printKeyAnalysis(int topAnalysis ){

		int top = Math.max( topAnalysis, ( candidates.size() * topAnalysis )/ 100 );

		System.out.println(topAnalysis+"% candidates of total are: "+top);

		for(Integer quid : keyAnalysis.keySet() ){

			KeyAnalysis ka = keyAnalysis.get( quid );
			boolean isDoubt = false;
			for( String ans: ka.ansCount.keySet() ){
				if( ka.correct < ka.ansCount.get( ans )  ){
					isDoubt = true;
					break;
				}
			}
			if( isDoubt )
				keyAnalysis.get( quid ).print();
		}
	}

	void printQuestions(){
		Question.printHeader();
		for(Integer quid: questions.keySet()){
			Question question = questions.get(quid);
			Double perR =  Double.parseDouble( new DecimalFormat("#0.0#").format( (double) (100 / (double) this.candidates.size() ) * (double) question.correct ) );

			if( perR > 80.00d ){
                                question.DL = "DL1";
				DL1++;
                        }else if( perR >= 60.00d && perR <= 80.00d ){
                                question.DL = "DL2";
				DL2++;
                        }else if( perR >= 40.00d && perR <= 60.00d ){
                                question.DL = "DL3";
				DL3++;
                        }else if( perR >= 20.00d && perR <= 40.00d ){
                                question.DL = "DL4";
				DL4++;
                        }else if( perR < 20.00d ){
                                question.DL = "DL5";
				DL5++;
                        }
			question.print( quid );
		}	
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("DL1("+DL1+"), DL2("+DL2+"), DL3("+DL3+"), DL4("+DL4+"), DL5("+DL5+")");
		System.out.println("------------------------------------------------------------------------------\n");
	}
}

class Process{

	Paper paper;

	Process(String paper){
		this.paper = new Paper(paper);
	}

	void readAnswerkey(String filename, boolean header){
		BufferedReader br = null;
		int count = 0;
		try{	
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			while( (line = br.readLine()) != null ){
				if( header ){
					header = false;
					continue;
				}		
				count++;
				String[] token = line.split(",");
				paper.questions.put( new Integer(token[0]), new MultipalChoice( new Answer(token[1].trim()), new Double( token[2].trim() ), new Double( token[3].trim() ) ) );
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null )
					br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.err.println("Total "+count+" Question Read");
	}

	void readResponses(String filename, boolean header){
		BufferedReader br = null;
		int count = 0;
		try{	
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			while( (line = br.readLine()) != null ){
				if( header ){
					header = false;
					continue;
				}	
				count++;
				String[] token = line.split(",");
				paper.candidates.add( new Candidate( token[0].trim(), token[2].trim() ) );
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null )
					br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.err.println("Total "+count+" Candidate Read");
	}

	void processing(Double scale, int topAnalysis){

		paper.process( scale );
		paper.ranking();

		paper.keyAnalysis( topAnalysis );
		paper.printKeyAnalysis( topAnalysis );
		paper.printQuestions();
		paper.print();
	}

	public static void main(String[] args){
		String keyFile = null;
		String responseFile = null;
		String paper = null;
		Double scale = null;
		int topAnalysis = 10;

		int i = 0;
		while( i < args.length ){
			if( args[i].equals("-k") ){
				keyFile = args[i+1];
				i++;	
			}else if ( args[i].equals("-r")){
				responseFile = args[i+1];
				i++;
			}else if ( args[i].equals("-p")){
				paper = args[i+1];
				i++;
			}else if ( args[i].equals("-s")){
				scale = new Double( args[i+1].trim() );
				i++;
			}else if ( args[i].equals("-t")){
				topAnalysis = Integer.parseInt( args[i+1].trim() );
				i++;
			}
			i++;
		}
		if( keyFile == null || responseFile == null ){
			System.err.println("java Process -k <keyfile> -r <resposneFile> -p <paper> -s <to-scale> -t <topAnalysis>");
			return;
		}
		Process p = new Process(paper);
		p.readAnswerkey( keyFile, true);
		p.readResponses( responseFile, true);
		p.processing( scale, topAnalysis );

	}
}
