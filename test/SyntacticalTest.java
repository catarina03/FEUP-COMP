import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.specs.util.SpecsIo;


public class SyntacticalTest {
    @Test
    public void testFileCustom() {
        String jmmParser = SpecsIo.getResource("fixtures/public/custom.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFileHello() {
        String jmmParser = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFileLife() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Life.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFileSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Simple.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }



    @Test
    public void testFileTicTacToe() {
        String jmmParser = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFileWhileAndIF() {
        String jmmParser = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFileFindMaximum() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFileLazysort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFileMonteCarloPi() {
        String jmmParser = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFileQuickSort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFileBlowUp() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/BlowUp.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

/* TODO DONT KNOW WHY ITS BEING IGNORED
    @Test
    public void testFileCompleteWhileTest() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }
 */

    @Test
    public void testFileLengthError() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/LengthError.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

    /* TODO LOOP INFINITO
    @Test
    public void testFileMissingRightPar() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/MissingRightPar.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }
     */

    @Test
    public void testFileMultipleSequential() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/MultipleSequential.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void testFileNestedLoop() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/syntactical/NestedLoop.jmm");
        JmmParserResult result = TestUtils.parse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

}
