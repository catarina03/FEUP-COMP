import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.io.StringReader;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;


public class ExampleTest {



    @Test
    public void testExpression() {		
		assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());		

	}

    @Test
    public void testFile() {
        //assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());

        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        TestUtils.parse(jmmParser);

    }

/*
    @Test
    public void testFile() {
        /*
        try {
            File myObj = new File("java/FindMaximum.jmm");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



        System.out.println("HELLOOOOOOOOOOO");

        try {
            Path fileName = Path.of("public/java/FindMaximum.jmm");
            String content  = "";
            Files.writeString(fileName, content);

            String actual = Files.readString(fileName);
            System.out.println(actual);
        }
        catch (Exception e){
            e.printStackTrace();
        }




        //assertEquals("Program", TestUtils.parse(actual).getRootNode().getKind());

    }
    */




    //   @Test
    //   public void testExpression() {
    //         //assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());
    //         assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());
    //      //assertEquals("While", TestUtils.parse("while(true)\n").getRootNode().getKind());
    //      //TestUtils.parse("while(true)\n");
    //      //System.out.println(TestUtils.parse("while(true)\n").getRootNode().getKind());
    //   }



    // @Test
    // public void testFindMaximum() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testGrammarFindMaximum() {
    //     String jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");

    //     assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());

    //     TestUtils.parse(jmmCode);
    //     //System.out.flush();
    // }

    // @Test
    // public void testHelloWorld() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // // @Test
    // // public void testLazySort() {
    // //   var jmmCode = SpecsIo.getResource("fixtures/public/LazySort.jmm");

    // //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // // }

    // @Test
    // public void testLife() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/Life.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testMonteCarloPi() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testQuickSort() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/QuickSort.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testSimple() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/Simple.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testTicTacToe() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testWhileAndIf() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

}
