//package iesds;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class IESDS {
	private class ActionComb {
		ArrayList<Map<Integer, ArrayList<Strategy>>> playerList= new ArrayList<Map<Integer, ArrayList<Strategy>>>();
		int numOfPlayers;
		
		/**
		 * Initialize an instance of ActionComb object.
		 * An ActionComb object contains an arraylist of map, where each map correspond
		 * to all of the strategy combinations of a given play.
		 * A map itself map an action number to all of the strategy combinations it has if the action number is fixed.
		 * 
		 * @param numOfPlayers Number of agents in current game
		 */
		public ActionComb(int numOfPlayers){
			for(int i = 0; i < numOfPlayers; i++) {
				playerList.add(new TreeMap<Integer, ArrayList<Strategy>>());
			}
			this.numOfPlayers = numOfPlayers;
		}
		
		/**
		 * Insert a strategy if the player id and actionNo corresponds to that player is known.
		 * Note that a strategy with n tuples(players) need to be inserted n times to make up
		 * its complete relation diagram 
		 * @param playerNo Current player index
		 * @param actionNo Current action index
		 * @param s Strategy to be inserted
		 */
		public void insert(int playerNo, int actionNo, Strategy s) {
			//Guarantee to be not null
			Map<Integer, ArrayList<Strategy>> temp = playerList.get(playerNo);
			
			if(temp.get(actionNo) == null) {
				temp.put(actionNo, new ArrayList<Strategy>());
			}
			
			temp.get(actionNo).add(s);
		}
		
		/**
		 * Extract the playerNo-th fields from the strategy array once given a playerNo and an actionNo.
		 * 
		 * @param playerNo Current player index
		 * @param actionNo Current action index
		 * @return an array representation of all payoffs of the passed-in player if action actionNo is applied
		 */
		//Zero-based No.
		public int[] extractActionTuple(int playerNo, int actionNo) {
			ArrayList<Strategy> arr = this.playerList.get(playerNo).get(actionNo);
			
			if (arr == null) return null;
			
			int[] tuple = new int[arr.size()];
			
			for (int i = 0; i < tuple.length; i++) {
				tuple[i] = arr.get(i).getPayoff(playerNo);
			}
			
			return tuple;
		}
		
		private double[] mixedDominated(double[][] A, int[] b) {
			int N = b.length;
			
			for(int p = 0; p < N; p++) {
				//Partial pivoting
				int max = p;
				
				for (int i = p+1; i < N; i++) {
					if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
						max = i;
					}
				}
				
				double[] temp = A[p]; int t = b[p];
				A[p] = A[max]; b[p] = b[max];
				A[max] = temp; b[max] = t;
				
				for(int i = p+1; i < N; i++) {
					double alpha = A[i][p]/A[p][p];
					b[i] -=  alpha* b[p];
					for(int j = p; j < N; j++) {
						A[i][j] -= alpha* A[p][j];
					}
				}
			}
			
			double[] x = new double[A[0].length];
			
			for(int i = A[0].length-1; i >= 0; i--) {
				double sum = 0;
				for(int j = i+1; j < N; j++) {
					sum += A[i][j] * x[j];
				}
				x[i] = (b[i] - sum)/A[i][i];
			}
			
			return x;
		}
		
		
		
		/**
		 * Test whether each fields of tuple1 is greater than that of tuple2
		 * @param tuple1 an array representation of all payoff of player i given an action number
		 * @param tuple2 an array representation of all payoff of player i given an action number
		 * @return a boolean value indicates if tuple1 dominates tuple2
		 */
		public boolean strictlyDominated(int[] tuple1, int[] tuple2) {
			if (tuple1 == null || tuple2 == null) return false;
			else if (tuple1.length == tuple2.length) {
				for (int i = 0; i < tuple1.length; i++) {
					if (tuple1[i] <= tuple2[i]) return false;
				}
				
				//return true if tuple1 dominates tuple2
				return true;
			}
			
			return false;
		}
		
		/**
		 * Delete all strategy of listed in the array(playerNo, actionNo) from the relation diagram
		 * @param playerNo Current player index
		 * @param actionNo Current action index
		 */
		public void eliminate(int playerNo, int actionNo) {
			ArrayList<Strategy> arr = this.playerList.get(playerNo).get(actionNo);
			
			for (Strategy s : arr) {
				int[] actionProfile = s.actionProfile;
				
				for(int i = 0; i < actionProfile.length; i++) {
					if (i != playerNo) {
						this.playerList.get(i).get(actionProfile[i]).remove(s);
					}
				}
			}
			
			this.playerList.get(playerNo).remove(actionNo);
		}
		
		/**
		 * Print out the relation diagram in the required form
		 */
		public void printActionComb() {
			System.out.println(numOfPlayers);

			//Print actions left for each players
			for(int i = 0; i < numOfPlayers; i++) {
				Object[] stratArr = this.playerList.get(i).keySet().toArray();
				
				for(int pos = 0; pos < stratArr.length-1; pos++)
					System.out.print(((int)stratArr[pos]+1) + " ");
				System.out.println((int)stratArr[stratArr.length-1]+1);
			}
			
			Map<Integer, ArrayList<Strategy>> player1 = this.playerList.get(0);
			for (Integer i : player1.keySet()) {
				ArrayList<Strategy> arr = player1.get(i);
				StringBuilder sb = new StringBuilder();
				
				for(int pos = 0; pos < arr.size()-1; pos++) {
					sb.append(arr.get(pos).printPayOff() + " ");
				}
				sb.append(arr.get(arr.size()-1).printPayOff());
				
				System.out.println(sb.toString());
			}
		}
	}
	
	private class Strategy {
		int[] actionProfile;
		int[] payoff;
		
		/**
		 * Initialize a Strategy object using its action profile and expected payoff
		 * @param payoff
		 * @param count
		 */
		public Strategy(int[] payoff, int[] count) {
			this.payoff = new int[payoff.length];
			this.actionProfile = new int[count.length];
			
			for(int i = 0; i < payoff.length; i++) {
				this.payoff[i] = payoff[i];
			}
			
			for(int i = 0; i < payoff.length; i++) {
				actionProfile[i] = count[i];
			}
		}
		
		/**
		 * 
		 * @param i
		 * @return the i th field of a strategy's payoff tuple
		 */
		public int getPayoff(int i) {
			return payoff[i];
		}
		
		/**
		 * Used in printActionComb method
		 */
		public String printPayOff() {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < payoff.length-1; i++)
				sb.append(payoff[i] + " ");
			sb.append(payoff[payoff.length-1]);
			
			return sb.toString();
		}
		
		/**
		 * String representation of a relation in detailed.
		 * Used for debugging purpose
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("StrategyProfile:(");
			for (int i = 0; i < actionProfile.length-1; i++) {
				sb.append(actionProfile[i]);
				sb.append(", ");
			}
			
			sb.append(actionProfile[actionProfile.length-1]);
			sb.append(") =>");
			sb.append("(");
			
			for (int i = 0; i < payoff.length-1; i++) {
				sb.append(payoff[i]);
				sb.append(", ");
			}
			
			sb.append(payoff[payoff.length-1]);
			sb.append(")");
			
			return sb.toString();
		}
	}

	private static int calcFrom(int count[], int size[]) {
		int res = 0;
		int blocksize = 1;
		
		for (int i = count.length-1; i > 0; i--) {
			res += blocksize*count[i];
			blocksize *= size[i];
		}
		
		return res;
	}
	
	public static void main(String[] args) {
		IESDS currentGame = new IESDS();
		
		
		
		Scanner sc = new Scanner(System.in);
		//modify tests if the passed in matrix need to be processed or not
		int modify = Integer.parseInt(sc.nextLine());
		
		/* number of agents in this game,
		 * it also indicates the size of each strategy tuple
		 */
		int numOfPlayers = Integer.parseInt(sc.nextLine());
		ActionComb ac = currentGame.new ActionComb(numOfPlayers);
		
		/*double[][] A = {{3,0}, {0,4}};
		int[] b = {1,1};
		double[] x = ac.mixedDominated(A, b);
		
		for(int i = 0; i < x.length; i++) System.out.println(x[i]);
		*/
		
		
		/*
		 * Length of this array represents number of players in this game
		 * The element at position i represents number of actions for player i
		 */
		String[] actionTemp = sc.nextLine().split(" ");
		int[] actionNums = new int[actionTemp.length];
		int totalStratPerLine = 1;
		
		//Convert the string array into an array of integers
		for (int i = 0; i < actionTemp.length; i++) {
			actionNums[i] = Integer.parseInt(actionTemp[i]);
			if (i != 0) totalStratPerLine *= actionNums[i];
		}
				
		int[] count = new int[actionTemp.length];
		
		for (int linesOfInput = 0; linesOfInput < actionNums[0]; linesOfInput++) {
			//Total number of input line counts = # of strategies of agent1
			String[] lineTemp = sc.nextLine().split(" ");

			for (int numPerLine = 0; numPerLine < totalStratPerLine; numPerLine++) {
				if(count[actionNums.length-1] == actionNums[actionNums.length-1]) {
					for(int i = actionNums.length-1; i > 0; i--) {
						if (count[i] == actionNums[i]) {
							count[i] = 0;
							count[i-1]++;
						}
					}
				}
		
				int from = numOfPlayers*calcFrom(count, actionNums);
				int to = from+numOfPlayers;
				int[] currentProfile = new int[numOfPlayers];
				
				for(int temp = from; temp < to; temp++) {
					currentProfile[temp-from] = Integer.parseInt(lineTemp[temp]);
				}
				
				Strategy s = currentGame.new Strategy(currentProfile, count);
				
				for(int playerNo = 0; playerNo < numOfPlayers; playerNo++) {
					ac.insert(playerNo, count[playerNo], s);
				}
				count[actionNums.length-1]++;
				//System.out.println(s.toString());
			}
		}
		
		//Done with the data storage part
		//Here comes the eliminations!
		//Each iteration corresponds to player i
		
		ac.printActionComb();
		
		if (modify == 1) {
			boolean changed = true;
			
			while (changed) {
				changed = false;
				
				for (int i = 0; i < numOfPlayers; i++) {
					
					//Pure strategy part
					for (int j = 0; j < actionNums[i]; j++) {
						for (int k = 0; k < actionNums[i]; k++) {
							int[] tuple1 = ac.extractActionTuple(i, j);
							int[] tuple2 = ac.extractActionTuple(i, k);
							
							if (ac.strictlyDominated(tuple1, tuple2)) {
								changed = true;
								ac.eliminate(i, k);
								//actionNums[i]--;
								System.out.println();
								ac.printActionComb();
							}
							
							for(int l = j+1; l < actionNums[i]; l++) {
								int[] tuple3 = ac.extractActionTuple(i, l);

								if (j != k && k != l &&tuple1 != null && tuple2 != null && tuple3 != null) {
									double[] range = {0,1};
									boolean excep = false;
									
									for (int temp = 0; temp < tuple1.length; temp++) {
										if (tuple1[temp] != tuple3[temp]) {
											double p = (double)(tuple2[temp]-tuple3[temp])/(tuple1[temp]-tuple3[temp]);
											if (tuple1[temp] > tuple3[temp]) {
												if (range[0]< p) range[0] = p;
											} else if (tuple1[temp] < tuple3[temp]) {
												if (range[1] > p) range[1] = p;
											}
										} else {
											excep = true;
											break;
										}
									}
									
									if (!excep && range[0] > 0 && range[1] < 1 && range [0] < range[1]) {
										changed = true;
										ac.eliminate(i, k);
										System.out.println();
										ac.printActionComb();
									}
								}
							}
						}
					}
					
					//Mixed strategy part -- not sure if it is efficient
					
					/*
					 * If fixed a player i, then there are actionNums[i] strategies
					 * available to mix up.
					 * m equals actionNums[i]
					 */
					
					/*
					int t = 1;
					
					for(int acc = 0; acc < actionNums.length; acc++) {
						if (acc != i) t *= actionNums[i];
					}
					
					double[][] A = new double[t][actionNums[i]-1];
					
					
					
					
					for (int currAction = 0 ; currAction < actionNums[i]; currAction++) {
						int offset = 0;

						//Not good for cache though
						for (int col = 0; col < actionNums[i]-1; col++) {
							int[] colArr = ac.extractActionTuple(i, col);
							
							if (col != currAction) {
								for (int row = 0; row < t; row++) {
									A[row][offset] = colArr[row];
								}
								offset++;
							}
						}
						
						double[] x = ac.mixedDominated(A, ac.extractActionTuple(i, currAction));
						double sum = 0;
						
						for (int xpos = 0; xpos < x.length; xpos++) {
							if (x[xpos] < 0) x[xpos] = 0;
							sum += x[xpos];
						}
						
						if (sum < 1) {
							changed = true;
							ac.eliminate(i, currAction);
							System.out.println();
							ac.printActionComb();
						}
					}*/
				}
			}
			
			sc.close();
			
		}
	}

}
