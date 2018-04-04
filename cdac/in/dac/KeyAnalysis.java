package cdac.in.dac;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class KeyAnalysis{

	Integer Id;
	Answer answer;
	int correct;
	int wrong;
	int unattempted;
	Map<String, Integer> ansCount;

	KeyAnalysis(Integer Id, Answer answer){
		this.Id = Id;
		this.answer = answer;
		this.correct = 0;
		this.wrong = 0;
		this.unattempted = 0;
		this.ansCount = new TreeMap<String,Integer>();
			
	}

	void add(Response response){
		for(String res: response.responses ){
			Integer count = ansCount.get( res );
		        if( count == null )
				count = new Integer(0);
			count++;
			ansCount.put( res, count );	
		}
	}

	void add(Response response, Integer quid){
		for(String res: response.responses ){
			Integer count = ansCount.get( res );
		        if( count == null )
				count = new Integer(0);
			count++;
			ansCount.put( res, count );	
		}
	}	

	void print(){

		TreeMap<Integer, ArrayList<String>> counts = new TreeMap<Integer, ArrayList<String>>( Collections.reverseOrder() );
		for(String ans: ansCount.keySet() ){
			ArrayList<String> values = counts.get( ansCount.get( ans )  );
			if( values == null){
				values = new ArrayList<String>();
			}
			values.add(ans);
			counts.put( ansCount.get(ans), values );
		}
		System.out.println(" ________________________");
		System.out.format("|QuestionID     | %4d   |\n",Id);
		System.out.println("|_______________|________|");
		for(String ans: answer.answers){
			System.out.format("|Answer         | %4s   |\n",ans);
		}
		System.out.format("|Correct        | %4d   |\n",correct);
		System.out.format("|Wrong          | %4d   |\n",wrong);
		System.out.println("|_______________|________|");
		for(Integer key: counts.keySet() ){
			List<String> values = counts.get( key );
			for(String value: values){
				System.out.format("|%-5s          | %4d   |\n", value.toUpperCase(), key);
			}
		}
		System.out.println("|_______________|________|");
	}
}
