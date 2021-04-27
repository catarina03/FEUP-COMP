
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.report.Report;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import java.io.IOException;
import java.io.FileNotFoundException;

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
       /* if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }

        */

		JmmParserResult result;
		InputStream fileStream = null;

		// Opens file passed in arguments and gets its content
		try{
			File file = new File(args[0]);
			//System.out.println(file.getAbsolutePath());
			fileStream = new FileInputStream(file);
		} catch(FileNotFoundException e){
			System.out.println("Couldn't find file");
			System.exit(0);
		}

		// Parses file
		try {
			Jmm myJmm = new Jmm(fileStream);
    		SimpleNode root = myJmm.Program(); // returns reference to root node
		
    		root.dump(""); // prints the tree on the screen

			// Printing reports
    		result = new JmmParserResult(root, myJmm.reports);
			System.out.println(result.getReports().toString());

			String jsonTree = "";

			// Writing the json tree to a file (generated/jmm.json)
			try {
				jsonTree = root.toJson();
				Files.deleteIfExists(Paths.get("generated/jmm.json"));
				Files.createFile(Paths.get("generated/jmm.json"));
				Files.write(Paths.get("generated/jmm.json"), jsonTree.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			AnalysisStage analysisStage = new AnalysisStage();
			System.out.println("\n\n\n"+analysisStage.semanticAnalysis(result).getReports());

			/*
			SymbolTableManager symbolTable = new SymbolTableManager();

			JmmNode jmmNode = JmmNode.fromJson(jsonTree);
			FirstPreorderVisitor testVisitor = new FirstPreorderVisitor();
			testVisitor.visit(jmmNode, symbolTable);

			//ExampleVisitor exampleVisitor = new ExampleVisitor("identifier", "id");

			
			System.out.println("-- SymbolTable --\n" + symbolTable);
			//System.out.println(testVisitor.visit(jmmNode));

			 */

		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}



    }


}