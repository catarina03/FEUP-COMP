import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpressionVisitor extends AJmmVisitor<Analyser, String> {
    public String returnVar;
    public String code = "";
    public String conditionCode="";
    public String auxConditionCode="";
    public int tempVarNum = 0;

    /* other utils */
    public List<Symbol> classFields;
    public List<Symbol> methodParameters;
    public List<Symbol> scopeVariables;
    public Analyser analyser;
    public String currentMethodName;
    public boolean dotMethodCondition=false;
    public int LevelVisit = 0;

    public ExpressionVisitor(String currentMethodName) {
        this.currentMethodName = currentMethodName;
        //this.returnVar = returnVar;
        setDefaultVisit(this::visit);
    }

    public String visit(JmmNode node, Analyser analyser){
        this.analyser = analyser;
        LevelVisit++;
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

        if(LevelVisit == 1){
            returnVar = this.returnVar;
        }

        code += "\t\t" + returnVar + ".bool :=.bool "+ leftExpression+ " !.bool " + leftExpression + ";\n";
        if (LevelVisit != 1)
            auxConditionCode += code;
        conditionCode = " " + leftExpression + " !.bool " + leftExpression + " ";
        LevelVisit--;
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

        if(LevelVisit == 1){
            returnVar = this.returnVar;
        }
        code += "\t\t"+returnVar+".bool :=.bool "+leftExpression+" &&.bool "+rightExpression+";\n";
        if (LevelVisit != 1)
            auxConditionCode += code;
        conditionCode = " "+ leftExpression+" &&.bool "+rightExpression + " ";
        LevelVisit--;
        return returnVar;
    }

    private String dealWithExpressionTerminal(JmmNode node, Analyser analyser) {
        
        if(getNextSibling(node).isPresent() && (getNextSibling(node).get().getKind().equals("Less") || getNextSibling(
                node).get().getKind().equals("Plus") || getNextSibling(node).get().getKind()
                        .equals("Minus") || getNextSibling(node).get().getKind().equals("Div") || getNextSibling(node).get().getKind()
                        .equals("Mul")) && !dotMethodCondition && LevelVisit<=1){  //FIXME: verificar se acontece também para outros operadores (and) na presença de dot methods do lado direito
            dotMethodCondition=true;
            visit(getNextSibling(node).get(), analyser);
        }

        if(node.getOptional("ID").isPresent()){
            LevelVisit--;
            return node.get("ID"); 
        }
        LevelVisit--;
        return visit(node.getChildren().get(0), analyser);
    }

    private String dealWithTerminal(JmmNode node, Analyser analyser){
        if(node.getOptional("Integer").isPresent()){
            LevelVisit--;
            return node.get("Integer");
        }
        LevelVisit--;
        return visit(node.getChildren().get(0), analyser);
      //  return visit(node.getChildren().get(0), analyser);
    }

    private String dealWithTrue(JmmNode node, Analyser analyser){
        LevelVisit--;
        return "true";
    }

    private String dealWithFalse(JmmNode node, Analyser analyser){
        LevelVisit--;
        return "false";
    }

    private String dealWithLess(JmmNode node, Analyser analyser){
        String returnVar="";
        if(!node.getChildren().get(1).getKind().equals("DotMethodCall")){
            String left = visit(node.getChildren().get(0), analyser);
            String right = visit(node.getChildren().get(1), analyser);

            returnVar = "aux" + tempVarNum;
            tempVarNum++;

            if(LevelVisit == 1){
                returnVar = this.returnVar;
            }
            
            code += "\t\t" + returnVar + ".bool" + " :=.bool " + left + ".i32 <.bool " + right + ".i32;\n";
            if(LevelVisit!=1) auxConditionCode += code;
            conditionCode = " "+ left + ".i32 <.bool " + right + ".i32 ";
        }else{
            String left = visit(getUpperSibling(node).get(), analyser);
            String right = visit(node.getChildren().get(1), analyser);

            returnVar = "aux" + tempVarNum;
            tempVarNum++;

            if(LevelVisit == 1){
                returnVar = this.returnVar;
            }

            //TODO: do we need auxConditionCode here??
            code += "\t\t" + returnVar + ".bool" + " :=.bool " + left + ".i32 <.bool " + right + ".i32;\n";
            conditionCode = " " + left + ".i32 <.bool " + right + ".i32 ";
        }
        LevelVisit--;
        return returnVar;
    }

    private String dealWithPlus(JmmNode node, Analyser analyser){
        String returnVar="";
        if(!node.getChildren().get(1).getKind().equals("DotMethodCall")){
            String left = visit(node.getChildren().get(0), analyser);
            String right = visit(node.getChildren().get(1), analyser);

            String leftExpression = "";
            leftExpression = left + ".i32";


            String rightExpression = "";
            rightExpression = right + ".i32";


            returnVar = "aux" + tempVarNum;
            tempVarNum++;

            if(LevelVisit == 1){
                returnVar = this.returnVar;
            }

            code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" +.i32 "+rightExpression+";\n";
            if (LevelVisit != 1)
                auxConditionCode += code;
            conditionCode = " "+ leftExpression+" +.i32 "+rightExpression + " ";
           
        }else{
            String left = visit(getUpperSibling(node).get(), analyser);
            String right = visit(node.getChildren().get(1), analyser);

            String leftExpression = "";
            leftExpression = left + ".i32";

            String rightExpression = "";
            rightExpression = right + ".i32";

            returnVar = "aux" + tempVarNum;
            tempVarNum++;

            if(LevelVisit == 1){
            returnVar = this.returnVar;
            }

            code += "\t\t" + returnVar + ".i32 :=.i32 " + leftExpression + " +.i32 " + rightExpression + ";\n";
            if (LevelVisit != 1)
                auxConditionCode += code;
            conditionCode = " " + leftExpression + " +.i32 " + rightExpression + " ";
        }
        LevelVisit--;
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

        if(LevelVisit == 1){
            returnVar = this.returnVar;
        }

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" -.i32 "+rightExpression+";\n";
        if (LevelVisit != 1)
            auxConditionCode += code;        
        conditionCode = " "+ leftExpression+" -.i32 "+rightExpression + " ";
        LevelVisit--;
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

        if(LevelVisit == 1){
            returnVar = this.returnVar;
        }

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" *.i32 "+rightExpression+";\n";
        if (LevelVisit != 1)
            auxConditionCode += code;
        conditionCode = " "+ leftExpression+" *.i32 "+rightExpression + " ";
        LevelVisit--;
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

        if(LevelVisit == 1){
            returnVar = this.returnVar;
        }

        code += "\t\t"+returnVar+".i32 :=.i32 "+leftExpression+" /.i32 "+rightExpression+";\n";
        if (LevelVisit != 1)
            auxConditionCode += code;
        conditionCode = " "+ leftExpression+" /.i32 "+rightExpression + " ";
        LevelVisit--;
        return returnVar;
    }

    // TODO: ADAPT TOTALLY TO CONDITIONS, STILL HAS CODE FROM OLLIR PRODUCER
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

        SymbolTableManager table = analyser.getSymbolTable(); 

        // métodos DESTA CLASSE invokevirtual
        if (table.getMethods().contains(node.get("DotMethodCall"))){  
            if (node.getNumChildren() == 0) { // sem argumentos 
                String dotMethodReturn = OllirUtils.getType(table.getMethod(node.get("DotMethodCall")).getReturnType().getName());
                //if not this
                if(getUpperSibling(node).get().getOptional("ID").isPresent()){
                    auxCode += "\t\taux"+tempVarNum+"."+ dotMethodReturn +" :=."+dotMethodReturn+" invokevirtual(" + getUpperSibling(node).get().get("ID") + "."
                        + OllirUtils.getType(getNodeType(
                                getUpperSibling(node).get())) + ", \"" + node.get("DotMethodCall")
                        + "\")."+OllirUtils.getType(getNodeType(
                                getUpperSibling(node).get()))+";\n";

                //if this
                }else{
                    auxCode += "\t\taux" + tempVarNum + "." + dotMethodReturn + " :=." + dotMethodReturn + " invokevirtual("
                            + "this, \""
                            + node.get("DotMethodCall") + "\")."
                            + dotMethodReturn + ";\n";
                }
                returnVar = "aux" + tempVarNum;
                tempVarNum++;
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
                        } else if (getNextSibling(node.getChildren().get(i)).isPresent()
                                && getNextSibling(node.getChildren().get(i)).get().getKind().equals("ArrayAccess")) { // array
                                                                                                                      // access->int
                                                                                                                      // arrays
                            code += "\t\taux" + tempVarNum + ".i32 :=.i32 " + node.getChildren().get(i).get("ID") + "["
                                    + node.getChildren().get(i + 1).getChildren().get(0).get("ID") + ".i32].i32;\n";

                            auxCode += ", aux" + tempVarNum + ".i32";
                            tempVarNum++;
                            i++; // ignore the next kid cause its array access
                        
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
                    auxConditionCode += "\t\taux" + tempVarNum + ".i32 :=.i32 arraylength("
                            + getUpperSibling(node).get().get("ID") + ".array.i32).i32;\n";
                    returnVar = "aux"+ tempVarNum;
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

                        } else if (getNextSibling(node.getChildren().get(i)).isPresent()
                            && getNextSibling(node.getChildren().get(i)).get().getKind().equals("ArrayAccess")) { // array
                                                                                                                      // access->int
                                                                                                                      // arrays
                            code += "\t\taux" + tempVarNum + ".i32 :=.i32 " + node.getChildren().get(i).get("ID") + "["
                                    + node.getChildren().get(i + 1).getChildren().get(0).get("ID") + ".i32].i32;\n";

                            auxCode += ", aux" + tempVarNum + ".i32";
                            tempVarNum++;
                            i++; // ignore the next kid cause its array access
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

        conditionCode = returnVar;
        auxCode += auxConditionCode;
        code += auxCode;  
        LevelVisit--;
        return returnVar; 
    }



    public String getNodeType(JmmNode node) {
        String type;
        scopeVariables = analyser.getSymbolTable().getLocalVariables(currentMethodName);

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

    private Optional<JmmNode> getNextSibling(JmmNode node) {
        JmmNode parent = node.getParent();
        for (int i = 0; i < parent.getNumChildren()-1; i++) {
            if (node.toString().equals(parent.getChildren().get(i).toString())) {
                return Optional.of(parent.getChildren().get(i + 1));
            }
        }
        return Optional.empty();
    }

}
