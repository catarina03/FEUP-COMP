import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class SemanticTest {
    @Test
    public void semanticTestHello() {
        String jmmParser = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void semanticTestSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Simple.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    /*
    @Test
    public void semanticTestMonteCarloPi() {
        String jmmParser = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void semanticTestFindMaximum() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

     */
}
