import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmVisitor;
import pt.up.fe.comp.jmm.ast.PostorderJmmVisitor;

public class ExpressionVisitor extends AJmmVisitor<Analyser, String> {
    private String aux1;
    private String aux2;
    private String aux3;
    public String code = "";
    public String conditionCode="";
    public int tempVarNum = 0;

    public ExpressionVisitor() {
        setDefaultVisit(this::visit);
    }

    public String visit(JmmNode node, Analyser analyser){
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
            default:
                return "; " + node.getKind() + " IS MISSING\n";
        }
        /*
        addVisit("And", this::dealWithAnd);
        addVisit("ExpressionTerminal", this::dealWithExpressionTerminal);
        addVisit("Terminal", this::dealWithTerminal);
        addVisit("BooleanFalse", this::dealWithFalse);
        addVisit("BooleanTrue", this::dealWithTrue);
        addVisit("Less",this::dealWithLess);
        addVisit("Not", this::dealWithNot);
        addVisit("Plus", this::dealWithPlus);
        addVisit("Minus", this::dealWithMinus);
        addVisit("Mul", this::dealWithMul);
        addVisit("Div", this::dealWithDiv);
         */

        //return code;
    }

    private String dealWithNot(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0));
/*
        String leftExpression = "";
        if (left.equals("true")){
            leftExpression = "1.bool";
        }
        else if(left.equals("false")){
            leftExpression = "0.bool";
        }

 */
        return "";
    }

    private String dealWithAnd(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

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
        return visit(node.getChildren().get(0));
    }

    private String dealWithTerminal(JmmNode node, Analyser analyser){
        return visit(node.getChildren().get(0));
    }

    private String dealWithTrue(JmmNode node, Analyser analyser){
        return "true";
    }

    private String dealWithFalse(JmmNode node, Analyser analyser){
        return "false";
    }

    private String dealWithLess(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

        String returnVar = "aux" + tempVarNum;
        tempVarNum++;

        code += "\t\t" + returnVar + ".bool" + " :=.bool " + left + ".i32 <.bool " + right + ".i32;\n";
        conditionCode += " "+ left + ".i32 <.bool " + right + ".i32 ";
        return returnVar;
    }

    private String dealWithPlus(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

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
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

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
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

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
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1));

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

}
