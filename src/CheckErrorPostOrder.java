import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PostorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckErrorPostOrder extends PostorderJmmVisitor<Analyser, String> {
    public CheckErrorPostOrder() {
        addVisit("While", this::dealWithWhileIf);
        addVisit("If", this::dealWithWhileIf);
        addVisit("And", this::dealWithAnd);
        addVisit("BooleanTrue", this::dealWithBoolean);
        addVisit("BooleanFalse", this::dealWithBoolean);
        addVisit("ExpressionTerminal", this::dealWithExpressionTerminal);
        addVisit("Terminal", this::dealWithTerminal);
        addVisit("Less", this::dealWithLess);
        addVisit("Plus", this::dealWithArithmeticOp);
        addVisit("Minus", this::dealWithArithmeticOp);
        addVisit("Mul", this::dealWithArithmeticOp);
        addVisit("Div", this::dealWithArithmeticOp);
        addVisit("IDstatement", this::dealWithIDStatement);
        addVisit("IntArrayVar", this::dealWithIntArray);
        addVisit("New", this::dealWithNew);
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("DotMethodCall", this::dealWithDotMethod);
        addVisit("TypeObject", this::dealWithTypeObject);
        addVisit("This", this::dealWithThis);
        //setDefaultVisit(this::checkSemanticErrors);

        //getDistantNode("MethodDeclaration");
    }

    private String dealWithWhileIf(JmmNode node, Analyser analyser){
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++){
            returnList.add(visit(node.getChildren().get(i), analyser));
        }
        for (int i = 0; i < returnList.size(); i++){
            if (returnList.get(i) == null || !returnList.get(i).equals("boolean")){
                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), node.getKind() +" expression should result in a boolean"));
                return "Error";
            }
        }
        return "boolean";
    }

    private String dealWithAnd(JmmNode node, Analyser analyser){
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++){
            returnList.add(visit(node.getChildren().get(i), analyser));
        }
        for (int i = 0; i < returnList.size(); i++){
            if (returnList.get(i) != null && !returnList.get(i).equals("boolean")){
                //analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "While expression should result in a boolean"));
                return "error";
            }
        }
        return "boolean";
    }

    private String dealWithBoolean(JmmNode node, Analyser analyser){
        return "boolean";
    }

    private String dealWithTerminal(JmmNode node, Analyser analyser){
        if (node.getOptional("Integer").isPresent()){
            return "int";
        }
        else {
            return visit(node.getChildren().get(0), analyser);
        }
    }

    private String dealWithExpressionTerminal(JmmNode node, Analyser analyser){
        if (node.getOptional("ID").isPresent()){
            String varName = node.getOptional("ID").get();
            if (getDistantNode(node,"MethodDeclaration").isPresent()){
                String methodName = getDistantNode(node,"MethodDeclaration").get().get("functionName");
                ArrayList<Symbol> localVars = analyser.getSymbolTable().getMethod(methodName).getLocalVariables();
                Symbol localVar = null;
                for (int i = 0; i < localVars.size(); i++){
                    if (localVars.get(i).getName().equals(varName)){
                        localVar = localVars.get(i);
                    }
                }
                if (localVar != null){
                    return localVar.getType().getName();
                }
                else {
                    return "error";
                }
            }
        }
        else {
            return visit(node.getChildren().get(0), analyser);
        }
        return "unreachable";
    }

    private String dealWithLess(JmmNode node, Analyser analyser){
        return "boolean";
    }

    private String dealWithIDStatement(JmmNode node, Analyser analyser){
        System.out.println("HELOOOOO: "+node);
        Optional<JmmNode> methodNode = getDistantNode(node, "MethodDeclaration");
        boolean isAssignment = false;
        for (int i = 0; i < node.getNumChildren(); i++){
            if (node.getChildren().get(i).getKind().equals("VarAssignment")){
                isAssignment = true;
            }
        }
        if (isAssignment){
            if (methodNode.isPresent() && !methodNode.get().getOptional("functionName").isEmpty()){
                ClassMethod method = analyser.getSymbolTable().getMethod(methodNode.get().get("functionName"));
                Symbol assignmentVar = null;
                for (int j = 0; j < method.getLocalVariables().size(); j++){
                    if (method.getLocalVariables().get(j).getName().equals( node.get("ID"))){
                        assignmentVar = method.getLocalVariables().get(j);
                    }
                }
                System.out.println("LOCAL VAR? " + assignmentVar);
                if (assignmentVar != null){
                    List<String> returnList = new ArrayList<>();
                    for (int i = 0; i < node.getNumChildren(); i++){
                        if (!node.getChildren().get(i).getKind().equals("VarAssignment")){
                            returnList.add(visit(node.getChildren().get(i), analyser));
                        }
                    }
                    for (int i = 0; i < returnList.size(); i++){
                        System.out.println("RETURN LIST TYPE: " + returnList.get(i));
                        System.out.println("ASSIGMENT TYPE: " + assignmentVar.getType().getName());
                        if (!assignmentVar.getType().isArray()){
                            if (returnList.get(i).equals("this")){
                                continue;
                            }
                            //imports
                            if (returnList.get(i).equals("unknown")){
                                if (this.getUpperSibling(node).isPresent()){
                                    if(visit(this.getUpperSibling(node).get()).equals("this")){
                                        if(!analyser.getSymbolTable().getClassExtends()){
                                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Undeclared function"));
                                        }
                                    }
                                    else {
                                        boolean importExists = false;
                                        for (int j = 0; j < analyser.getSymbolTable().getImports().size(); j++){
                                            if (visit(this.getUpperSibling(node).get()).equals(analyser.getSymbolTable().getImports().get(j))){
                                                importExists = true;
                                            }
                                        }
                                        if (!importExists){
                                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Function target not recognized"));
                                        }
                                    }
                                }
                                continue;
                            }
                            if (!returnList.get(i).equals(assignmentVar.getType().getName())){
                                System.out.println("HEREEEEEEEEEEEEEE");
                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Incompatible types"));
                                return "error";
                            }
                        }
                        else {
                            if (!returnList.get(i).equals("intArray")){
                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Incompatible types"));
                                return "error";
                            }
                        }

                    }
                    return assignmentVar.getType().getName(); //TODO KINDA WRONG BUT KINDA USELESS
                }
                else {
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Variable not initialized"));
                    return "error";
                }
            }
        }
        return "unreacheable";
    }

    private String dealWithArithmeticOp(JmmNode node, Analyser analyser){
        return "int";
    }

    private String dealWithIntArray(JmmNode node, Analyser analyser){
        return "intArray";
    }

    private String dealWithNew(JmmNode node, Analyser analyser){
        return visit(node.getChildren().get(0), analyser);
    }

    private String dealWithArrayAccess(JmmNode node, Analyser analyser){
        return "int";
    }

    private String dealWithDotMethod(JmmNode node, Analyser analyser){
        if (node.getOptional("DotMethodCall").isPresent()){
            if (node.get("DotMethodCall").equals("length")){
                return "int";
            }
            else {
                ClassMethod method = analyser.getSymbolTable().getMethod(node.get("DotMethodCall"));
                if(method != null){ //IF NULL NO NEED TO WORRY, WE ASSUME ITS RIGHT
                    String returnType = method.getReturnType().getName();
                   // if (returnType.equals("Integer"))
                    return returnType;
                }
                else {
                    return "unkown";
                }
               // this.getDistantNode(node, "MethodDeclaration");
            }
        }
        return "unreachable";
    }

    private String dealWithTypeObject(JmmNode node, Analyser analyser){
        if (node.getOptional("Object").isPresent()){
            return node.get("Object");
        }
        return "unreachable";
    }

    private String dealWithThis(JmmNode node, Analyser analyser){
        return "this";
    }


    private java.util.Optional<JmmNode> getDistantNode(JmmNode node, String kind) {
        if(node.getKind().equals(kind)){
            return java.util.Optional.of(node);
        }
        JmmNode currentParent = node.getParent();
        Optional<JmmNode> parent = getDistantNode(currentParent, kind);
        if (parent.isEmpty()){
            return java.util.Optional.empty();
        }
        else {
            return parent;
        }
    }

    private Optional<JmmNode> getUpperSibling(JmmNode node) {
        JmmNode parent = node.getParent();
        for (int i = 1; i < parent.getNumChildren(); i++) {
            if (node.toString().equals(parent.getChildren().get(i).toString())){
                return Optional.of(parent.getChildren().get(i-1));
            }
        }
        return Optional.empty();
    }



    private String checkSemanticErrors(JmmNode node, Analyser analyser){
        switch (node.getKind()){
            case "Plus":
                break;
        }


        return "";
    }
}
