import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.misc.NotNull;




public class CFunction extends CBaseListener{
	int UNIT_MAX =  Integer.MAX_VALUE; 

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

	@Override 
	public void enterConstantExpression(CParser.ConstantExpressionContext ctx) {
		if (ctx.IntegerConstant != null){
			if (Integer.valueOf(ctx.IntegerConstant().getText()) > UNIT_MAX){
					System.out.println("Error: " + ctx.IntegerConstant().getText() + " It's too big, the min value of int is: " + UNIT_MAX);
				}
		}
	}
}
