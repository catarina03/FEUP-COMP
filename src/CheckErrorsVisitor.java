import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class CheckErrorsVisitor extends PreorderJmmVisitor<Analyser, Boolean> {
    public CheckErrorsVisitor() {
       // addVisit("Terminal", this::dealWithInt);
       // addVisit("Plus", this::dealWithPlus);
        setDefaultVisit(this::checkSemanticErrors);
    }

    private JmmNode getLowerSibling(JmmNode node) {
        JmmNode parent = node.getParent();
        for (int i = 0; i < parent.getNumChildren(); i++) {
            if (node.toString().equals(parent.getChildren().get(i).toString())){
                if (parent.getChildren().size() > i){
                    return parent.getChildren().get(i+1);
                }
            }
        }
        return null;
    }

    private JmmNode getUpperSibling(JmmNode node) {
        JmmNode parent = node.getParent();
        for (int i = 1; i < parent.getNumChildren(); i++) {
            if (node.toString().equals(parent.getChildren().get(i).toString())){
                return parent.getChildren().get(i-1);
            }
        }
        return null;
    }

    private Boolean dealWithPlus(JmmNode node, Analyser analyser){
        JmmNode leftChild = node.getChildren().get(0);
        JmmNode rightChild = node.getChildren().get(1);
        boolean leftboolean = visit(leftChild);
        System.out.println(node.get("Integer"));
        return false;
    }

    private Boolean dealWithInt(JmmNode node, Analyser analyser){
        System.out.println(node.get("Integer"));
        return false;
    }

    private Boolean checkSemanticErrors(JmmNode node, Analyser analyser){
        switch (node.getKind()){
            case "IntArrayVar":
                for (int i = 0; i < node.getChildren().size(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                            if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                if (node.getChildren().get(i).getChildren().get(j).getOptional("Integer").isEmpty()){
                                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array sizes must be integer"));
                                    break;
                                }
                            }
                        }
                    }
                }
                break;

            case "ArrayAccess":
                JmmNode arrayAccessUpperSibling = getUpperSibling(node);
                if (arrayAccessUpperSibling != null && arrayAccessUpperSibling.getKind().equals("ExpressionTerminal")){
                    for (int i = 0; i < arrayAccessUpperSibling.getNumChildren(); i++){
                        if (arrayAccessUpperSibling.getChildren().get(i).getKind().equals("Terminal")){
                            if (!arrayAccessUpperSibling.getChildren().get(i).getOptional("ID").isPresent()){
                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array access should be done on an array"));
                            }
                        }
                    }
                }

                for (int i = 0; i < node.getChildren().size(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                            if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                System.out.println(node.getChildren().get(i).getChildren().get(j));
                                if (node.getChildren().get(i).getChildren().get(j).getOptional("Integer").isEmpty()){
                                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array indices must be integer"));
                                }
                            }
                        }
                    }
                }
                break;

            case "This":
                JmmNode lowerSibling = this.getLowerSibling(node.getParent());
                if (lowerSibling.getKind().equals("DotMethodCall")){
                    ClassMethod method = analyser.getSymbolTable().getMethod(lowerSibling.get("DotMethodCall"));
                    if (method == null){
                        analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method does not exist"));
                        break;
                    }
                    int numberOfArgs = lowerSibling.getNumChildren();
                    List<Symbol> wantedParams = method.getMethodParameters();
                    if (wantedParams.size() != numberOfArgs){
                        analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call does not have the right number of arguments"));
                        break;
                    }
                    for (int i = 0; i < numberOfArgs; i++){
                        if (lowerSibling.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                            for (int j = 0; j < lowerSibling.getChildren().get(i).getNumChildren(); j++){
                                if (lowerSibling.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                    System.out.println(lowerSibling.getChildren().get(i).getChildren().get(j));
                                    System.out.println(method.getMethodParameters().get(i).getType());

                                    if (method.getMethodParameters().get(i).getType().getName().equals("int")){
                                        if (lowerSibling.getChildren().get(i).getChildren().get(j).getNumChildren() > 0){
                                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call requires Integer argument"));
                                        }
                                        else{
                                            if (!lowerSibling.getChildren().get(i).getChildren().get(j).getOptional("Integer").isPresent()){
                                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call requires Integer argument"));
                                            }
                                        }
                                    }
                                    else if (method.getMethodParameters().get(i).getType().getName().equals("boolean")){
                                        System.out.println("Needs boolean");
                                        System.out.println("This node: "+lowerSibling.getChildren().get(i).getChildren().get(j));
                                        System.out.println("No of children of node:"+lowerSibling.getChildren().get(i).getChildren().get(j).getNumChildren());
                                        if (lowerSibling.getChildren().get(i).getChildren().get(j).getNumChildren() == 0){
                                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call requires boolean argument"));
                                        }
                                        for (int k = 0; k < lowerSibling.getChildren().get(i).getChildren().get(j).getNumChildren(); k++){
                                            System.out.println(lowerSibling.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind());
                                            if (!lowerSibling.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanTrue") &&
                                                    !lowerSibling.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanFalse")){
                                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call requires boolean argument"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case "Plus":
            case "Minus":
            case "Mul":
            case "Div":
                JmmNode upperSibling = getUpperSibling(node);
                JmmNode ancestor = node.getAncestor("MethodDeclaration").get();
                String functionName = ancestor.get("functionName");
                ClassMethod method = analyser.getSymbolTable().getMethod(functionName);
                Symbol operandOne = null;
                Symbol operandTwo = null;

                if (upperSibling != null){
                    if (upperSibling.getKind().equals("ExpressionTerminal")){
                        if (upperSibling.getOptional("ID").isPresent()){
                            String operandOneName = upperSibling.getOptional("ID").get();
                            for (int i = 0; i <  method.getLocalVariables().size(); i++){
                                if (method.getLocalVariables().get(i).getName().equals(operandOneName)){
                                    operandOne = method.getLocalVariables().get(i);
                                }
                            }
                        }
                        else {
                            for (int j = 0; j < upperSibling.getNumChildren(); j++){
                                if (upperSibling.getChildren().get(j).getKind().equals("Terminal")){
                                    if (upperSibling.getChildren().get(j).getOptional("Integer").isPresent()){
                                        operandOne = new Symbol(new Type("int", false), "");
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < node.getNumChildren(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        if (node.getChildren().get(i).getOptional("ID").isPresent()) {
                            String operandTwoName = node.getChildren().get(i).getOptional("ID").get();
                            for (int j = 0; j < method.getLocalVariables().size(); j++) {
                                if (method.getLocalVariables().get(j).getName().equals(operandTwoName)) {
                                    operandTwo = method.getLocalVariables().get(j);
                                }
                            }
                        }
                        else {
                            for (int j = 0; j < node.getChildren().get(i).getNumChildren(); j++){
                                if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                    if (node.getChildren().get(i).getChildren().get(j).getOptional("Integer").isPresent()){
                                        operandTwo = new Symbol(new Type("int", false), "");
                                    }
                                }
                            }
                        }
                    }
                }

                if (operandOne != null && operandTwo != null && (operandOne.getType().isArray() || operandTwo.getType().isArray())){
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array cannot be used directly in arithmetic expressions"));
                    break;
                }
                else if (operandOne == null || operandTwo == null || !operandOne.getType().getName().equals("int") || !operandTwo.getType().getName().equals("int")){
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
                    break;
                }
                break;

            case "And":
                JmmNode andUpperSibling = getUpperSibling(node);
                JmmNode andAncestor = node.getAncestor("MethodDeclaration").get();
                String andFunctionName = andAncestor.get("functionName");
                ClassMethod andMethod = analyser.getSymbolTable().getMethod(andFunctionName);
                Symbol andOperandOne = null;
                Symbol andOperandTwo = null;
                if (andUpperSibling != null){
                    if (andUpperSibling.getKind().equals("ExpressionTerminal")){
                        if (andUpperSibling.getOptional("ID").isPresent()){
                            String andOperandOneName = andUpperSibling.getOptional("ID").get();
                            for (int i = 0; i <  andMethod.getLocalVariables().size(); i++){
                                if (andMethod.getLocalVariables().get(i).getName().equals(andOperandOneName)){
                                    andOperandOne = andMethod.getLocalVariables().get(i);
                                }
                            }
                        }
                        else {
                            for (int i = 0; i < andUpperSibling.getNumChildren(); i++){
                                if (andUpperSibling.getChildren().get(i).getKind().equals("Terminal")){
                                    for (int j = 0; j < andUpperSibling.getChildren().get(i).getNumChildren(); j++){
                                        if (andUpperSibling.getChildren().get(i).getChildren().get(j).getKind().equals("BooleanTrue")){
                                            andOperandOne = new Symbol(new Type("boolean", false), "");
                                        }
                                        else if (andUpperSibling.getChildren().get(i).getChildren().get(j).getKind().equals("BooleanFalse")){
                                            andOperandOne = new Symbol(new Type("boolean", false), "");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < node.getNumChildren(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        if ( node.getChildren().get(i).getOptional("ID").isPresent()){
                            String operandTwoName = node.getChildren().get(i).getOptional("ID").get();
                            for (int j = 0; j <  andMethod.getLocalVariables().size(); j++){
                                if (andMethod.getLocalVariables().get(j).getName().equals(operandTwoName)){
                                    andOperandTwo = andMethod.getLocalVariables().get(j);
                                }
                            }
                        }
                        else {
                            for (int j = 0; j < node.getChildren().get(i).getNumChildren(); j++){
                                if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                    for (int k = 0; k < node.getChildren().get(i).getChildren().get(j).getNumChildren(); k++){
                                        if (node.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanTrue")){
                                            andOperandTwo = new Symbol(new Type("boolean", false), "");
                                        }
                                        else if (node.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanFalse")){
                                            andOperandTwo = new Symbol(new Type("boolean", false), "");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!andOperandOne.getType().getName().equals("boolean") || !andOperandTwo.getType().getName().equals("boolean")){
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be boolean"));
                }
                break;

            case "Not":
                JmmNode notAncestor = node.getAncestor("MethodDeclaration").get();
                String notFunctionName = notAncestor.get("functionName");
                ClassMethod notMethod = analyser.getSymbolTable().getMethod(notFunctionName);
                Symbol notOperandOne = null;
                for (int i = 0; i < node.getNumChildren(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        if (node.getChildren().get(i).getOptional("ID").isPresent()){
                            String operandOneName = node.getChildren().get(i).getOptional("ID").get();
                            for (int j = 0; j <  notMethod.getLocalVariables().size(); j++){
                                if (notMethod.getLocalVariables().get(j).getName().equals(operandOneName)){
                                    notOperandOne = notMethod.getLocalVariables().get(j);
                                }
                            }
                        }
                        else {
                            for (int j = 0; j < node.getChildren().get(i).getNumChildren(); j++){
                                if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                    for (int k = 0; k < node.getChildren().get(i).getChildren().get(j).getNumChildren(); k++){
                                        if (node.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanTrue")){
                                            notOperandOne = new Symbol(new Type("boolean", false), "");
                                        }
                                        else if (node.getChildren().get(i).getChildren().get(j).getChildren().get(k).getKind().equals("BooleanFalse")){
                                            notOperandOne = new Symbol(new Type("boolean", false), "");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!notOperandOne.getType().getName().equals("boolean")){
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Operand must be boolean"));
                    break;
                }
                break;

                /*
            case "DotMethodCall":
                String dotMethodTarget = null;
                if (node.getParent().getKind().equals("IDStatement")){
                    if (node.getParent().getOptional("ID").isPresent()){
                        dotMethodTarget = node.getParent().getOptional("ID").get();
                        if ()
                        if (analyser.getSymbolTable().getSuper() == null){
                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Operand must be boolean"));
                            break;
                        }
                        else if (analyser.getSymbolTable().getImports().size() == 0){

                        }
                        else{

                        }
                    }
                }

                 */

            default:
                break;
        }
        return true;
    }
}
