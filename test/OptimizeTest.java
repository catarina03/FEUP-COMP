
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;


public class OptimizeTest {
    /*

    @Test
    public void testHelloWorld() {

        String jmmParser = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testHelloWorldArguments() {

        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/testField.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }
    
    @Test
    public void testIf() {
        String jmmParser = SpecsIo.getResource("fixtures/public/iftest.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testIfNot() {
        String jmmParser = SpecsIo.getResource("fixtures/public/ifnottest.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testDotMethods() {
        String jmmParser = SpecsIo.getResource("fixtures/public/dotmethodstest.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testWhile() {
        String jmmParser = SpecsIo.getResource("fixtures/public/whiletest.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testArrayAccess() {
        String jmmParser = SpecsIo.getResource("fixtures/public/arrayaccess.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testCustom() {
        String jmmParser = SpecsIo.getResource("fixtures/public/custom.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFindMaximum() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testFindMaximumSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/FindMaximumSimple.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testLazySort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testLife() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Life.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testMonteCarloPi() {
        String jmmParser = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testQuickSort() {
        String jmmParser = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testSimple() {
        String jmmParser = SpecsIo.getResource("fixtures/public/Simple.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testTicTacToe() {
        String jmmParser = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testWhileAndIf() {
        String jmmParser = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testField() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/testField.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testAssignField() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/assignField.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }


    @Test
    public void testPrintAddResult() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/printAddResult.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testAssignLocalVar() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/assignLocalVar.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testArgument() {
        String jmmParser = SpecsIo.getResource("fixtures/public/helloWorld/testArgument.jmm");
        JmmSemanticsResult semanticsResult = TestUtils.analyse(jmmParser);

        var result = TestUtils.optimize(semanticsResult, false);
        TestUtils.noErrors(result.getReports());
    }

    */
}
