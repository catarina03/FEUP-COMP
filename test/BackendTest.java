
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());


        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }


    @Test
    public void testPrintAddResult() {
        var ollirResult = TestUtils.optimize(SpecsIo.getResource("fixtures/public/helloWorld/printAddResult.jmm"));
        System.out.println(ollirResult.getSymbolTable());
        var result = TestUtils.backend(ollirResult);

        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testAssignLocalVar() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/assignLocalVar.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testArgument() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/testArgument.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testAssignField() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/assignField.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Result: 5", output.trim());
    }

    @Test
    public void testBooleanExpression() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/booleanExpression.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Result: ", output.trim());
    }

    @Test
    public void testArrayAccess() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/arrayaccess.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testCustom() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/custom.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testDotMethod() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/dotmethodstest.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }


    @Test
    public void testFindMaximum() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/FindMaximum.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testFindMaximumSimple() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/FindMaximumSimples.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testField() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/testField.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testIfNot() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/ifnottest.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testIf() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/iftest.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testLazySort() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/LazySort.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testLife() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Life.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testMonteCarloPi() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testQuickSort() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/QuickSort.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testSimple() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testTicTacToe() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/TicTacToe.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testWhileAndIf() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testWhile() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/whiletest.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testArithmeticMain() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/helloWorld/arithmeticMain.jmm"));
        System.out.println(result.getJasminCode());

        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Result: 65", output.trim());
    }



    // -------------------------- //
    // OLLIR FOLDER TESTS  //
    // ------------------------ //

    @Test
    public void testFac() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/Fac.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testFindMaximumOllir() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/FindMaximum.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testFindMaximumSimpleOllir() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/FindMaximumSimple.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testHelloWorldOllir() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/HelloWorld.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testMyClass1() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass1.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testMyClass2() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass2.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testMyClass3() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass3.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testMyClass4() {
        var ollirResult = new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass4.ollir"));
        var jasminResult = TestUtils.backend(ollirResult);
        System.out.println(jasminResult.getJasminCode());

        TestUtils.noErrors(jasminResult.getReports());

        var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
    }

}
