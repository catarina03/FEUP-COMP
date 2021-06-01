import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmVisitor;
import pt.up.fe.comp.jmm.ast.PostorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpressionVisitor extends AJmmVisitor<Analyser, String> {
    private String aux1;
    private String aux2;
    private String aux3;
    public String code = "";
    public String conditionCode="";
    public int tempVarNum = 0;

    /* other utils */
    public List<Symbol> classFields;
    public List<Symbol> methodParameters;
    public List<Symbol> scopeVariables;
    public Analyser analyser;
    public String currentMethodName;

    public ExpressionVisitor(String currentMethodName) {
        this.currentMethodName = currentMethodName;
        setDefaultVisit(this::visit);
    }

    public String visit(JmmNode node, Analyser analyser){
        this.analyser = analyser;

        switch (node.getKind()){
            case "And":
                return dealWithAnd(node, analyser);
            case "ExpressionTerminal":
                return dealWithExpressionTerminal(node, analyser);
            case "Terminal":
                return dealWithTerminal(node, analyser);
            case "BooleanFalse":
                return dealWithFalse(node, analyser);
            case "BooleanTrue":
                return dealWithTrue(node, analyser);
            case "Less":
                return dealWithLess(node, analyser);
            case "Not":
                return dealWithNot(node, analyser);
            case "Plus":
                return dealWithPlus(node, analyser);
            case "Minus":
                return dealWithMinus(node, analyser);
            case "Mul":
                return dealWithMul(node, analyser);
            case "Div":
                return dealWithDiv(node, analyser);
            case "DotMethodCall":
                return dealWithDotMethodCall(node, analyser);
            default:
                return "; " + node.getKind() + " IS MISSING\n";
        }
    }

    private String dealWithNot(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);

        String leftExpression = "";
        if (left.equals("true")) {
            leftExpression = "1.bool";
        } else if (left.equals("false")) {
            leftExpression = "0.bool";
        } else {
            leftExpression = left + ".bool";
        }

        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t" + returnVar + ".bool :=.bool "+ leftExpression+ " !.bool " + leftExpression + ";\n";
        conditionCode += " " + leftExpression + " !.bool " + leftExpression + " ";
        return returnVar;
    }

    private String dealWithAnd(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String leftExpression = "";
        if (left.equals("true")){
            leftExpression = "1.bool";
        }
        else if(left.equals("false")){
            leftExpression = "0.bool";
        }
        else{
            leftExpression = left + ".bool";
        }

        String rightExpression = "";
        if (right.equals("true")){
            rightExpression = "1.bool";
        }
        else if(right.equals("false")){
            rightExpression = "0.bool";
        }
        else{
            rightExpression = right + ".bool";
        }

        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t"+returnVar+".bool :=.bool "+leftExpression+" &&.bool "+rightExpression+";\n";
        conditionCode += " "+ leftExpression+" &&.bool "+rightExpression + " ";
        return returnVar;
    }

    private String dealWithExpressionTerminal(JmmNode node, Analyser analyser) {
        if(node.getOptional("ID").isPresent()){
            return node.get("ID");  //FIXME: isto é para as continhas mas as continhas ainda não estão feitas a 100%
        }
        return visit(node.getChildren().get(0), analyser);
    }

    private String dealWithTerminal(JmmNode node, Analyser analyser){
        if(node.getOptional("Integer").isPresent()){
            return node.get("Integer");
        }
        return visit(node.getChildren().get(0), analyser);
      //  return visit(node.getChildren().get(0), analyser);
    }

    private String dealWithTrue(JmmNode node, Analyser analyser){
        return "true";
    }

    private String dealWithFalse(JmmNode node, Analyser analyser){
        return "false";
    }

    private String dealWithLess(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t" + returnVar + ".bool" + " :=.bool " + left + ".i32 <.bool " + right + ".i32;\n";
        conditionCode += " "+ left + ".i32 <.bool " + right + ".i32 ";
        return returnVar;
    }

    private String dealWithPlus(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String leftExpression = "";
        leftExpression = left + ".i32";


        String rightExpression = "";
        rightExpression = right + ".i32";


        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" +.i32 "+rightExpression+";\n";
        conditionCode += " "+ leftExpression+" +.i32 "+rightExpression + " ";
        return returnVar;
    }


    private String dealWithMinus(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String leftExpression = "";
        leftExpression = left + ".i32";


        String rightExpression = "";
        rightExpression = right + ".i32";


        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" -.i32 "+rightExpression+";\n";
        conditionCode += " "+ leftExpression+" -.i32 "+rightExpression + " ";
        return returnVar;
    }

    private String dealWithMul(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String leftExpression = "";
        leftExpression = left + ".i32";


        String rightExpression = "";
        rightExpression = right + ".i32";


        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" *.i32 "+rightExpression+";\n";
        conditionCode += " "+ leftExpression+" *.i32 "+rightExpression + " ";
        return returnVar;
    }

    private String dealWithDiv(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0), analyser);
        String right = visit(node.getChildren().get(1), analyser);

        String leftExpression = "";
        leftExpression = left + ".i32";


        String rightExpression = "";
        rightExpression = right + ".i32";


        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" /.i32 "+rightExpression+";\n";
        conditionCode += " "+ leftExpression+" /.i32 "+rightExpression + " ";
        return returnVar;
    }


    private String dealWithDotMethodCall(JmmNode node, Analyser analyser){
        String returnVar="";

        classFields = analyser.getSymbolTable().getFields();

        methodParameters = analyser.getSymbolTable().getParameters(currentMethodName);

        List<String> classFieldsNames = new ArrayList<>();
        if (classFields != null) {
            for (Symbol field : classFields) {
                classFieldsNames.add(field.getName());
            }
        }

        List<String> methodParameterNames = new ArrayList<>();
        if (methodParameters != null) {
            for (Symbol param : methodParameters) {
                methodParameterNames.add(param.getName());
            }
        }

        String auxCode = "";

        SymbolTableManager table = analyser.getSymbolTable(); //TODO: print aux code before condition and condition code inside condition

        // métodos DESTA CLASSE invokevirtual
        if (table.getMethods().contains(node.get("DotMethodCall"))){  //FIXME: COMO FICA O OLLIR QUANDO HÁ DOT METHODS EM CONDIÇÕES
            if (node.getNumChildren() == 0) { // sem argumentos
                auxCode += "\t\tinvokevirtual(" + node.getParent().get("ID") + "."
                        + OllirUtils.getType(getNodeType(node.getParent())) + ", \"" + node.get("DotMethodCall")
                        + "\").V;\n";
            } else {// com argumentos
                auxCode += "\t\tinvokevirtual(" + node.getParent().get("ID") + "."
                        + OllirUtils.getType(getNodeType(node.getParent())) + ", \"" + node.get("DotMethodCall") + "\"";
                for (int i = 0; i < node.getNumChildren(); i++) {
                    // expression terminal with ID
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getOptional("ID").isPresent()) {
                        if (methodParameterNames.contains(node.getChildren().get(i).get("ID"))) {
                            int index = methodParameterNames.indexOf(node.getChildren().get(i).get("ID"));
                            auxCode += ", $" + index + "." + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i)));

                        } else if (classFieldsNames.contains(node.getChildren().get(i).get("ID"))) { // FIXME: check if
                                                                                                     // correct compare
                                                                                                     // to similar
                                                                                                     // condition below
                            auxCode += ", getfield(this, " + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i))) + ")";
                        } else {
                            auxCode += ", " + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i)));
                        }
                    }
                    // expression terminal withoug ID and a terminal kid
                    else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getOptional("Integer").isPresent()) {
                        auxCode += ", " + node.getChildren().get(i).getChildren().get(0).get("Integer") + ".i32";
                    } else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getNumChildren() == 1) { // terminal kids
                                                                                                       // with boolean
                                                                                                       // kids
                        if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanTrue")) {
                            auxCode += ", 1.bool";
                        } else if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanFalse")) {
                            auxCode += ", 0.bool";
                        }
                    }
                }
                auxCode += ").V;\n";
            }

        } else { // metodos de OUTRAS CLASSES / length

            if (node.getNumChildren() == 0) { // sem argumentos
                if (node.get("DotMethodCall").equals("length")) {
                    auxCode += "\t\taux" + tempVarNum + ".i32 :=.i32 arraylength("
                            + getUpperSibling(node).get().get("ID") + ".array.i32).i32;\n";
                    auxCode += "\t\t" + node.getParent().get("ID") + ".i32 :=.i32 aux" + tempVarNum + ".i32;\n";
                    tempVarNum++;
                } else {
                    auxCode += "\t\tinvokestatic(" + node.getParent().get("ID") + ", \"" + node.get("DotMethodCall")
                            + "\").V;\n";
                }
            } else {// com argumentos
                auxCode += "\t\tinvokestatic(" + node.getParent().get("ID") + ", \"" + node.get("DotMethodCall") + "\"";
                for (int i = 0; i < node.getNumChildren(); i++) {
                    // expression terminal with ID
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getOptional("ID").isPresent()) {
                        if (methodParameterNames.contains(node.getChildren().get(i).get("ID"))) {
                            int index = methodParameterNames.indexOf(node.getChildren().get(i).get("ID"));
                            if (!node.getChildren().get(i).get("ID").equals("main")) { // Checks if static
                                index++;
                            }
                            auxCode += ", $" + index + "." + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i)));

                        } else if (classFieldsNames.contains(node.getChildren().get(i).get("ID"))) {
                            code += "\t\taux" + tempVarNum + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i))) + " :=."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i))) + " getfield(this, "
                                    + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i))) + ")."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i))) + ";\n";
                            auxCode += ", aux" + tempVarNum + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i)));
                            tempVarNum++;
                        } else {
                            auxCode += ", " + node.getChildren().get(i).get("ID") + "."
                                    + OllirUtils.getType(getNodeType(node.getChildren().get(i)));
                        }
                    }
                    // expression terminal withoug ID and a terminal kid
                    else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getOptional("Integer").isPresent()) {
                        auxCode += ", " + node.getChildren().get(i).getChildren().get(0).get("Integer") + ".i32";
                    } else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getNumChildren() == 1) { // terminal kids
                                                                                                       // with boolean
                                                                                                       // kids
                        if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanTrue")) {
                            auxCode += ", 1.bool";
                        } else if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanFalse")) {
                            auxCode += ", 0.bool";
                        }
                    }
                }
                auxCode += ").V;\n";
            }
        }
        code += auxCode;  
        return returnVar; 
    }



    public String getNodeType(JmmNode node) {
        String type;
        scopeVariables = analyser.getSymbolTable().getLocalVariables(node.get("functionName"));

        List<String> methodParametersNames = new ArrayList<>();
        List<String> scopeVariablesNames = new ArrayList<>();
        List<String> classFieldsNames = new ArrayList<>();

        if (methodParameters != null) {
            for (Symbol param : methodParameters) {
                methodParametersNames.add(param.getName());
            }
        }
        if (scopeVariables != null) {
            for (Symbol var : scopeVariables) {
                scopeVariablesNames.add(var.getName());
            }
        }
        if (classFields != null) {
            for (Symbol field : classFields) {
                classFieldsNames.add(field.getName());
            }
        }

        if (methodParametersNames.contains(node.get("ID"))) {
            int i = methodParametersNames.indexOf(node.get("ID"));
            type = methodParameters.get(i).getType().getName();
            if (methodParameters.get(i).getType().isArray()) {
                type += "[]";
            }
        } else if (scopeVariablesNames.contains(node.get("ID"))) {
            int i = scopeVariablesNames.indexOf(node.get("ID"));
            type = scopeVariables.get(i).getType().getName();
            if (scopeVariables.get(i).getType().isArray()) {
                type += "[]";
            }
        } else {
            int i = classFieldsNames.indexOf(node.get("ID"));
            type = classFields.get(i).getType().getName();
            if (classFields.get(i).getType().isArray()) {
                type += "[]";
            }
        }
        return type;
    }

    private Optional<JmmNode> getUpperSibling(JmmNode node) {
        JmmNode parent = node.getParent();
        for (int i = 1; i < parent.getNumChildren(); i++) {
            if (node.toString().equals(parent.getChildren().get(i).toString())) {
                return Optional.of(parent.getChildren().get(i - 1));
            }
        }
        return Optional.empty();
    }

}
