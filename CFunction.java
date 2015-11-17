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
	HashMap<String, String> variablesValue = new HashMap<>();
	//<String,String>
	//Identifier, Types!
	HashMap<String,ArrayList<String>> variables =  new HashMap<>();
	ArrayList<String> types =  new ArrayList<>();

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
	public void exitAdditiveExpression(CParser.AdditiveExpressionContext ctx) 
	{ 
		int line = ctx.getStart().getLine();
		
		if (ctx.additiveExpression() != null && ctx.multiplicativeExpression() != null){
			String var_a = ctx.additiveExpression().getText();
			String var_b = ctx.multiplicativeExpression().getText();
			ArrayList<String> type = new ArrayList();
			String type_a="", type_b="";
			int a = 0, b=0;
			if(variablesValue.containsKey(var_a)){
				 a = Integer.valueOf(variablesValue.get(var_a));
				 type = variables.get(var_a);
				 type_a = type.get(0);
			}
			else {
				a = Integer.valueOf(var_a);
			}
			if(variablesValue.containsKey(var_b)){
				b = Integer.valueOf(variablesValue.get(var_b));
				type = variables.get(var_b);
				type_b = type.get(0);
			}
			else{
				b = Integer.valueOf(var_b);
 			}
 			String operator = "";
 			if (ctx.getText().contains("+")) operator = "suma";
 			else operator = "resta";
 			
 			ruleint3032C(a,b,type_a,type_b,operator, line); 			
		}
	}

	@Override public void enterMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) 
	{ 
		if (ctx.multiplicativeExpression() != null && ctx.castExpression() != null){
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

	
	@Override public void exitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx)
	{
		types.add(parser.getTokenStream().getText(ctx));
	}

	@Override public void exitDirectDeclarator(CParser.DirectDeclaratorContext ctx)
	{
		variables.put(parser.getTokenStream().getText(ctx),types);
		types = new ArrayList<>();

	}
	
	@Override 
	public void enterInitDeclarator(CParser.InitDeclaratorContext ctx) { 
		
		if (ctx.declarator()!=null && ctx.initializer()!= null)
				variablesValue.put(ctx.declarator().getText(), ctx.initializer().getText());

			else
				variablesValue.put(ctx.declarator().getText(), "No Value");	
	}

	@Override 
	public void exitCastExpression(CParser.CastExpressionContext ctx) 
	{
		int ruleLine = ctx.getStart().getLine();
		if (ctx.typeName()!= null && ctx.castExpression()!=null){
			String type = "";
			if (ctx.typeName().getText().contains("unsigned"))
				type= "unsigned";
			else 
				type = "signed";
			String var = ctx.castExpression().getText();
			if (variables.containsKey(var)){
				ArrayList<String> objects = variables.get(var);
				if (!type.equals(objects.get(0))){
					if (variablesValue.containsKey(var)){
						int y = Integer.valueOf(variablesValue.get(var));
						if (y<0)
							System.out.println("Warning: Ensure that integer conversions do not result in lost or misinterpreted data in Line: " + ruleLine);	
					}
				}
			}
		}
	}

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
	void ruleint3032C(int a,int b,String type_a,String type_b,String operator, int line){
		Boolean s = false;
		if (type_a.equals("signed") && type_b.equals("signed")){
			if (operator.equals("suma")){
				if (((b > 0) && (a > (MAX_INT - b))) || ((b < 0) && (a < (MIN_INT - b)))) 
					s = true;
				
			}	
			if (operator.equals("resta")){
				if ((b> 0 && a< MIN_INT + b) ||  (b < 0 && a > MIN_INT + b)  || (a-b > a))  
					s = true;

			}
			if (s){
				System.out.println("Warning: Ensure that operations on signed integers do not result in overflow at line: " + line);
				s = false;
			}
		}
		else if (type_a.equals("unsigned") && type_b.equals("unsigned")){
			if (operator.equals("suma") ){
				if ((MAX_INT - a < b) || (a+b<a))
					s = true;
			}
			if (operator.equals("resta")) 	{
				if ((a < b) || (a-b > a)){
					s = true;
				}
			}
			if (s){
				System.out.println("Warning: Ensure that unsigned integer operations do not wrap at line: " + line);
				s = false;
			}
		}		
	}
}