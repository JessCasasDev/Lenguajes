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

	
	@Override 
	public void exitAdditiveExpression(@NotNull CParser.AdditiveExpressionContext ctx) 
	{ 
		if (ctx.additiveExpression() != null && ctx.multiplicativeExpression() != null){
			int initLine = ctx.getStart().getLine();
			String tokens = parser.getTokenStream().getText(ctx);
			String t = "";

			int a = Integer.valueOf(ctx.additiveExpression().getText());
			int b = Integer.valueOf(ctx.multiplicativeExpression().getText());
			for (int i=0; i<tokens.length(); i++){
				if (tokens.charAt(i) == '+'){
					t = "Add";
					break;
				}
				else if(tokens.charAt(i) == '-'){
					t = "Subs";
					break;
				}
			}
			Boolean s = false;
			if (a > 0 && b > 0){ //Significa que son Unsigned
				if (t.equals("Add")){
					if (MAX_INT - a < b)
						s = true;
					else if (a+b<a)
						s = true;
				}
				if (t.equals("Subs")){
					if (a < b){
						s = true;
					}
					else if (a-b > a){
						s = true;
					}
				}
				if (s){
					System.out.println("Warning: Ensure that unsigned integer operations do not wrap at line: " + initLine);
					s = false;
				}
			}
			else{//Cuando son signed
				if (t.equals("Add")){
					if (((b > 0) && (a> (MAX_INT - b))) || ((b < 0) && (a < (MIN_INT - b)))) {
						s = true;
					}
				}
				if (t.equals("Subs")){
					if ((b > 0 && a < MIN_INT + b) ||  (b < 0 && a > MIN_INT + b)) {
						s = true;
					}
				}
				if (s){
					System.out.println("Warning: Ensure that operations on signed integers do not result in overflow at line: " + initLine);
					s = false;
				}

			}
		}
	}
	@Override 
	public void exitUnaryExpression(@NotNull CParser.UnaryExpressionContext ctx) { 
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



	}

	//<String,String>
	//Identifier, Types!
	HashMap<String,String> variables =  new HashMap<>();
	ArrayList<String> identifiers =  new ArrayList<>();

	@Override public void exitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx)
	{
		identifiers.add(parser.getTokenStream().getText(ctx));
	}

	@Override public void exitDeclaration(CParser.DeclarationContext ctx)
	{
		String tokens= parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		System.out.println("exitDeclaration");
		System.out.println(tokens);

		/*
		if (tokens.contains("volatile")) 
		{   
			String regex = "\\W+";
			String[] a = input.split(regex);
			volatileVars.add(a.getText(a.size()-1));
		}
		*/
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
