import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import org.antlr.v4.runtime.misc.NotNull;


public class CFunction extends CBaseListener{
	int MAX_INT =  Integer.MAX_VALUE; 
	int MIN_INT = Integer.MIN_VALUE;
	CParser parser;


	public CFunction(CParser parser)
	{
		this.parser = parser;
		//filePositionFucntion.addAll(Arrays.asList("ftell","ftello","ftello64","fseek","fseeko","fseeko64","rewind"));
	}


	/***************************************************************************************************************************/
	/**********************************************************OVERRIDED RULES**************************************************/
	/***************************************************************************************************************************/
	//Linea de prueba
	
	@Override 
	public void exitUnaryExpression(@NotNull CParser.UnaryExpressionContext ctx) 
	{ 
		if (ctx.unaryOperator()!= null){
			if (ctx.unaryOperator().getText().equals('-')){
				int value = Math.abs(Integer.valueOf(ctx.castExpression().getText()))*-1;
			}
		}
	}

	@Override 
	public void enterMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) 
	{ 
		if (ctx.multiplicativeExpression() != null && ctx.castExpression() != null){
		//	String w = variables.get(q);
	//		System.out.println(w);
			int a = Integer.valueOf(ctx.multiplicativeExpression().getText());
			int b = Integer.valueOf(ctx.castExpression().getText());
			String tokens = parser.getTokenStream().getText(ctx);
			String type = "";
			int initLine = ctx.getStart().getLine();

			for (int i=0; i<tokens.length(); i++){
				if (tokens.charAt(i) == '/'){
					type = "div";
				}
				if (tokens.charAt(i) == '%'){
					type = "mod";
				}
			}
			if (type.equals("div") || type.equals("mod")){
				if (b == 0){
					System.out.println("Error: Ensure that division and remainder operations do not result in divide-by-zero errors at line: " + initLine);
				}
			}
		}

	}

	@Override 
	public void enterPrimaryExpression(CParser.PrimaryExpressionContext ctx) 
	{ 
		//TODO  SOLO SE PUEDE CON INTEGER 
	/*	if (ctx.Constant() != null){
			int initLine = ctx.getStart().getLine();
		//Caso de Entero  //TODO REVISAR COMO SE DIFERENCIA EL ENTERO CON EL FLOTANTE 
			int num = Integer.valueOf(ctx.Constant().getText());
			int precision = 0;
			while (num != 0) {
				if (num % 2 == 1) {
					precision++;
				}
				num >>= 1.0;
			}

			if (precision > 32){
				System.out.println("Warning: Use correct integer precisions at line: " + initLine);
			}
		}*/

	}

	@Override public void exitBlockItem(CParser.BlockItemContext ctx)
	{
		String tokens = parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();

		if (tokens.contains("fputs")) {
			fcloseUsed = false;
		}

		if ((tokens.contains("printf") &&(fcloseUsed))) {
			System.out.println("Warning: The stdout is used after it is closed ");
			System.out.println("Line: " + ruleLine);
			//fcloseUsed = false;
		}else if (tokens.contains("getc")&&(fopenWithStdin))
		{
			System.out.println("Warning: The stdin is used after it is used ");
			System.out.println("Line: " + ruleLine);
			//fopenWithStdin = false;
		}
	}


	@Override 
	public void enterIterationStatement(CParser.IterationStatementContext ctx) 
	{ 
		String tokens = parser.getTokenStream().getText(ctx);
		String t = String.valueOf(ctx.getToken(0,1));
		if (t.equals("for")){

		}
	}
	/*
	@Override 
	public void enterConstantExpression(CParser.ConstantExpressionContext ctx) {
		if (ctx.IntegerConstant != null){
			if (Integer.valueOf(ctx.IntegerConstant().getText()) > UNIT_MAX){
					System.out.println("Error: " + ctx.IntegerConstant().getText() + " It's too big, the min value of int is: " + UNIT_MAX);
				}
		}
	}*/
	ArrayList<String> filesInStream =  new ArrayList<>();
	ArrayList<String> filePositionFucntion =  new ArrayList<>();
	ArrayList<String> fuctions = new ArrayList<>();

	@Override 
	public void exitTypedefName(CParser.TypedefNameContext ctx) 
	{
		
	}



	@Override public void exitAssignmentExpression(CParser.AssignmentExpressionContext ctx)
	{
		String tokens = parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		
		if (!(tokens.indexOf('[') == -1))
		{
			exp30C(tokens,ruleLine);	
		}
		else
		{
			//exp30C(beforeEqual,afterEqual,ruleLine);
		}

		//System.out.println("Antes de ctx");
		if(ctx.assignmentOperator() != null )
		{
			if (ctx.assignmentOperator().getText().equals("=") )
			{
				String leftVariable = ctx.unaryExpression().getText();
				String rightVariable = ctx.assignmentExpression().getText();
				exp32C(leftVariable,rightVariable,ruleLine);
			}

		}

		if (tokens.contains("=")) 
		{	
			//System.out.println("Cadena: " + tokens);
			if (tokens.contains("fopen") && tokens.contains("\"r\"") )
			{
				countFOpen = true;
			}
			else if (tokens.contains("fopen") && tokens.contains("\"w\""))
			{
				countFOpen = false;
			}
			if (!countFOpen) {
				System.out.println("Warning:  An attacker can exploit the race window between the two calls to fopen() to overwrite an existing file");
				System.out.println("Line: "  + ruleLine);
				countFOpen =true;
			}	
		}

	}

	boolean countFOpen = true; 
	boolean fcloseUsed = false;
	boolean fopenWithStdin = false;
	@Override public void exitSelectionStatement(CParser.SelectionStatementContext ctx)
	{
		String tokens = parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		
		if (tokens.contains("fclose")&& (tokens.contains("stdout"))) {
			fcloseUsed = true;
		}
		if (tokens.contains("fopen")&& (tokens.contains("stdin"))) {
			fopenWithStdin = true;
		}
	}


	//<String,String>
	//Identifier, Types!
	HashMap<String,ArrayList<String>> variables =  new HashMap<>();
	ArrayList<String> types =  new ArrayList<>();

	
	@Override public void exitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx)
	{
		types.add(parser.getTokenStream().getText(ctx));
	}

	@Override public void exitDirectDeclarator(CParser.DirectDeclaratorContext ctx)
	{
		variables.put(parser.getTokenStream().getText(ctx),types);
		types = new ArrayList<>();

	}
	@Override public void exitDeclaration(CParser.DeclarationContext ctx)
	{

		String tokens= parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		String q = ctx.getText();
		if (q.contains("+") || q.contains("-"))
			regla(q, ruleLine);

	}

	public void regla(String t, int line){
		String f = t;
		Boolean unsigned = false, signed = false;
		if (f.contains("unsigned")){
			f = f.substring("unsigned".length(),f.length() );
			unsigned = true;
		}
		if (f.contains("signed")){

			f = f.substring("signed".length(),f.length() );
			signed = true;
		}

		if (f.contains("int")){
			f = f.substring("int".length(), f.length());
			String var  = "";
			while (f.contains(";")){
				for (int i=0; i<f.length(); i++){
					if (!(f.charAt(i) == '=')){
						var = var + f.charAt(i);
					}
					else{
						break;
					}
				}

				int pos_in = f.indexOf("=");
				String a ="";
				int in_op = 0;
				char op = 'a';
				if ((f.charAt(pos_in+1)) == '-') {
					a = a+'-';
					pos_in++;
				}
				for (int i=pos_in+1; i<f.length(); i++){
					if (!((f.charAt(i) == '-') || (f.charAt(i) == '+'))){
						a = a + f.charAt(i);
					}
					else{
						op = f.charAt(i);
						in_op = i;
						break;
					}
				}
				String b = "";
				for (int i=in_op+1; i<f.length(); i++){
					if (!(f.charAt(i) == ';')) {
						b = b + f.charAt(i);
					}
					else{
						f = f.substring(0,f.length()-1);
						break;
					}
				}
				
				if (b.contains("(") && b.contains("-")) b=b.substring(1,b.length()-1);

				Integer a_s = 0;
				if (a.charAt(0) == '-'){
					a = a.substring(1,a.length());
					a_s	= Integer.valueOf(a);
				}
				else a_s = Integer.valueOf(a);

				Integer b_s = Integer.valueOf(b);
				Boolean s = false;	

				if (unsigned){					

					if (op =='+' ){
						if ((MAX_INT - a_s < b_s) || (a_s+b_s<a_s))
							s = true;
					}
					if (op == '-') 	{
						if ((a_s < b_s) || (a_s-b_s > a_s)){
							s = true;
						}
					}
					if (s){
						System.out.println("Warning: Ensure that unsigned integer operations do not wrap at line: " + line);
						s = false;
					}
				}
				if(signed){

					if (op =='+'){
						if (((b_s > 0) && (a_s> (MAX_INT - b_s))) || ((b_s < 0) && (a_s < (MIN_INT - b_s)))) {
							s = true;
						}
					}
					if (op == '-'){
						if ((b_s > 0 && a_s < MIN_INT + b_s) ||  (b_s < 0 && a_s > MIN_INT + b_s)  || (a_s-b_s > a_s))  {
							s = true;
						}
					}
					if (s){
						System.out.println("Warning: Ensure that operations on signed integers do not result in overflow at line: " + line);
						s = false;
					}
				}
			}
		}
	}
	
	//Solo una linea de prueba

	@Override public void exitInitDeclaratorList(CParser.InitDeclaratorListContext ctx)
	{

	}



	@Override 
	public void exitPostfixExpression(CParser.PostfixExpressionContext ctx) 
	{
		String tokens= parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();


		
	}

	/***************************************************************************************************************************/
	/**********************************************************AUXILIARY METHODS*************************************************/
	/***************************************************************************************************************************/


	void exp32C(String leftVariable, String rightVariable, int ruleLine)
	{
		if (leftVariable.charAt(0) == '*') 
		{
			leftVariable = leftVariable.substring(1,leftVariable.length());
		}
		
		int lengthRight = rightVariable.length()-1;
		String aux  = "";
		while ( !(rightVariable.charAt(lengthRight) == ('&') || rightVariable.charAt(lengthRight) == ('*')) && lengthRight > 0 )
		{
			aux = aux+=rightVariable.charAt(lengthRight)+"";
			lengthRight--;
		}
		StringBuffer reverse = new StringBuffer(aux);
		aux = new String(reverse.reverse());
		//System.out.println(aux);
		rightVariable = aux;



		if ( variables.get(leftVariable).contains("volatile") && !variables.get(rightVariable).contains("volatile")) 
		{
			System.out.println("Warning:  Volatile objects can not be accessed through a non-volatile-qualified reference");
			System.out.println("Line: " + ruleLine);
		}
	}

	void exp30C(String tokens, int ruleLine)
	{
		System.out.println("Entre a exp30C");
		String insideLimiters = tokens.substring(tokens.indexOf('[')+1,tokens.indexOf(']'));
		String afterEqual = tokens.substring(tokens.indexOf('=')+1,tokens.length());
		if( insideLimiters.contains("+") || insideLimiters.contains("-"))//chequear que la variable tenga ++ 
		{
			String variable;
			if(insideLimiters.charAt(0) == '+' || insideLimiters.charAt(0) == '-')
			{
				variable = insideLimiters.substring(2,insideLimiters.length());
			}
			else
			{
				if (insideLimiters.contains("+")) {
					variable = insideLimiters.substring(0,insideLimiters.indexOf('+'));	
				}
				else
					variable = insideLimiters.substring(0,insideLimiters.indexOf('-'));		
				
			}

			if (afterEqual.contains(variable)) {
				System.out.println("Warning: The order of evaluation of arguments is unspecified and can happen in any order");
				System.out.println("At line:" + ruleLine);
			}

		}
	}


}

