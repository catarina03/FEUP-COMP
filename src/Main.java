
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.jasmin.JasminUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
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

			try {
				String jsonTree = root.toJson();
				Files.deleteIfExists(Paths.get("Simple.json"));
				Files.createFile(Paths.get("Simple.json"));
				Files.write(Paths.get("Simple.json"), jsonTree.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

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

		JmmParserResult result;
		InputStream fileStream = null;

		// Opens file passed in arguments and gets its content
		try{
			File file = new File(args[0]);
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
			try {
				jsonTree = root.toJson();
				Files.deleteIfExists(Paths.get("Simple.json"));
				Files.createFile(Paths.get("Simple.json"));
				Files.write(Paths.get("Simple.json"), jsonTree.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			AnalysisStage analysisStage = new AnalysisStage();
			JmmSemanticsResult semanticsResult = analysisStage.semanticAnalysis(result);
			System.out.println("\n\n\n"+ semanticsResult.getReports());

			try {
				String symbolTableFile = semanticsResult.getSymbolTable().print();
				Files.deleteIfExists(Paths.get("Simple.symbols.txt"));
				Files.createFile(Paths.get("Simple.symbols.txt"));
				Files.write(Paths.get("Simple.symbols.txt"), symbolTableFile.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Ollir
			OptimizationStage optimizationStage = new OptimizationStage();
			OllirResult ollirResult = optimizationStage.toOllir(analysisStage.semanticAnalysis(result));

			try {
				String ollirCode = ollirResult.getOllirCode();
				Files.deleteIfExists(Paths.get("Simple.ollir"));
				Files.createFile(Paths.get("Simple.ollir"));
				Files.write(Paths.get("Simple.ollir"), ollirCode.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Jasmin
			BackendStage backendStage = new BackendStage();
			JasminResult jasminResult = backendStage.toJasmin(ollirResult);
			System.out.println(jasminResult.getJasminCode());

			try {
				String jasmminCode = jasminResult.getJasminCode();
				Files.deleteIfExists(Paths.get("Simple.j"));
				Files.createFile(Paths.get("Simple.j"));
				Files.write(Paths.get("Simple.j"), jasmminCode.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Run
			jasminResult.run();
			JasminUtils.assemble(new File("Simple.j"), new File("../comp2021-2e/Simple.class"));
			
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}

    }

}