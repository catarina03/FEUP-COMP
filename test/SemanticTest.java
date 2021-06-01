import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class SemanticTest {

    @Test
    public void semanticTestArrIndexNotInt() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/arr_index_not_int.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void semanticTestArrSizeNotInt() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/arr_size_not_int.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void semanticTestBadArguments() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/badArguments.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }
/*
    @Test
    public void semanticTestBinopIncomp() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/binop_incomp.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);

        TestUtils.mustFail(result.getReports());
    }

 */

    @Test
    public void semanticTestFuncNotFound() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/funcNotFound.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

    /*
    @Test
    public void semanticTestSimpleLength() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/simple_length.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }



    @Test
    public void semanticTestVarExpIncomp() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/var_exp_incomp.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

     */

    /*
    @Test
    public void semanticTestVarLitIncomp() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/var_lit_incomp.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }


    @Test
    public void semanticTestVarUndef() {
        String jmmParser = SpecsIo.getResource("fixtures/public/fail/semantic/var_undef.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.mustFail(result.getReports());
    }

     */

    //pass
    @Test
    public void semanticTestSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Simple.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticArithmeticMain() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/arithmeticMain.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticAssignField() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/assignField.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticAssignLocalVar() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/assignLocalVar.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    /*
    //pass
    @Test
    public void semanticBooleanExpressions() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/booleanExpression.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }
    */

    //pass
    @Test
    public void semanticPrintAdd() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/printAdd.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticPrintAddResult() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/printAddResult.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semantictestArgument() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/testArgument.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticTestField() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/testField.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticTestMonteCarloPi() {
        String jmmParser = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticTestFindMaximum() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticTestFindMaximumSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximumSimple.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticTestHello() {
        String jmmParser = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    //pass
    @Test
    public void semanticDotMethodsTest() {
        String jmmParser = SpecsIo.getResource("fixtures/public/dotmethodstest.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    /*
    //pass
    @Test
    public void semanticCustom() {
        String jmmParser = SpecsIo.getResource("fixtures/public/custom.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }



    //pass
    @Test
    public void semanticArrayAccess() {
        String jmmParser = SpecsIo.getResource("fixtures/public/arrayaccess.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }



    //pass
    @Test
    public void testFileLife() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Life.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }



    //pass
    @Test
    public void testIfTest() {
        String jmmParser = SpecsIo.getResource("fixtures/public/iftest.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

     */

    /*
    //pass
    @Test
    public void testIfNotTest() {
        String jmmParser = SpecsIo.getResource("fixtures/public/ifnottest.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }


    //pass
    @Test
    public void testFileTicTacToe() {
        String jmmParser = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

     */

    //pass
    @Test
    public void testFileWhileAndIF() {
        String jmmParser = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    /*
    //pass
    @Test
    public void testWhileTest() {
        String jmmParser = SpecsIo.getResource("fixtures/public/whiletest.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

     */

    //pass
    @Test
    public void testFileLazysort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

    /*
    //pass
    @Test
    public void testFileQuickSort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        JmmSemanticsResult result = TestUtils.analyse(jmmParser);
        TestUtils.noErrors(result.getReports());
    }

     */
}
