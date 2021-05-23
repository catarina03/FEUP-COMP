import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PostorderJmmVisitor;

public class ExpressionVisitor extends PostorderJmmVisitor<Analyser, String> {
    private String aux1;
    private String aux2;
    private String aux3;
    public String code = "";
    private int tempVarNum = 0;

    public ExpressionVisitor(){
        addVisit("And", this::dealWithAnd);
        addVisit("ExpressionTerminal", this::dealWithExpressionTerminal);
        addVisit("Terminal", this::dealWithTerminal);
        addVisit("BooleanFalse", this::dealWithFalse);
        addVisit("BooleanTrue", this::dealWithTrue);
    }

    private String dealWithAnd(JmmNode node, Analyser analyser){
        String left = visit(node.getChildren().get(0));
        String right = visit(node.getChildren().get(1)); //upper sibling

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

        code += "\t\t"+returnVar+" :=.bool "+leftExpression+" &&.bool "+rightExpression+";\n";
        return returnVar;
        //code += "\t\t"+leftExpression+" &&.bool "+rightExpression+";\n";
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

        


}
