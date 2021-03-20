import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.io.StringReader;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;



public class ExampleTest {

/*
    @Test
    public void testFileHello() {

        String jmmParser = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);
    }


    @Test
    public void testFileLife() {

        String jmmParser = SpecsIo.getResource("fixtures/public/Life.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }




    @Test
    public void testFileSimple() {

        String jmmParser = SpecsIo.getResource("fixtures/public/Simple.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }



    @Test
    public void testFileTicTacToe() {

        String jmmParser = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }


    @Test
    public void testFileWhileAndIF() {

        String jmmParser = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }


    @Test
    public void testFileFindMaximum() {

        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }

    @Test
    public void testFileFindLazysort() {

        String jmmParser = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }

    @Test
    public void testFileMonteCarloPi() {

        String jmmParser = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }

    @Test
    public void testFileQuickSort() {

        String jmmParser = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }

    @Test
    public void testFileBlowUp() {

        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/BlowUp.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }
    */

    @Test
    public void testFileCompleteWhileTest() {

        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        System.out.println("FILE HERE ---------------");
        System.out.println(jmmParser);

        System.out.println("PARSE TREE HERE ---------------");
        TestUtils.parse(jmmParser);

    }


}
