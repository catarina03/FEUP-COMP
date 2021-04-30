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
        addVisit("While", this::dealWithWhile);
        addVisit("And", this::dealWithAnd);
        addVisit("BooleanTrue", this::dealWithBoolean);
        addVisit("BooleanFalse", this::dealWithBoolean);
        addVisit("ExpressionTerminal", this::dealWithExpressionTerminal);
        //setDefaultVisit(this::checkSemanticErrors);

        //getDistantNode("MethodDeclaration");
    }

    private String dealWithWhile(JmmNode node, Analyser analyser){
        //System.out.println("1: "+node.getKind());
        System.out.println("Analyser: "+analyser + "for node: "+node);
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++){
            returnList.add(visit(node.getChildren().get(i)));
        }
        for (int i = 0; i < returnList.size(); i++){
            if (!returnList.get(i).equals("boolean")){
                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "While expression should result in a boolean"));
                return "Error";
            }
        }
        return "boolean";
    }

    private String dealWithAnd(JmmNode node, Analyser analyser){
        //System.out.println("2: "+node.getKind());
        System.out.println("Analyser: "+analyser + "for node: "+node);
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++){
            returnList.add(visit(node.getChildren().get(i)));
        }
        for (int i = 0; i < returnList.size(); i++){
            System.out.println("Return list: "+returnList.get(i));
            if (!returnList.get(i).equals("boolean")){
                System.out.println("ERROR");
                System.out.println(analyser);
                System.out.println(node);
                analyser.addReport(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "While expression should result in a boolean"));
                return "Error";
            }
        }
        return "boolean";
    }

    private String dealWithBoolean(JmmNode node, Analyser analyser){
        //System.out.println("3: "+node.getKind());
        System.out.println("Analyser: "+analyser + "for node: "+node);
        return "boolean";
    }

    private String dealWithExpressionTerminal(JmmNode node, Analyser analyser){
        //System.out.println("5: "+node.getKind());
        System.out.println("Analyser: "+analyser + "for node: "+node);
        if (node.getOptional("ID").isPresent()){
            String varName = node.getOptional("ID").get();
            if (getDistantNode(node,"MethodDeclaration").isPresent()){
                String methodName = getDistantNode(node,"MethodDeclaration").get().get("functionName");
                System.out.println("MethodName: "+methodName);
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
                    return "Error";
                }
            }
        }
        return "";
    }


    java.util.Optional<JmmNode> getDistantNode(JmmNode node, String kind) {
        //System.out.println("Analyser: "+analyser + "for node: "+node);
        if(node.getKind().equals(kind)){
            return java.util.Optional.of(node);
        }
        JmmNode currentParent = node.getParent();
        getDistantNode(currentParent, kind);
        return java.util.Optional.empty();
    }



    private String checkSemanticErrors(JmmNode node, Analyser analyser){
        switch (node.getKind()){
            case "Plus":
                break;
        }


        return "";
    }
}
