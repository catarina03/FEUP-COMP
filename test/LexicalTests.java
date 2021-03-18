package test;

import static org.junit.Assert.assertEquals;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

import java.text.ParseException; 


public class LexicalTests {

    @Test
    public void testFindMaximum() {
        String jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testGrammarFindMaximum() {
        String jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());

        TestUtils.parse(jmmCode);
        System.out.flush();
    }

    @Test
    public void testHelloWorld() {
        var jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    // @Test
    // public void testLazySort() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/LazySort.jmm");

    //     assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    @Test
    public void testLife() {
        var jmmCode = SpecsIo.getResource("fixtures/public/Life.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testMonteCarloPi() {
        var jmmCode = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testQuickSort() {
        var jmmCode = SpecsIo.getResource("fixtures/public/QuickSort.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testSimple() {
        var jmmCode = SpecsIo.getResource("fixtures/public/Simple.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testTicTacToe() {
        var jmmCode = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

    @Test
    public void testWhileAndIf() {
        var jmmCode = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");

        assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    }

//     /*
//      * Tests below this comment will fail
//      */

     
    // @Rule
    // public final ExpectedException expectedException = ExpectedException.none();


    // @Test(expected = RuntimeException.class)
    // public void testBlowUp() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/BlowUp.jmm");

    //     TestUtils.parse(jmmCode).getRootNode().getKind();

    //     expectedException.expect(ParseException.class);
    // }

    // @Test(expected = RuntimeException.class)
    // public void testCompleteWhileTest() {

    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
    
    //     TestUtils.parse(jmmCode).getRootNode().getKind();
    
    //     expectedException.expect(ParseException.class);

    // }

    // @Test(expected = RuntimeException.class)
    // public void testLengthError() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/LengthError.jmm");

    //     TestUtils.parse(jmmCode).getRootNode().getKind();

    //     expectedException.expect(ParseException.class);
    // }

    // @Test(expected = RuntimeException.class)
    // public void testMissingRightPar() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/MissingRightPar.jmm");

    //     TestUtils.parse(jmmCode).getRootNode().getKind();

    //     expectedException.expect(ParseException.class);
    // }

    // @Test(expected = RuntimeException.class)
    // public void testMultipleSequential() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/MultipleSequential.jmm");

    //     TestUtils.parse(jmmCode).getRootNode().getKind();

    //     expectedException.expect(ParseException.class);
    // }

    // @Test(expected = RuntimeException.class)
    // public void testNestedLoop() {
    //     var jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/NestedLoop.jmm");

    //     TestUtils.parse(jmmCode).getRootNode().getKind();

    //     expectedException.expect(ParseException.class);
    // }
    
 }
