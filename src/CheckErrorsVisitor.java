import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class CheckErrorsVisitor extends PreorderJmmVisitor<Analyser, Boolean> {
    public CheckErrorsVisitor() {
        setDefaultVisit(this::checkSemanticErrors);
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

            /*
            case "IntArrayVar":
                for (int i = 0; i < node.getChildren().size(); i++){
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")){
                        for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                            if (node.getChildren().get(i).getChildren().get(j).getKind().equals("Terminal")){
                                node.getChildren().get(i).getChildren().get(j).get("Integer");
                            }
                            //TODO REPORTS
                        }
                    }
                    else{
                        //TODO
                        //Report report = new Report()
                    }
                }
                */
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
        }

        return true;
    }
}
