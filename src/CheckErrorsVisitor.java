import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class CheckErrorsVisitor extends PreorderJmmVisitor<Analyser, Boolean> {
    public CheckErrorsVisitor() {
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

    private Boolean checkSemanticErrors(JmmNode node, Analyser analyser){
        switch (node.getKind()){
            case "IntArrayVar":
                for (int i = 0; i < node.getChildren().size(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                            if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                for (String attribute : node.getChildren().get(i).getChildren().get(j).getAttributes()){
                                    if(!attribute.equals("Integer")){
                                        analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array sizes must be integer"));
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case "ArrayAccess":
                for (int i = 0; i < node.getChildren().size(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                            if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                for (String attribute : node.getChildren().get(i).getChildren().get(j).getAttributes()){
                                    if(!attribute.equals("Integer")){
                                        analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Array indices must be integer"));
                                    }
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
                JmmNode upperSibling = getUpperSibling(node);
                JmmNode ancestor = node.getAncestor("MethodDeclaration").get();
                String functionName = ancestor.get("functionName");
                ClassMethod method = analyser.getSymbolTable().getMethod(functionName);
                Symbol operandOne = null;
                Symbol operandTwo = null;
                if (upperSibling != null){
                    if (upperSibling.getKind().equals("ExpressionTerminal")){
                        String operandOneName = upperSibling.get("ID");
                        for (int i = 0; i <  method.getLocalVariables().size(); i++){
                            if (method.getLocalVariables().get(i).getName().equals(operandOneName)){
                                operandOne = method.getLocalVariables().get(i);
                            }
                        }
                    }
                }
                for (int i = 0; i < node.getNumChildren(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        String operandTwoName = node.getChildren().get(i).get("ID");
                        for (int j = 0; j <  method.getLocalVariables().size(); j++){
                            if (method.getLocalVariables().get(j).getName().equals(operandTwoName)){
                                operandTwo = method.getLocalVariables().get(i);
                            }
                        }
                    }
                }
                if (!operandOne.getType().equals(operandTwo.getType()) || operandOne == null || operandTwo == null){
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Operands must be of the same type"));
                }
                break;
            default:
                break;
        }
        return true;
    }
}
