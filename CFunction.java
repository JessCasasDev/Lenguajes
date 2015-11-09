import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import org.antlr.v4.runtime.misc.NotNull;




public class CFunction extends CBaseListener{
	int UNIT_MAX =  Integer.MAX_VALUE; 

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
		if (ctx.additiveExpression() != null && ctx.multiplicativeExpression() != null){
			int initLine = ctx.getStart().getLine();
			String tokens = parser.getTokenStream().getText(ctx);
			String t = "";
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
			int a = Integer.valueOf(ctx.additiveExpression().getText());
			int b = Integer.valueOf(ctx.multiplicativeExpression().getText());
			Boolean s = false;
					if (a > 0 && b > 0){ //Significa que son Unsigned
						if (t.equals("Add")){
							if (UNIT_MAX - a < b)
								s = true;
							else if (a+b<a)
								s = true;
							else
								System.out.println(a+b);
						}
						if (t.equals("Subs")){
							if (a < b)
								s = true;
							else if (a-b > a)
							{
								s = true;
							}
							else
								System.out.println(a-b);
						}
						if (s) System.out.println("Warning: Ensure that unsigned integer operations do not wrap at line: " + initLine);
					}
				}
			}
	@Override 
	public void exitUnaryExpression(@NotNull CParser.UnaryExpressionContext ctx) 
	{ 
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

	}

	//<String,String>
	//Identifier, Types!
	HashMap<String,ArrayList<String>> variables =  new HashMap<>();
	ArrayList<String> types =  new ArrayList<>();

	@Override public void exitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx)
	{
	
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

	@Override public void exitDeclaration(CParser.DeclarationContext ctx)
	{
		String tokens= parser.getTokenStream().getText(ctx);
		int ruleLine = ctx.getStart().getLine();
		
		//System.out.println(variables);


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


}
