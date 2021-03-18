import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.io.StringReader;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

import pt.up.fe.comp.TestUtils;

public class ExampleTest {


  @Test
  public void testExpression() {		
		//assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());		
    //assertEquals("While", TestUtils.parse("while(true)\n").getRootNode().getKind());	
    TestUtils.parse("while(true)\n");	
    System.out.println(TestUtils.parse("while(true)\n").getRootNode().getKind());
	}



    // @Test
    // public void testFindMaximum() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testHelloWorld() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

    // @Test
    // public void testLazySort() {
    //   var jmmCode = SpecsIo.getResource("fixtures/public/LazySort.jmm");

    //   assertEquals("Program", TestUtils.parse(jmmCode).getRootNode().getKind());
    // }

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
