import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import org.antlr.v4.runtime.misc.NotNull;




public class CFunction extends CBaseListener{
	//int UNIT_MAX =  Integer.MAX_VALUE; 

	CParser parser;


	public CFunction(CParser parser)
	{
		this.parser = parser;
		filePositionFucntion.addAll(Arrays.asList("ftell","ftello","ftello64","fseek","fseeko","fseeko64","rewind"));
	}

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


	@Override 
	public void exitPostfixExpression(CParser.PostfixExpressionContext ctx) 
	{
		int countUngetc =  0;
		String fuction = "ungetc";
		String fullSignature = parser.getTokenStream().getText(ctx);
		int initLine = ctx.getStart().getLine();
		
		//System.out.println(fuctions);		

		if (fullSignature.contains(fuction) && fullSignature.length() > fuction.length() )
		{
			StringTokenizer st =  new StringTokenizer(fullSignature.substring(fullSignature.indexOf('(')+1, fullSignature.indexOf(')')), ",");
			st.nextToken();
			String fileStream = st.nextToken();
			//System.out.println("filestream "+ fileStream);
			if (filesInStream.contains(fileStream))
			{
				System.out.println("Warning: Multiple calls to function \"ungetc\" at line:" + initLine);
			}
			else
				filesInStream.add(fileStream);

			//System.out.println(filesInStream);
		}

		//System.out.println(fullSignature);
		for (int i=0; i < fuctions.size() ;i++ ) {
			if( filePositionFucntion.contains(fuctions.get(i)))
			{
				filesInStream.remove(filesInStream.size()-1);
				fuctions.remove(fuctions.get(i));
			}
		}

	}
}
