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
        addVisit("Condition", this::dealWithCondition);
        addVisit("Target", this::dealWithTarget);
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
                return "error";
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
            if (getDistantNode(node,"MethodDeclaration").isPresent()){ ;
                // GETS VAR SYMBOL
                boolean isMain = false;
                for (int ch = 0; ch < node.getNumChildren(); ch++){
                    if (getDistantNode(node,"MethodDeclaration").get().getChildren().get(ch).getKind().equals("Main")){
                        isMain = true;
                    }
                }
                boolean hasFunctionName = false;
                if (getDistantNode(node,"MethodDeclaration").get().getOptional("functionName").isPresent()){
                    hasFunctionName = true;
                }
                if (isMain || hasFunctionName){
                    String methodName = null;
                    if (hasFunctionName){
                        methodName = getDistantNode(node,"MethodDeclaration").get().get("functionName");
                    }
                    else if (isMain){
                        methodName = "main";
                    }
                    // CHECKS IF VAR IS A LOCAL VAR
                    ArrayList<Symbol> localVars = analyser.getSymbolTable().getMethod(methodName).getLocalVariables();
                    Symbol localVar = null;
                    for (int i = 0; i < localVars.size(); i++){
                        if (localVars.get(i).getName().equals(varName)){
                            localVar = localVars.get(i);
                        }
                    }
                    // CHECKS IF VAR IS PARAMETER
                    Symbol param = null;
                    for (Symbol par : analyser.getSymbolTable().getMethod(methodName).getMethodParameters()){
                        if (par.getName().equals(varName)){
                            param = par;
                        }
                    }

                    if (localVar != null){
                        return localVar.getType().getName();
                    }
                    else if (param != null){
                        return param.getType().getName();
                    }
                    else {
                        return "error";
                    }
                }
            }
        }
        else {
            return visit(node.getChildren().get(0), analyser);
        }
        return "unreachable";
    }

    private String dealWithLess(JmmNode node, Analyser analyser){
        // OPERAND 2
        boolean isDotMethod = false;
        for (int i = 0; i < node.getNumChildren(); i++){
            if (node.getChildren().get(i).getKind().equals("DotMethodCall")){
                isDotMethod = true;
                if(!visit(node.getChildren().get(i), analyser).equals("int")){
                    System.out.println("1 "+visit(node.getChildren().get(i), analyser));
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
                    return "error";
                }
            }
        }
        if (!isDotMethod){
            if(!visit(node.getChildren().get(0), analyser).equals("int")){
                System.out.println("2 "+visit(node.getChildren().get(0), analyser));
                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
                return "error";
            }
        }

        // OPERAND 1
        if(getUpperSibling(node).isPresent() && !visit(getUpperSibling(node).get(), analyser).equals("int")){
            System.out.println(getUpperSibling(node));
            System.out.println("3 "+visit(getUpperSibling(node).get(), analyser));
            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
            return "error";
        }
        //return "int";


        return "boolean";
    }

    private String dealWithIDStatement(JmmNode node, Analyser analyser){
        Optional<JmmNode> methodNode = getDistantNode(node, "MethodDeclaration");
        boolean isAssignment = false;
        for (int i = 0; i < node.getNumChildren(); i++){
            if (node.getChildren().get(i).getKind().equals("VarAssignment")){
                isAssignment = true;
            }
        }
        if (isAssignment){
            boolean isMain = false;
            for (int ch = 0; ch < methodNode.get().getNumChildren(); ch++){
                if (methodNode.get().getChildren().get(ch).getKind().equals("Main")){
                    isMain = true;
                }
            }
            boolean hasFunctionName = false;
            if (methodNode.get().getOptional("functionName").isPresent()){
                hasFunctionName = true;
            }
            if (methodNode.isPresent() && (isMain || hasFunctionName)){
                ClassMethod method = null;
                if (hasFunctionName){
                    method = analyser.getSymbolTable().getMethod(methodNode.get().get("functionName"));
                }
                else if (isMain){
                    method = analyser.getSymbolTable().getMethod("main");
                }
                Symbol assignmentVar = null;
                for (int j = 0; j < method.getLocalVariables().size(); j++){
                    if (method.getLocalVariables().get(j).getName().equals( node.get("ID"))){
                        assignmentVar = method.getLocalVariables().get(j);
                    }
                }
                if (assignmentVar != null){
                    boolean isDotMethod = false;
                    boolean hasDotMethod = false;
                    for (int i = 0; i < node.getNumChildren(); i++){
                        // FOR DOT METHODS
                        if (node.getChildren().get(i).getKind().equals("DotMethodCall")){
                            isDotMethod = true;
                            hasDotMethod = true;
                        }
                        if (isDotMethod){
                            String methodCallName = null;
                            if (node.getChildren().get(i).getOptional("DotMethodCall").isPresent()){
                                methodCallName = node.getChildren().get(i).getOptional("DotMethodCall").get();
                            }
                            ClassMethod methodCall = analyser.getSymbolTable().getMethod(methodCallName);
                            if (i-1 >= 0){
                                if(node.getChildren().get(i-1).getKind().equals("ExpressionTerminal")){
                                    if (node.getChildren().get(i-1).getOptional("ID").isPresent()){
                                        String target = node.getChildren().get(i-1).getOptional("ID").get();
                                        boolean isLocalVar = false;
                                        Symbol targetVar = null;
                                        for (Symbol localVar : analyser.getSymbolTable().getLocalVariables(method.getMethodName())){
                                            if (target.equals(localVar.getName())){
                                                targetVar = localVar;
                                                isLocalVar = true;
                                            }
                                        }
                                        boolean isParam = false;
                                        for (Symbol localVar : analyser.getSymbolTable().getParameters(method.getMethodName())){
                                            if (target.equals(localVar.getName())){
                                                targetVar = localVar;
                                                isParam = true;
                                            }
                                        }
                                        if (target.equals("this") || (targetVar != null && targetVar.getType().getName().equals(analyser.getSymbolTable().getClassName()))){
                                            if (methodCall == null && analyser.getSymbolTable().getClassExtends()){
                                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Class method " + methodCallName +"is not declared"));
                                                return "error";
                                            }
                                        }
                                        else {
                                            for (Symbol variable : analyser.getSymbolTable().getLocalVariables(methodCallName)){
                                                if (variable.getName().equals(target)){
                                                    boolean isPresent = false;
                                                    for (String importVar : analyser.getSymbolTable().getImports()){
                                                        if (variable.getType().getName().equals(importVar)){
                                                            isPresent = true;
                                                        }
                                                    }
                                                    if (variable.getType().getName().equals(analyser.getSymbolTable().getClassName())){
                                                        isPresent = true;
                                                    }
                                                    if (variable.getType().getName().equals(analyser.getSymbolTable().getSuper())){
                                                        isPresent = true;
                                                    }
                                                    if(!isPresent){
                                                        analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Target " + target +"is not present"));
                                                        return "error";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("1: "+visit(node.getChildren().get(i), analyser));
                                        if(visit(node.getChildren().get(i), analyser).equals("int") || visit(node.getChildren().get(i), analyser).equals("boolean")){
                                            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Invalid target"));
                                            return "error";
                                        }
                                    }
                                }
                                else {
                                    System.out.println("2 exp not terminal: "+node.getChildren().get(i));
                                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Invalid target"));
                                    return "error";
                                }
                            }
                            /*
                            System.out.println("NODE: "+node);
                            System.out.println("CHILDREN: "+node.getChildren().get(i));
                            String visitResult = visit(node.getChildren().get(i), analyser);
                            if (!visitResult.equals(assignmentVar.getType().getName())){
                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Incompatible types"));
                                return "error";
                            }

                             */
                            isDotMethod = false;
                        }


                    }

                    if (!hasDotMethod){
                        List<String> returnList = new ArrayList<>();
                        for (int i = 0; i < node.getNumChildren(); i++){
                            if (!node.getChildren().get(i).getKind().equals("VarAssignment")){
                                returnList.add(visit(node.getChildren().get(i), analyser));
                            }
                        }
                        for (int i = 0; i < returnList.size(); i++){
                            if (!assignmentVar.getType().isArray()){
                                if (returnList.get(i).equals("this") || returnList.get(i).equals(analyser.getSymbolTable().getClassName())){
                                    continue;
                                }
                                //imports
                                if (returnList.get(i).equals("unknown")){
                                    if (this.getUpperSibling(node).isPresent()){
                                        if(visit(this.getUpperSibling(node).get(), analyser).equals("this")){
                                            if(!analyser.getSymbolTable().getClassExtends()){
                                                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Undeclared function"));
                                            }
                                        }
                                        else {
                                            boolean importExists = false;
                                            for (int j = 0; j < analyser.getSymbolTable().getImports().size(); j++){
                                                if (visit(this.getUpperSibling(node).get(), analyser).equals(analyser.getSymbolTable().getImports().get(j))){
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
        // OPERAND 2
        boolean isDotMethod = false;
        for (int i = 0; i < node.getNumChildren(); i++){
            if (node.getChildren().get(i).getKind().equals("DotMethodCall")){
                isDotMethod = true;
                if(!visit(node.getChildren().get(i), analyser).equals("int")){
                    System.out.println("1 "+visit(node.getChildren().get(i), analyser));
                    analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
                    return "error";
                }
            }
        }
        if (!isDotMethod){
            if(!visit(node.getChildren().get(0), analyser).equals("int")){
                System.out.println("2 "+visit(node.getChildren().get(0), analyser));
                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
                return "error";
            }
        }

        // OPERAND 1
        if(getUpperSibling(node).isPresent() && !visit(getUpperSibling(node).get(), analyser).equals("int")){
            System.out.println(getUpperSibling(node));
            System.out.println("3 "+visit(getUpperSibling(node).get(), analyser));
            analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Both operands must be integers"));
            return "error";
        }
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

    private String dealWithCondition(JmmNode node, Analyser analyser){
        return "boolean";
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

    private String dealWithTarget(JmmNode node, Analyser analyser){
       // if (node.ge)
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


}
