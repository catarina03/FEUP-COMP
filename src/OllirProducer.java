import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmVisitor;

public class OllirProducer implements JmmVisitor{
    public SymbolTable table;
    public String code = "";

    OllirProducer(SymbolTable table){
        this.table=table;
    }


    @Override
    public Object visit(JmmNode node, Object data) {
        /*
         * System.out.println("\n\nNODE: "); System.out.println("String: "
         * +node.toString()); System.out.println("Type: " +
         * node.getClass().getComponentType()); System.out.println("Kind: " +
         * node.getKind()); System.out.println("Class: " + node.getClass());
         * System.out.println("Attributes: " + node.getAttributes());
         * System.out.println("Children: " + node.getChildren());
         */

        switch (node.getKind()) {
            case "Class":
                generateClass(node);
        }

        return defaultVisit(node, "");
    }


    //TODO: mudar isto ihih
    private String defaultVisit(JmmNode node, String space) {
        String content = space + node.getKind();
        String attrs = node.getAttributes().stream().filter(a -> !a.equals("line")).map(a -> a + "=" + node.get(a))
                .collect(Collectors.joining(", ", "[", "]"));

        content += ((attrs.length() > 2) ? attrs : "") + "\n";
        for (JmmNode child : node.getChildren()) {
            content += visit(child, space + " ");
        }
        return content;
    }


    @Override
    public void setDefaultVisit(BiFunction method) {}

    @Override
    public void addVisit(String kind, BiFunction method) {}

    
    private void generateClass(JmmNode classNode) {
        code+=table.getClassName() + "{\n";

        
        List<Symbol> fields = table.getFields();
        
        if (fields != null) {
            List<String> fieldsNames = new ArrayList<>();

            for (int i = 0; i < fields.size(); i++) {
                fieldsNames.add(fields.get(i).getName());
            }
        }

        // code+=".construct public " + symbolTable.getClassName() + "().V \n";

        // generateClassVariables(classNode);
        // generateConstructor();

        // List<JmmNode> children = classNode.getChildren();
        // for (int i = 0; i < children.size(); i++) {
        //     JmmNode child = children.get(i);

        //     switch (child.getKind()) {
        //         case "Main":
        //             generateMain(child);
        //             break;
        //         case "Method":
        //             generateMethod(child);
        //             break;
        //     }
        // }
        // stringCode.append("}");
    }

}
