import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;

/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class BackendStage implements JasminBackend {
    private static final String PARAM_DELIM1 = "(";
    private static final String PARAM_DELIM2 = ")";

    private StringBuilder jasminCode;
    private List<Report> reportList;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit ollirClass = ollirResult.getOllirClass();

        try {

            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code
            //String jasminCode = ""; // Convert node ...
            jasminCode = new StringBuilder();

            // More reports from this stage
            reportList = new ArrayList<>();

            // TODO: OllirClass needs to implements "getMethods()" with returns a List or Set with
            //    the methods present in the program at hand.
            for (var method : ollirClass.getMethods()) {
                generateMethodCode(method);
            }

            return new JasminResult(ollirResult, jasminCode.toString(), reportList);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }

    private void jasminType(Element element) {
        //TODO: Add more possible element types.
        switch (element.getType().getTypeOfElement()) {

            case BOOLEAN:
                jasminCode.append("\n\t\tb"); // TODO: Confirm if correct
                break;

            case INT32:
                jasminCode.append("\n\t\ti");
                break;

            case ARRAYREF:
                jasminCode.append("\n\t\ta");
                break;

            default:
                jasminCode.append("\n\t\t");
                break;
        }
    }

    private void generateMethodCode(Method method) {
        Map<String, Integer> listLocalVar = new TreeMap<>();

        System.out.printf("\tMETHOD NAME: %s\n", method.getMethodName().toUpperCase());

        jasminCode.append("\n\n.method " + method.getMethodAccessModifier().toString().toLowerCase());

        if (method.isConstructMethod()) {
            jasminCode.append("<init>");
        } else {
            if (method.isStaticMethod()) {
                jasminCode.append("static");
            }

            if (method.isFinalMethod()) {
                jasminCode.append(" final");
            }

            jasminCode.append(" " + method.getMethodName());
        }

        jasminCode.append(PARAM_DELIM1);

        if (method.getParams().size() > 0) {
            paramTypes(method,listLocalVar);
        }

        jasminCode.append(PARAM_DELIM2);

        //Identifies the type of element returned by the method at hand.
        identifyType(method.getReturnType().getTypeOfElement());

        if (!method.isConstructMethod()) {
            jasminCode.append("\n\t\t.limit locals 99\n\t\t.limit stack 99\n");
        }

        for (var instruct : method.getInstructions()) {
            generateInstructionCode(instruct, listLocalVar, method);
        }

        jasminCode.append("\n.end method");
    }

    private void paramTypes(Method method, Map<String, Integer> listLocalVar) {
        for (Element param : method.getParams()) {

            if (param.isLiteral()) {
                jasminCode.append("L");
            }

            identifyType(param.getType().getTypeOfElement());

            listLocalVar.put(((Operand) param).getName(), listLocalVar.size());
        }
    }

    private void identifyType(ElementType elementType) {
        switch (elementType) {

            case INT32:
                jasminCode.append("I");
                break;

            case BOOLEAN:
                jasminCode.append("Z");
                break;

            case ARRAYREF:
                jasminCode.append("[I");
                break;

            case OBJECTREF:
                jasminCode.append("OBJECTREF");
                break;

            case STRING:
                jasminCode.append("java/lang/String");
                break;

            case VOID:
                jasminCode.append("V");
        }
    }

    private void generateInstructionCode(Instruction instruct, Map<String, Integer> localVarList, Method method) {
        System.out.printf("\tINSTRUCTION NAME: %s\n", instruct.getInstType());

        instruct.show();

        switch (instruct.getInstType()) {

            case ASSIGN:
                generateAssignCode((AssignInstruction) instruct, localVarList);
                break;

            case CALL:
                generateCallCode((CallInstruction) instruct, localVarList, method);
                break;

            case RETURN:
                generateRetCode((ReturnInstruction) instruct, localVarList);
                break;
        }
    }

    private void generateAssignCode(AssignInstruction assignInst, Map<String, Integer> localVarList) {
        String var;
        String operand;
        Instruction instructRhs = assignInst.getRhs();

        switch (instructRhs.getInstType()) {

            case NOPER:
                Element elemRhs = ((SingleOpInstruction) instructRhs).getSingleOperand();

                var = ((Operand) assignInst.getDest()).getName();

                if (elemRhs.isLiteral()) {
                    operand = ((LiteralElement) elemRhs).getLiteral();

                    identifyOperandType(elemRhs.getType().getTypeOfElement());

                    jasminCode.append("const_");
                } else {
                    operand = localVarList.get(((Operand) elemRhs).getName()).toString();

                    identifyOperandType(elemRhs.getType().getTypeOfElement());

                    jasminCode.append("load_");
                }

                jasminCode.append(operand);

                identifyOperandType(elemRhs.getType().getTypeOfElement());

                jasminCode.append("store_");

                if (!localVarList.containsKey(var)) {

                    localVarList.put(var, localVarList.size());
                    jasminCode.append(localVarList.size());
                } else {

                    jasminCode.append(localVarList.get(var));
                }

                jasminCode.append("\n");

                break;

            case BINARYOPER:
                var = ((Operand) assignInst.getDest()).getName();
                operand = localVarList.get(var).toString();

                OperationType opType = ((BinaryOpInstruction) instructRhs).getUnaryOperation().getOpType();

                switch (opType) {

                    case ADD:

                        Element leftOperand = ((BinaryOpInstruction) instructRhs).getLeftOperand();
                        Element rightOperand = ((BinaryOpInstruction) instructRhs).getRightOperand();

                        identifyOperandType(leftOperand.getType().getTypeOfElement());

                        jasminCode.append("load_");
                        jasminCode.append(localVarList.get(((Operand) leftOperand).getName()));

                        identifyOperandType(assignInst.getDest().getType().getTypeOfElement());

                        jasminCode.append("add");

                        identifyOperandType(rightOperand.getType().getTypeOfElement());

                        jasminCode.append("load_");
                        jasminCode.append(localVarList.get(((Operand) rightOperand).getName()));

                        identifyOperandType(assignInst.getDest().getType().getTypeOfElement());

                        jasminCode.append("store_");
                        jasminCode.append(operand);

                        break;
                }

                jasminCode.append("\n");

                break;
        }
    }

    private void generateCallCode(CallInstruction callInstruct, Map<String, Integer> localVarList, Method method) {
        if (method.isConstructMethod()) {

            jasminCode.append("\n\taload_0\n\tinvokespecial java/lang/Object.<init>()V;");
        }

        switch (callInstruct.getReturnType().getTypeOfElement()) {

            default:
                jasminCode.append("\n\treturn");
                break;
        }
    }

    private void generateRetCode(ReturnInstruction retInstruct, Map<String, Integer> localVarList) {
        identifyOperandType(retInstruct.getOperand().getType().getTypeOfElement());

        jasminCode.append("load_");
        jasminCode.append(localVarList.get(((Operand) retInstruct.getOperand()).getName()).toString());

        identifyOperandType(retInstruct.getOperand().getType().getTypeOfElement());

        jasminCode.append("return");
    }

    private void identifyOperandType(ElementType typeElement) {
        switch (typeElement) {

            case INT32:
                jasminCode.append("\n\t\ti");
                break;

            case BOOLEAN:
                jasminCode.append("\n\t\tb"); //TODO: Confirm if correct.
                break;

            case ARRAYREF:
                jasminCode.append("\n\t\ta");
                break;

            default:
                jasminCode.append("\n\t\t");
                break;
        }
    }
}
