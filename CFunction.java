import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import org.antlr.v4.runtime.misc.NotNull;




public class CFunction extends CBaseListener{
	//int UNIT_MAX =  Integer.MAX_VALUE; 

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
	public void exitAdditiveExpression(@NotNull CParser.AdditiveExpressionContext ctx) { 
		try{
			if (ctx.additiveExpression() != null && ctx.multiplicativeExpression() != null){
				/*
				if (Integer.valueOf(ctx.additiveExpression().getText()) > UNIT_MAX){
					System.out.println("Error: " + ctx.additiveExpression().getText() + " It's too big, the min value of int is: " + UNIT_MAX);
				}
				else if (Integer.valueOf(ctx.additiveExpression().getText()) > UNIT_MAX){
					System.out.println("Error: " + ctx.multiplicativeExpression().getText() + " It's too big, the min value of int is: " + UNIT_MAX);
				}
				else{*/
				/*Regla: https://www.securecoding.cert.org/confluence/display/c/INT30-C.+Ensure+that+unsigned+integer+operations+do+not+wrap*/
					int a = Integer.valueOf(ctx.additiveExpression().getText());
					int b = Integer.valueOf(ctx.multiplicativeExpression().getText());
					//b = Math.abs(b)*-1;
					if (a+b < a){
						System.out.println("Error: Ensure that unsigned integer operations do not wrap");
					}
					else{
						System.out.println(a+b);
					}
				}
			
		}catch(Exception e){
			System.out.println(e);
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
		String tokens = parser.getTokenStream().getText(ctx);

		//System.out.println(tokens);
		/*
		if (!( tokens.indexOf('(') == -1 ))
		{
			fuctions.add(tokens.substring(0,tokens.indexOf(')')-1));
		}
		*/ //Esta regla toma identificadores, pueden haber variables tambien
		fuctions.add(tokens);
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

	ArrayList<String> volatileVars = new ArrayList<>();

	@Override public void exitDeclaration(CParser.DeclarationContext ctx)
	{
		String tokens= parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		
		if (tokens.contains("volatile")) 
		{
			String regex = "\\W+";
			String[] a = input.split(regex);
			volatileVars.add(a.getText(a.size()-1));
		}
		


	
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
