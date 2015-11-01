import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.File;
import java.util.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			CLexer lexer;
			if (args.length > 0 )
			{
				lexer = new CLexer(new ANTLRFileStream(args[0]));
			}
			else
			{
				lexer = new CLexer( new ANTLRInputStream(System.in));
			}
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			CParser parser = new CParser(tokens);
			//ParseTree tree = parser.init();	
		}

		catch(Exception e)
		{
			System.err.println("Error (Main) ");
			e.printStackTrace();
		}	
	}
	
}