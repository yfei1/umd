
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;


public class FourInARow {
	private static class Board implements Comparable<Board>{
		public Board(int[] mloc, int[] oloc) {
			this.mloc = mloc;
			this.oloc = oloc;
		}
		
		public Board(Board b) {
			this.value =b.value;
			this.mloc = b.mloc.clone();
			this.oloc = b.oloc.clone();
		}
		
		public void setVal(int val) {
			this.value = val;
		}
		
		public int getVal() {			
			return this.value;
			
		}
		
		/*
		 * Keeps the squared sum error from four markers to an optimized line.
		 * Current Optimization Standard: Select the median line.
		 */
		int value = 0;
		int[] mloc = new int[4];
		int[] oloc = new int[4];	
		
		/*public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Option1's SS value is " + squaredSum[0] + ";\n");
			sb.append("Option2's SS value is " + squaredSum[1] + ";\n");
			sb.append("Option3's SS value is " + squaredSum[2] + ";\n");

			return sb.toString();
		}*/
		
		public int[] getMyLoc() {
			return mloc;
		}
		
		public int[] getOppLoc() {
			return oloc;
		}
		
		private boolean oppContain(int num) {
			int[] oloc = this.getOppLoc();
			for (int i = 0; i < 4; i++) {
				if (oloc[i] == num) return true;
			}
			
			return false;
		}
		
		private int[] steps(int turn) {
			int[] arr = (turn == 0)?this.getMyLoc():this.getOppLoc();
			int[] topLeft = new int[4];
			int[] topRight = new int[4];
			int[] vert = new int[4];
			
			int leftMed = 0, rightMed = 0, vertMed = 0;
			
			for(int i = 0; i < 4; i++) {
				topLeft[i] = arr[i] % 10;
				topRight[i] = arr[i] / 10;
				vert[i] = topLeft[i] + topRight[i];
			}
			
			int[] leftClone = topLeft.clone();
			int[] rightClone = topRight.clone();
			int[] vertClone = vert.clone();
			
			Arrays.sort(leftClone);
			Arrays.sort(rightClone);
			Arrays.sort(vertClone);
			
			leftMed = (leftClone[1]+leftClone[2])/2;
			rightMed = (rightClone[1]+rightClone[2])/2;
			vertMed = (vertClone[1] + vertClone[2])/2;
			
			int[] stepToWin = new int[3];
			
			for(int i = 0; i < 4; i++) {
				stepToWin[0] += (Math.abs(topLeft[i]-leftMed)+1)/2;
				if(oppContain(topRight[i]*10+leftMed)) stepToWin[0] +=2;
				
				stepToWin[1] += (Math.abs(topRight[i]-rightMed)+1)/2;
				if(oppContain(rightMed*10 + topLeft[i])) stepToWin[1] +=2;
				
				stepToWin[2] += (Math.abs((Math.abs(vertMed-topLeft[i])-topRight[i]))+1)/2;
			}
			
			return stepToWin;
		}
		
		/*Next move of a given array in order to minimize the Squared Sum value*/
		private int[] squaredSum(int turn) {
			//NOTE:UP&DOWN DIRECTION NOT IMPLEMENTED
			int[] arr = (turn == 0)?this.getMyLoc():this.getOppLoc();
			int[] topLeft = new int[4];
			int[] topRight = new int[4];
			int[] vert = new int[4];
			/*horMed, verMed: the median of their hor/ver-coordiates
			 * horSS, verSS: sum of (xi - xavg)^2 in different axis*/
			int leftMed = 0, rightMed = 0, vertMed = 0,
				leftSS = 0, rightSS = 0, verticalSS = 0;
			
			/*
			 * Initialize hor, ver, horAvg, verAvg
			 */
			for(int i = 0; i < 4; i++) {
				topLeft[i] = arr[i] % 10;
				topRight[i] = arr[i] / 10;
				vert[i] = topLeft[i] + topRight[i];
			}
			
			leftMed = (topLeft[1]+topLeft[2])/2;
			rightMed = (topRight[1]+topRight[2])/2;
			vertMed = (vert[1] + vert[2])/2;

			for(int i = 0; i < 4; i++) {
				/*
				 * Note that the cross over between leftMed and rightMed must fall on the vertical line.
				 * Also, the sum of all digits of that cell is a constant with respect to the vertical line.
				 */
				int lastDigit = arr[i]%10;
				int tensDigit = arr[i]/10;
				int vertTensDigit = Math.abs(vertMed - lastDigit);
				
				//distance from arr[i] to vertTensDigit*10+lastDigit is the shortest point to line distance.
				verticalSS += Math.pow((Math.abs(vertTensDigit - tensDigit) + 1)/2, 2);
				leftSS += Math.pow((Math.abs(topLeft[i] - leftMed) + 1)/2, 2);
				rightSS += Math.pow((Math.abs(topRight[i] - rightMed) + 1)/2, 2);
			}
				
			/*
			ssArr.dir1[0] = arr[leftIndex];
			ssArr.dir2[0] = arr[rightIndex];
			ssArr.dir3[0] = arr[vertIndex];
			*/
			/*int option = ssArr.maxStrat();
			int[] result = arr.clone();
			
			if (option == 1) result[leftIndex] = (topLeft[leftIndex] > leftMed)? result[leftIndex]-2:result[leftIndex]+2;
			else if (option == 2) result[rightIndex] = (topRight[rightIndex] > rightMed)? result[rightIndex]-20:result[rightIndex]+20;
			else result[vertIndex] = (vert[vertIndex] > vertMed)? result[vertIndex]
			*/
			int[] result = {leftSS, verticalSS, rightSS};
			
			return result;
		}

		@Override
		public int compareTo(Board b) {
			return this.value-b.value;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Board) {
				Board b = (Board)o;
				this.value = b.value;
				
				for(int i = 0; i < 4; i++) {
					if (this.mloc[i]!=b.mloc[i] || this.oloc[i]!=b.oloc[i]) return false;
				}
				
				return true;
			}
			return false;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			
			sb.append("[");
			for(int i = 0; i < 4; i++) {
				sb.append(mloc[i] + ", ");
			}
			sb.append("]");
			sb.append("=>" + this.value);
			
			return sb.toString();
		}
	}


	public static int minStrat(int[] squaredSum) {
		int min = 200;
		int index = 0;
		
		for(int i = 0; i < 3; i++)
			if (squaredSum[i] < min) {
				index = i; 
				min = squaredSum[i];
			}
		return min;
	}
	
	public static int maxStrat(int[] squaredSum) {
		int max = 0;
		int index = 0;

		for(int i = 0; i < 3; i++) {
			if (squaredSum[i] > max) {
				index = i;
				max = squaredSum[i];
			}
		}
		
		return max;
	}
	
	private static void updateLocation(Scanner sc, int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(sc.next());
		}
	}
	
	/*Used for debugging*/
	private static void printLoc(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}


	public static Board eval(Board b) {
		//b.value = (int) (Math.pow(200-minStrat(b.squaredSum(0)),2) + minStrat(b.squaredSum(1)));
		int mmin = minStrat(b.steps(0));
		int mmax = maxStrat(b.steps(0));
		int omin = minStrat(b.steps(1));
		int omax = maxStrat(b.steps(1));
		
		b.value = -mmin;
		return b;
	}
	
	
	public static Board alphaBeta (Board board, int depth, int turn, double alpha, double beta){
		if(depth==0) return board;
		else{ 
			int[] mloc=board.mloc;
			int[] oloc=board.oloc;
			if (turn==0){
				HashMap<Integer, ArrayList<Integer>> dest=possibleDest(mloc,oloc);
				ArrayList<Board> possibleMoves=new ArrayList<Board>();
				for(int i=0;i<mloc.length;i++){
					for(int j=0;j<dest.get(mloc[i]).size();j++){
						int[] tempLoc=mloc.clone();
						tempLoc[i]=dest.get(mloc[i]).get(j);
						Board b=eval(new Board(tempLoc,oloc));
						possibleMoves.add(b);
					}
				}
				possibleMoves.sort(null);
				Board b=new Board(board);
				int value=Integer.MIN_VALUE;
				//at each Max node, children are largest-value-first
				for(int i=possibleMoves.size()-1;i>=0;i--){
					Board tempB=possibleMoves.get(i);
					Board result=alphaBeta(tempB,depth-1,1,alpha,beta);
					value=Math.max(value,result.value);					
					if(value==result.value){
						b=new Board(tempB);
						b.setVal(value);
					}
					if(value>=beta) return b;
					else alpha=Math.max(alpha,value);
				}
	             return b;
			}else{
				HashMap<Integer, ArrayList<Integer>> dest=possibleDest(oloc,mloc);
				ArrayList<Board> possibleMoves=new ArrayList<Board>();
				for(int i=0;i<oloc.length;i++){
					for(int j=0;j<dest.get(oloc[i]).size();j++){
						int[] tempLoc=oloc.clone();
						tempLoc[i]=dest.get(oloc[i]).get(j);
						Board b=eval(new Board(mloc,tempLoc));
						possibleMoves.add(b);
					}
				}
				possibleMoves.sort(null);
				int value=Integer.MAX_VALUE;
				Board b=new Board(board);
				//at each Min node, children are smallest-value-first
				for(int i=0;i<possibleMoves.size();i++){
					Board tempB=possibleMoves.get(i);
					Board result=alphaBeta(tempB,depth-1,0,alpha,beta);
					value=Math.min(value,result.value);			
					if(value==result.value){
						b=new Board(tempB);
						b.setVal(value);
					}
					if(value<=alpha) return b;
					else beta=Math.min(beta,value);
				}
				return b;
			}
		}
	}

	
	public static boolean containsE(int index,int[] loc){
		for(int i=0;i<loc.length;i++){
			if(loc[i]==index) return true;
		}
		return false;
	}
	
	public static ArrayList<Integer> searchMove(int position, int[] mloc, int[]oloc){
		ArrayList<Integer> dest=new ArrayList<Integer>();
		int current=position;
		for(int i=0;i<2;i++){
			if(current>=10&&(current%10!=9)){
				current-=9;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		current=position;
		for(int i=0;i<2;i++){
			if(current<100&&(current%10!=0)){
				current+=9;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		current=position;
		for(int i=0;i<2;i++){
			if(current%10!=0){
				current-=1;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		current=position;
		for(int i=0;i<2;i++){
			if(current%10!=9){
				current+=1;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		current=position;
		for(int i=0;i<2;i++){
			if(current>=10){
				current-=10;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		current=position;
		for(int i=0;i<2;i++){
			if(current<100){
				current+=10;
				if((containsE(current,mloc)==false)&&(containsE(current,oloc)==false)&&(!dest.contains(current))){
					dest.add(current);
				}
			}
		}
		return dest;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> possibleDest (int[] mloc,int[]oloc){
		HashMap<Integer, ArrayList<Integer>> destinations=new HashMap<Integer, ArrayList<Integer>>();
		for(int i=0;i<mloc.length;i++){
			int position=mloc[i];
			ArrayList<Integer> oneDes=searchMove(position,mloc,oloc);
			destinations.put(position, oneDes);
		}
		return destinations;
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int[] myLoc = new int[4];
		int[] oppLoc = new int[4];
		updateLocation(sc, myLoc);
		updateLocation(sc, oppLoc);
		int turn = Integer.parseInt(sc.next());
		
		/*
		int[] myLoc = {0,9,103,106};
		int[] oppLoc = {90,91,100,101};
		int turn = 1;
		*/
		
		/*HashMap<Integer, ArrayList<Integer>> temp=possibleDest(myLoc,oppLoc);
		for(int i=0;i<temp.get(6).size();i++){
			System.out.println(temp.get(6).get(i));
		}*/
		
		int depth=1;
		/*
		 * Test Case:
		 * 42 43 44 53
		 * 35 36 46 56
		 * 0
		 */
		
		/*
		int[] myLoc = {7,18,27,38};
		int[] oppLoc = {3,26,100,89};		
		Board b = new Board(myLoc,oppLoc);
		Board ha = alphaBeta (b, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		System.out.println(ha);
		*/

		
		//iterative deepening
		while(depth <= 1){
			depth++;
			Board b = new Board(myLoc, oppLoc);
			
			//long start = System.currentTimeMillis();
			Board ha = alphaBeta (b, depth, turn, Integer.MIN_VALUE, Integer.MAX_VALUE);
			//long end = System.currentTimeMillis();
			
			for(int i = 0; i < 4; i++)
				if (turn == 1 && oppLoc[i] != ha.oloc[i]) {
					System.out.println(oppLoc[i] + " " + ha.oloc[i]);
				} else if (turn == 0 && myLoc[i] != ha.mloc[i]){
					System.out.println(myLoc[i] + " " + ha.mloc[i]);
				}
			
			//System.out.println(end-start);
		
	}

	}


}
