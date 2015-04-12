/** 
 *Name: Suma Gopal
 *Course: CS 316, Spring 2014
 *Project 1: Lexical Analyzer - Run on Eclipse, in MS Windows OS.
 *Professor Yukawa
 *Description: The sample conditional version code from http://picasso.cs.qc.cuny.edu/cs316/lexArith.java,  
 *was modified and used in this program.  The code I added was in the following parts:
 in enum State and in the method nextState.
 *This class implements the lexical analyzer which accepts the assigned DFA with keywords.
 *
 **IMPORTANT NOTES**:	
 *1)To run this program: Must name the input file as "input.txt" and the output file as "output.txt" 
 *(and both text files must be saved into the same folder as lexArith.java)
 *
 *
 *2)This program appears to function properly according to the given DFA with the given keywords. 
 *However, the output is indented incorrectly- this program might contain errors but I'm not sure. 
 *
 *
 
This program uses the Enum type introduced in JDK 1.5.0.

This class is a lexical analyzer for the tokens defined by the grammar:

<letter> --> a|b|...|z|A|B...|Z
<digit> --> 0|1|...|9
<basic id> --> <letter> {<letter>|<digit>}
<letters and digits> --> {<letter>|<digit>}+
<id> --> <basic id> { ("_"|"-")<letters and digits> //Note: "-" is a dash and "_" is an underscore 
<int> --> [+/-] {<digit>}+
<float> --> [+/-] ( {<digit>}+ "." {<digit>} | "." {<digit>}+ )
<floatE> --> <float> (e|E) [+/-] {<digit>}+
<add> --> +
<sub> --> -
<mul> --> *
<div> --> /
<lt> --> "<"
<le> --> "<="
<gt> --> ">"
<ge> --> ">="
<eq> --> "="
<LParen> --> "("
<RParen> --> ")"

This class implements a DFA that will accept the above tokens.
There are 24 final states accepted by the DFA (9 final states being the keywords labelled with "Keyword_").
The DFA has 15 final states represented by enum-type literals:

state     token accepted

Id        identifiers
Int       integers
Float     floats without exponentiation part
FloatE    floats with exponentiation part
Add      +
Sub     -
Mul     *
Div       /
LParen    (
RParen    )
lt		<
le		<=
gt		>
ge		>=


The DFA also uses 5 non-final states:

state      string recognized

Start      the empty string
Period     float parts ending with "."
E          float parts ending with E or e
EPlusMinus float parts ending with + or - in exponentiation part

The states are represented by an Enum type called "State".
The function "driver" is the driver to operate the DFA. 
The function "nextState" returns the next state given
the current state and the input character.

To modify this lexical analyzer to recognize a different token set,
the functions "nextState", "isFinal" and the enum type "State" need to be modified;
the function "driver" and the other utility functions remain the same.

**/

import java.io.*;


public abstract class lexArith
{
	public enum State 
       	{ 
	  // 5 non-final states     ordinal number

		Start,             	// 0
		Period,            	// 1
		E,                 	// 2
		EPlusMinus,        	// 3
		underscoreMinus, 	// 4

	  // final states

		Id,                	// 5
		Int,               	// 6
		Float,             	// 7
		FloatE,            	// 8
		Add,              	// 9
		Sub,             	// 10
		Mul,             	// 11
		Div,               	// 12
		LParen,            	// 13
		RParen,            	// 14
		Le,					// 15
		Lt,					// 16
		Ge,					// 17
		Gt,					// 20
		Eq,					// 21
		
		// Keywords & their respective identifiers that are non-final states.............................
		Keyword_if, // if - final state
		Id_if,
		If_i,
		Keyword_define, // define - final state
		Id_define, 
		Define_d,
		Define_de,
		Define_def,
		Define_defi,
		Define_defin,
		Keyword_cond, // cond - final state
		Id_cond,
		Cond_c,
		Cond_co,
		Cond_con,
		Keyword_else, // else - final state
		Id_else,
		Else_e,
		Else_el,
		Else_els,
		Keyword_and, // and - final state
		Id_and,
		And_a,
		And_an,
		Keyword_or, // or - final state
		Id_or,
		Or_o,
		Keyword_not, // not - final state
		Id_not,
		Not_n,
		Not_no,
		Keyword_false, // false - final state
		Id_false,
		False_f,
		False_fa,
		False_fal,
		False_fals,
		Keyword_true, // true - final state
		Id_true,
		True_t,
		True_tr,
		True_tru,
		
		UNDEF
	}

	// By enumerating the non-final states first and then the final states,
	// test for a final state can be done by testing if the state's ordinal number
	// is greater than or equal to that of Id.

	public static String t; // holds an extracted token
	public static State state; // the current state of the FA
	private static int a; // the current input character
	private static char c; // used to convert the variable "a" to 
	                       // the char type whenever necessary
	private static BufferedReader inStream;
	private static PrintWriter outStream;

	private static int getNextChar()

	// Returns the next character on the input stream.

	{
		try
		{
			return inStream.read();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return -1;
		}
	} //end getNextChar

	private static int getChar()

	// Returns the next non-whitespace character on the input stream.
	// Returns -1, end-of-stream, if the end of the input stream is reached.

	{
		int i = getNextChar();
		while ( Character.isWhitespace((char) i) )
			i = getNextChar();
		return i;
	} // end getChar

	private static int driver()

	// This is the driver of the FA. 
	// If a valid token is found, assigns it to "t" and returns 1.
	// If an invalid token is found, assigns it to "t" and returns 0.
	// If end-of-stream is reached without finding any non-whitespace character, returns -1.

	{
		State nextState; // the next state of the FA

		t = "";
		state = State.Start;

		if ( Character.isWhitespace((char) a) )
			a = getChar(); // get the next non-whitespace character
		if ( a == -1 ) // end-of-stream is reached
			return -1;

		while ( a != -1 ) // while "a" is not end-of-stream
		{
			c = (char) a;
			nextState = nextState( state, c );
			if ( nextState == State.UNDEF ) // The FA will halt.
			{
				if ( isFinal(state) )
					return 1; // valid token extracted
				else // "c" is an unexpected character
				{
					t = t+c;
					a = getNextChar();
					return 0; // invalid token found
				}
			}
			else // The FA will go on.
			{
				state = nextState;
				t = t+c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if ( isFinal(state) )
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	private static State nextState(State s, char c)

	// Returns the next state of the FA given the current state and input char;
	// if the next state is undefined, UNDEF is returned.

	{
		switch( state )
		{
		case Start:
				if ( c == 'd') 				//Checking for the first letter of each keyword.
					return State.Define_d;
				else if ( c == 'i' )
				    return State.If_i;
				else if ( c == 'c')
					return State.Cond_c;
				else if ( c == 'e')
					return State.Else_e;
				else if ( c == 'a' )
					return State.And_a;
				else if ( c == 'o')
					return State.Or_o;
				else if ( c == 'n')
					return State.Not_n;
				else if ( c == 'f')
					return State.False_f;
				else if ( c == 't')
					return State.True_t;
				else if ( Character.isLetter(c) )
					return State.Id;
			else if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.')
				return State.Period;
			else if ( c == '+' )
				return State.Add;
			else if ( c == '-' )
				return State.Sub;
			else if ( c == '*' )
				return State.Mul;
			else if ( c == '/' )
				return State.Div;
			else if ( c == '(' )
				return State.LParen;
			else if ( c == ')' )
				return State.RParen;
			else if ( c == '<')
				return State.Lt;
			else if( c == '>')
				return State.Gt;
			else if ( c == '=')
				return State.Eq;
			else
				return State.UNDEF;
		case Id:
			if ( Character.isLetterOrDigit(c) )
				return State.Id;
			if( c == '_' || c == '-' )
				return State.underscoreMinus;
			else
				return State.UNDEF;
		case underscoreMinus:
			if( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.UNDEF;
		case Int:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.' )
				return State.Float;
			else
				return State.UNDEF;
		case Lt:
		    if (c == '=')
			return State.Le;
		    else
			return State.UNDEF;
		case Gt: 
			if( c == '=' )
				return State.Ge;
			else
				return State.UNDEF;
		case Sub:
			if( c == '.' )
				return State.Period;
			else if( Character.isDigit(c) )
				return State.Int;
			else 
				return State.UNDEF;
		case Add:
			if( c == '.')
				return State.Period;
			else if( Character.isDigit(c) )
				return State.Int;
			else 
				return State.UNDEF;
		case Period:
			if ( Character.isDigit(c) )
				return State.Float;
			else
				return State.UNDEF;
		case Float:
			if ( Character.isDigit(c) )
				return State.Float;
			else if ( c == 'e' || c == 'E' )
				return State.E;
			else
				return State.UNDEF;
		case E:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else if ( c == '+' || c == '-' )
				return State.EPlusMinus;
			else
				return State.UNDEF;
		case EPlusMinus:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else
				return State.UNDEF;
		case FloatE:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else
				return State.UNDEF;
		default:
			return State.UNDEF;
		case Keyword_if:
			return State.UNDEF;
		case Keyword_and:
			return State.UNDEF;
		case Keyword_define:
			return State.UNDEF;
		case Keyword_not:
			return State.UNDEF;
		
		//....................................................................
		// Cases for the following Keywords:
		// Cases for "define" 
		case Define_d:
			if( c == 'e')
				return State.Define_de;
			else
				return State.Id;
		case Define_de:
			if( c == 'f')
				return State.Define_def;
			else 
				return State.Id;
		case Define_def:
			if( c == 'i')
				return State.Define_defi;
			else 
				return State.Id;
		case Define_defi:
			if( c == 'n')
				return State.Define_defin;
			else 
				return State.Id;
		case Define_defin:
			if( c == 'e')
				return State.Id_define;
			else return State.Id;
		case Id_define:
			if( c == '_' || c == '-')
				return State.underscoreMinus;
			else if ( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.Keyword_define;
			
		//Cases for "if"
		case If_i:
			if( c == 'f')
				return State.Id_if;
			else return State.Id;
		case Id_if:
			if ( c == '_' || c == '-')
				return State.underscoreMinus;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.Keyword_if;
		
		//Cases for "cond"
		case Cond_c:
			if( c == 'o')
				return State.Cond_co;
			else 
				return State.Id;
		case Cond_co:
			if( c == 'n')
				return State.Cond_con;
			else return State.Id;
		case Cond_con:
			if( c == 'd')
				return State.Id_cond;
			else
				return State.Id;
		case Id_cond:
			if( c == '_' || c == '-')
				return State.underscoreMinus;
			else if( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.Keyword_cond;
		
		// Cases for "else"
		case Else_e:
			if( c == 'l')
				return State.Else_el;
			else 
				return State.Id;
		case Else_el:
			if( c == 's')
				return State.Else_els;
			else 
				return State.Id;
		case Else_els:
			if( c == 'e')
				return State.Id_else;
			else 
				return State.Id;
		case Id_else:
			if( c == '_' || c == '-')
				return State.underscoreMinus;
			else if( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.Keyword_else;
		
		// Cases for "and"
		case And_a:
			if( c == 'n')
				return State.And_an;
			else
				return State.Id;
		case And_an:
			if( c == 'd')
				return State.Id_and;
			else
				return State.Id;
		case Id_and:
			if( c == '_' || c == '-' )
				return State.underscoreMinus;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else 
				return State.Keyword_and;
		
		// Cases for "or"
		case Or_o:
			if( c == 'r')
				return State.Id_or;
			else 
				return State.Id;
		case Id_or:
			if( c == '_' || c == '-')
				return State.underscoreMinus;
			else if ( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.Keyword_or;
		
		// Cases for "not"
		case Not_n:
			if( c == 'o')
				return State.Not_no;
			else return State.Id;
		case Not_no:
			if( c == 't')
				return State.Id_not;
			else
				return State.Id;
		case Id_not:
			if( c == '_' || c == '-' )
				return State.underscoreMinus;
			else if( Character.isLetterOrDigit(c) )
				return State.Id;
			else
				return State.Keyword_not; 
		
		// Cases for "false" 
		case False_f:
			if( c == 'a')
				return State.False_fa;
			else return State.Id;
		case False_fa:
			if( c == 'l')
				return State.False_fal;
			else
				return State.Id;
		case False_fal:
			if( c == 's')
				return State.False_fals;
			else 
				return State.Id;
		case False_fals:
			if( c == 'e')
				return State.Id_false;
			else return State.Id;
		case Id_false:
			if( c == '_' || c == '-' )
				return State.underscoreMinus;
			else if( Character.isLetterOrDigit(c) )
				return State.Id;
			else 
				return State.Keyword_false;
		
		// Cases for "true"
		case True_t:
			if( c == 'r')
				return State.True_tr;
			else 
				return State.Id;
		case True_tr:
			if( c == 'u')
				return State.True_tru;
			else
				return State.Id;
		case True_tru:
			if( c == 'e')
				return State.Id_true;
			else 
				return State.Id;
		case Id_true:
			if( c == '_' || c == '-' )
				return State.underscoreMinus;
			else if( Character.isLetterOrDigit(c) )
				return State.Id;
			else
				return State.Keyword_true;
		}
	
	} // end nextState

	
	
	private static boolean isFinal(State state)
	{
		return ( state.compareTo(State.Id) >= 0 );  
	}
	
	public static void getToken()

	// Extract the next token using the driver of the FA.
	// If an invalid token is found, issue an error message.

	{
		int i = driver();
		if ( i == 0 )
			displayln(t + "  -- Invalid Token");
	} // end getToken

	public static void display(String s)
	{
		outStream.print(s);
	}

	public static void displayln(String s)
	{
		outStream.println(s);
	}

	public static void setLex(String infile, String outfile)

	// Sets the input and output streams to "inFile" and "outFile", respectively.
	// Also sets the current input character "a" to the first character on
	// the input stream.

	{
		try
		{
			inStream = new BufferedReader( new FileReader(infile) );
			outStream = new PrintWriter( new FileOutputStream(outfile) );
			a = inStream.read();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	} // end setIO

	public static void closeIO()
	{
		try
		{
			inStream.close();
			outStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	} // end closeIO

	public static void main(String argv[])
	{
		String infile = "input.txt";
		String outfile = "output.txt";
		
		int i;

		setLex( infile, outfile );

		while ( a != -1 ) // while "a" is not end-of-stream
		{
			i = driver(); // extract the next token
			if ( i == 1 )
				displayln( t+"   : "+state.toString() );
			else if ( i == 0 )
				displayln( t+"  -- Invalid Token");
		} 

		closeIO();
	} // end main
} 

