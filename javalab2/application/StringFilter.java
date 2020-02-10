package application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javafx.util.Pair;

public class StringFilter {
	private String src;
	private ArrayList<String> list;
	private ArrayList<Pair<String,Double>> simlist;
	
	StringFilter(String source,ArrayList<String> lst){
		src=source;
		list=lst;
	}
	
	private int matchLen(String src,String target) {
		int x=src.length();
		int y=target.length();
		int[][] T=new int[x+1][y+1];
		int i=0,j=0;
		for(i=0;i<=x;i++) T[i][0]=0;//初始记录表
		for(j=0;j<=y;j++) T[0][j]=0;
		for(i=1;i<=x;i++)
			for(j=1;j<=y;j++) {
				int val= 0;
				if (Character.toUpperCase(src.charAt(i-1))
						==Character.toUpperCase(target.charAt(j-1)))
					val=1; //小心越界
				T[i][j]=Math.max(Math.max(T[i-1][j],T[i][j-1]),T[i-1][j-1]+val);
			}
		return T[x][y];
	}
	
	public ArrayList<String> filter() {
		simlist = new ArrayList<Pair<String,Double>>();
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			String target = it.next();
			Integer len=matchLen(src,target);
			if(len==src.length()) {
				Double sim = 2.0*len.doubleValue()/(src.length()+target.length());
				simlist.add(new Pair<String,Double>(target,sim));
			}
		}

		simlist.sort(new CompareTool());
		ArrayList<String> anslist = new ArrayList<String>();
		Iterator<Pair<String,Double>> simit=simlist.iterator();
		while(simit.hasNext()) {
			anslist.add(simit.next().getKey());
		}
		return anslist;
	}
}

class CompareTool implements Comparator<Pair<String,Double>>{
	@Override
	public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
		// TODO Auto-generated method stub
		return o1.getValue()<o2.getValue()?1:o1.getValue()==o2.getValue()?0:-1;
	}
	
}
