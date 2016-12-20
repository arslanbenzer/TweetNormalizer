import java.util.Arrays;
import java.util.regex.Pattern;

import org.python.apache.xerces.util.SynchronizedSymbolTable;

public class Editex {
    public static int[][] lettersInfo =
        {
         /*a*/{0},   /*b*/{1}, /*c*/{2,9}, /*d*/{3}, /*e*/{0}, /*f*/{7},
         /*g*/{6},   /*h*/{},  /*i*/{0},   /*j*/ {6},/*k*/{2}, /*l*/{4},
         /*m*/{5},   /*n*/{5}, /*o*/{0},   /*p*/{7}, /*q*/{2}, /*r*/{4},
         /*s*/{8,9}, /*t*/{3}, /*u*/{0},   /*v*/{7}, /*w*/{},  /*x*/{8},
         /*y*/{0},   /*z*/{8,9}
        }; 

    public static int[] lettersInfo2 =
        {
         /*a*/0,   /*b*/1, /*c*/2, /*d*/3, /*e*/0, /*f*/1,
         /*g*/2,   /*h*/7,  /*i*/0,   /*j*/ 2,/*k*/2, /*l*/4,
         /*m*/5,   /*n*/5, /*o*/0,   /*p*/1, /*q*/2, /*r*/6,
         /*s*/2, /*t*/3, /*u*/0,   /*v*/1, /*w*/7,  /*x*/2,
         /*y*/0,   /*z*/2, /*{*/ 7
        };
    /** Judge whether two letters are in a group or not. */
    private static int isSameGroup2(char char1, char char2){
    	int BIG_COSTS = 3; // If characters are not in same group
    	int SML_COSTS = 2; // If characters are in same group
    	if (char1 == char2)
    		return 0;
    	int code1 = lettersInfo2[char1-'a'];
    	int code2 = lettersInfo2[char2-'a'];
    	if ((code1 == code2) || (code2 == 7))  //Same or silent
    		return SML_COSTS; // Small difference costs
    	else
    		return BIG_COSTS;
    }

    
    public static double editDistanceScore (String string1, String string2){
    	if (string1 == ""&&string2 == "")
    		return 0.0;
    	else if (string1 == string2)
    		return 1.0;
    	/*
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	boolean hasSpecialChar = p.matcher(string1).find();
    	if (hasSpecialChar)
    		return 0.0;
    	hasSpecialChar = p.matcher(string2).find();
    	if (hasSpecialChar)
    		return 0.0;*/
    	int n=string1.length();
    	int m=string2.length();
    	int max_len = Math.max(n, m);
    	if(n>m){  //make sure str1 is longer
    		String temp=string1;
    		string1=string2;
    		string2=temp;
    		int temp2=m;
    		m=n;
    		n=temp2;
    	}
    	double []current=new double [n+1];
    	for (int i = 0; i < current.length; i++) {
			current[i]=i;
			//System.out.print(current[i]+" ");
		}
    	for(int i=1;i<m+1;i++){
    		double [] previous = current.clone();
    		//System.out.print("prev ");
    		/*for (int k = 0; k < current.length; k++) {
				System.out.print(previous[k]+ " ");
			}*/
        	Arrays.fill(current, 0.0);
        	current[0]=i;

    		//System.out.print("crr ");
    		/*for (int k = 0; k < current.length; k++) {
				System.out.print(current[k]+ " ");
			}*/
    		char str2char = string2.charAt(i-1);
    		//System.out.print("\nprev2: ");
    		for(int j=1;j<n+1;j++){
        		/*for (int k = 0; k < current.length; k++) {
    				System.out.print(previous[k]+ " ");
    			}*/
    			double substitute = previous[j-1];

    			//System.out.print("sbsbaþ: "+substitute);
    			if(string1.charAt(j-1)!=str2char){
    				substitute+=1;
    			}
				//System.out.print(" == "+(string1.charAt(j-1)!=str2char)+" ");
    			//System.out.println(substitute);
    			double min=min(previous[j]+1, current[j-1]+1, substitute);
    			//System.out.println("min: "+min);
    			current[j]=min;
    		}
    	}
    	double w = 1.0 - current[n] / (max_len);
    	if(w<0)
    		w=0;
    	if(w>1)
    		w=1;
    	return w;
    }
   
   
    public static double editexCorrect(String string1, String string2){
    	int BIG_COSTS = 3; // If characters are not in same group
    	int SML_COSTS = 2; // If characters are in same group
    	string1=string1.replace("'", "");
    	string2=string2.replace("'", "");
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	boolean hasSpecialChar = p.matcher(string1).find();
    	if (hasSpecialChar)
    		return 0.0;
    	hasSpecialChar = p.matcher(string2).find();
    	if (hasSpecialChar)
    		return 0.0;
    	if (string1 == string2)
		    return 1;
    	int n=string1.length();
    	int m=string2.length();
    	if(n==0||m==0)
    		return 0.0;
    	if(n>m){  //make sure str1 is longer
    		String temp=string1;
    		string1=string2;
    		string2=temp;
    		int temp2=m;
    		m=n;
    		n=temp2;
    	}
        char[] str1 = string1.toCharArray();
        char[] str2 = string2.toCharArray();
        int F[][] = new int[n+1][m+1];
        F[1][0] = BIG_COSTS;   //Initialize first row and first column of cost matrix
        F[0][1] = BIG_COSTS;
        int sum=BIG_COSTS;
        for(int i = 2; i < n+1; i++) {
            sum += isSameGroup2(str1[i-2],str1[i-1]);
            F[i][0]=sum;
        }
        sum=BIG_COSTS;
        for(int i = 2; i < m+1; i++) {
            sum+=isSameGroup2(str2[i-2],str2[i-1]);
            F[0][i] = sum;
        }

        for(int i = 1; i < n+1; i++) {
        	int inc1;
        	if(i==1)
        		inc1=BIG_COSTS;
        	else 
        		inc1=isSameGroup2(str1[i-2], str1[i-1]);
        	for(int j = 1; j < m+1; j++) {
        		int inc2;
        		if (j == 1)
        			inc2 = BIG_COSTS;
        		else
        			inc2 = isSameGroup2(str2[j-2], str2[j-1]);
        		int diag,code1,code2;
        		if (str1[i-1] == str2[j-1])
        			diag = 0;
        		else {
        			code1 = lettersInfo2[str1[i-1]-'a'];  // -1 is not a char
        			code2 = lettersInfo2[str2[j-1]-'a'];  // -2 if not a char
        			if (code1 == code2)  // Same phonetic group
        				diag = SML_COSTS;
        			else
        				diag = BIG_COSTS;
        		}
        		F[i][j] = min(F[i-1][j]+inc1, F[i][j-1]+inc2, F[i-1][j-1]+diag);
        	}

        }
        double  w = 1.0 - ((double) F[n][m] /(double) Math.max(F[0][m],F[n][0]));
        if (w < 0.0)
            w = 0.0;
        return w;
    }
    
    public static double editexCorrect2(String string1, String string2){
    	int BIG_COSTS = 3; // If characters are not in same group
    	int SML_COSTS = 2; // If characters are in same group
    	string1=string1.replace("'", "");
    	string2=string2.replace("'", "");
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	boolean hasSpecialChar = p.matcher(string1).find();
    	if (hasSpecialChar)
    		return 0.0;
    	hasSpecialChar = p.matcher(string2).find();
    	if (hasSpecialChar)
    		return 0.0;
    	if (string1 == string2)
		    return 1;
    	int n=string1.length();
    	int m=string2.length();
    	if(n==0||m==0)
    		return 0.0;
    	if(n>m){  //make sure str1 is longer
    		String temp=string1;
    		string1=string2;
    		string2=temp;
    		int temp2=m;
    		m=n;
    		n=temp2;
    	}
        char[] str1 = string1.toCharArray();
        char[] str2 = string2.toCharArray();
        int F[][] = new int[n+1][m+1];
        F[1][0] = BIG_COSTS;   //Initialize first row and first column of cost matrix
        F[0][1] = BIG_COSTS;
        int sum=BIG_COSTS;
        for(int i = 2; i < n+1; i++) {
            sum += isSameGroup2(str1[i-2],str1[i-1]);
            F[i][0]=sum;
        }
        sum=BIG_COSTS;
        for(int i = 2; i < m+1; i++) {
            sum+=isSameGroup2(str2[i-2],str2[i-1]);
            F[0][i] = sum;
        }

        for(int i = 1; i < n+1; i++) {
        	int inc1;
        	if(i==1)
        		inc1=BIG_COSTS;
        	else 
        		inc1=isSameGroup2(str1[i-2], str1[i-1]);
        	for(int j = 1; j < m+1; j++) {
        		int inc2;
        		if (j == 1)
        			inc2 = BIG_COSTS;
        		else
        			inc2 = isSameGroup2(str2[j-2], str2[j-1]);
        		int diag,code1,code2;
        		if (str1[i-1] == str2[j-1])
        			diag = 0;
        		else {
        			code1 = lettersInfo2[str1[i-1]-'a'];  // -1 is not a char
        			code2 = lettersInfo2[str2[j-1]-'a'];  // -2 if not a char
        			if (code1 == code2)  // Same phonetic group
        				diag = SML_COSTS;
        			else
        				diag = BIG_COSTS;
        		}
        		F[i][j] = min(F[i-1][j]+inc1, F[i][j-1]+inc2, F[i-1][j-1]+diag);
        	}

        }
        return F[n][m];
    }
    
    private static int min (int a, int b, int c){
    	return Math.min(a, Math.min(b, c));
    }
    
    private static double min (double a, double b, double c){
    	return Math.min(a, Math.min(b, c));
    }
    private static boolean isSameGroup(char char1, char char2){
        if(char1 == 'h' || char1 == 'w' || char2 == 'h' || char2 == 'w'
                || char1 == '#' || char2 == '#'){
            return false;
        }
        else {
            int [] char1GroupNum = lettersInfo[char1-'a'];
            int [] char2GroupNum = lettersInfo[char2-'a'];
            for(int i = 0; i < char1GroupNum.length; i++) {
                for(int j = 0; j < char2GroupNum.length; j++) {
                    if(char1GroupNum[i] == char2GroupNum[j]){
                        return true;
                    }
                }
            }
            return false;
        }
    }
    /**
     * Compute that two adjacent letters in a string
     * is equal or in a similar group
     */
    public static int d(char char1, char char2){
        if( (char1 == 'h' || char1 == 'w') && char1 != char2){
            return 1;
        }
        else {
            return r(char1, char2);
        }
    }

    /**
     * Compute that two letters in different strings
     * is equal or in a similar group
     */
    public static int r(char char1, char char2){
        int result = 2;
        if(char1 == char2) {
            result = 0;
        }
        else if(isSameGroup(char1,char2)){
            result = 1;
        }
        return result;
    }

    /**
     * The Editex algorithm which is similar to the normal
     * edit distance algorithm.
     */
    public static double editex(String string1, String string2){
    	string1=string1.replace("'", "");
    	string2=string2.replace("'", "");
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	boolean hasSpecialChar = p.matcher(string1).find();
    	if (hasSpecialChar)
    		return 0.0;
    	hasSpecialChar = p.matcher(string2).find();
    	if (hasSpecialChar)
    		return 0.0;
    	if (string1 .equals("") || string2.equals(""))
		    return 0.0;
    	else if (string1 == string2)
		    return 1.0;
    	int n=string1.length();
    	int m=string2.length();
        char[] str1 = ("#" + string1).toCharArray();
        char[] str2 = ("#" + string2).toCharArray();
        int length1, length2;
        length1 = str1.length;
        length2 = str2.length;

        int F[][] = new int[length1][length2];
        int dStr1[] = new int[length1-1];
        int dStr2[] = new int[length2-1];

        F[0][0] = 0;
        for(int i = 1; i < length1; i++) {
            dStr1[i-1] = d(str1[i-1],str1[i]);
            F[i][0] = F[i-1][0] + dStr1[i-1];
        }
        for(int i = 1; i < length2; i++) {
            dStr2[i-1] = d(str2[i-1],str2[i]);
            F[0][i] = F[0][i-1] + dStr2[i-1];
        }

        for(int i = 1; i < length1; i++) {
            for(int j = 1; j < length2; j++) {
                F[i][j] = min3(F[i - 1][j] + dStr1[i - 1],
                        F[i][j - 1] + dStr2[j - 1],
                        F[i - 1][j - 1] + r(str1[i], str2[j]));

            }

        }
        double  w = 1.0 - ((double) F[n][m] /(double) Math.max(F[0][m],F[n][0]));
        if (w < 0.0)
            w = 0.0;
        return w;
    }
    public static int editex2(String string1, String string2){
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	boolean hasSpecialChar = p.matcher(string1).find();
    	if (hasSpecialChar)
    		return 0;
    	hasSpecialChar = p.matcher(string2).find();
    	if (hasSpecialChar)
    		return 0;
    	int n=string1.length();
    	int m=string2.length();
        char[] str1 = ("#" + string1).toCharArray();
        char[] str2 = ("#" + string2).toCharArray();
        int length1, length2;
        length1 = str1.length;
        length2 = str2.length;

        int F[][] = new int[length1][length2];
        int dStr1[] = new int[length1-1];
        int dStr2[] = new int[length2-1];

        F[0][0] = 0;
        for(int i = 1; i < length1; i++) {
            dStr1[i-1] = d(str1[i-1],str1[i]);
            F[i][0] = F[i-1][0] + dStr1[i-1];
        }
        for(int i = 1; i < length2; i++) {
            dStr2[i-1] = d(str2[i-1],str2[i]);
            F[0][i] = F[0][i-1] + dStr2[i-1];
        }

        for(int i = 1; i < length1; i++) {
            for(int j = 1; j < length2; j++) {
                F[i][j] = min3(F[i - 1][j] + dStr1[i - 1],
                        F[i][j - 1] + dStr2[j - 1],
                        F[i - 1][j - 1] + r(str1[i], str2[j]));

            }

        }
        return  F[length1-1][length2-1];
    }

    /** Get the minimum in three numbers */
    public static int min3(int a, int b, int c) {
        if( a > b )
            return (( c > b) ? b : c);
        else
            return (( c > a) ? a : c);
    }

    public static void main(String[] args){
    	System.out.println(editexCorrect2("confrims", "confrums"));
    }
}
