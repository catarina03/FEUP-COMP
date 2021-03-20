
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Main implements JmmParser {


	public JmmParserResult parse(String jmmCode) {
		
		try {
		    Jmm myJmm = new Jmm(new StringReader(jmmCode));
    		SimpleNode root = myJmm.Program(); // returns reference to root node
            	
    		root.dump(""); // prints the tree on the screen


			// Writing the json tree to a file (generated/jmm.json)
			try {
				String jsonTree = root.toJson();
				Files.deleteIfExists(Paths.get("generated/jmm.json"));
				Files.createFile(Paths.get("generated/jmm.json"));
				Files.write(Paths.get("generated/jmm.json"), jsonTree.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			

			// ORIGINAL
    		//return new JmmParserResult(root, myJmm.reports);


			//MY EXPERIMENT
			JmmParserResult result;

			result = new JmmParserResult(root, myJmm.reports);
			System.out.println(result.getReports().toString());
			return result;



		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}
	}

    public static void main(String[] args) {
        System.out.println("Executing with args: " + Arrays.toString(args));
        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }

		JmmParserResult result;

		// Copy of the code in parser function :)
		try {
		    Jmm myJmm = new Jmm(new StringReader(args[0]));
    		SimpleNode root = myJmm.Program(); // returns reference to root node
		
    		root.dump(""); // prints the tree on the screen
    	
    		result = new JmmParserResult(root, myJmm.reports);
			System.out.println(result.getReports().toString());

		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}

    }


}