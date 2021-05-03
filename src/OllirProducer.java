import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
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

        generateClassFields(classNode);
        generateConstructor();  //TODO: é suposto chamar esta função sempre mesmo quando não tem construtor???

        List<JmmNode> children = classNode.getChildren();
        for (JmmNode child : children) {
            switch (child.getChildren().get(0).getKind()) { //get methods, if first child of method is returnType its a method, else its main
                case "ReturnType":
                    generateMethod(child);
                    break;
                case "Main":
                    generateMain(child);
                    break;
            }
        }
        code+="}";
    }

    private void generateClassFields(JmmNode node) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            if (child.getKind().equals("VarDeclaration")) {
                generateGlobalVar(child);
            }
        }
    }

    private void generateGlobalVar(JmmNode node) {
        JmmNode typeNode = node.getChildren().get(0);
        String varName = node.get("variable");
        String varType = null;

        if (typeNode.getKind().equals("Identifier")) {
            varType = typeNode.get("variable"); // TODO: testar isto oopsie
        } else {
            varType = typeNode.get("type"); 
        }

        code += "\n\t.field private " + varName + "." + getType(varType) + ";\n";
    }

    private void generateConstructor() { 
        code+="\n\t.construct " + table.getClassName() + "().V {\n\t\tinvokespecial(this, \"<init>\").V;\n\t}\n";
    }

    private void generateMain(JmmNode node) {
        generateMainHeader(node);
        generateMainBody(node);
        code+="\t}\n";
    }

    private void generateMainHeader(JmmNode node) { //TODO: make more better function oopsie
        String mainArgs="";
        String mainReturnType="";

        List<Symbol> mainParameters = table.getParameters("main");
        List<String> mainParametersNames =  new ArrayList<>();

        for (Symbol param : mainParameters) {
            mainParametersNames.add(param.getName());
        }

        for (int i = 0; i < mainParameters.size(); i++) {
            if (i > 0) {
                mainArgs+=", ";
            }
            String type = mainParameters.get(i).getType().getName();
            if (mainParameters.get(i).getType().isArray()) {
                type += "[]";
            }
            mainArgs+=mainParametersNames.get(i) + "." + getType(type);
        }

        mainReturnType+=".V";
        code+="\n\t.method public static main (" + mainArgs + ")" + mainReturnType + " {\n";
    }

    public void generateMainBody(JmmNode node) {
        for (int i = 1; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);

            switch (child.getKind()) {
                case "Statement":
                    // generateStatement(child);
                    break;
            }
        }
    }

    private void generateMethod(JmmNode node) {
        generateMethodHeader(node);
        generateMethodBody(node);
        code+="\t}\n";
    }

    private void generateMethodHeader(JmmNode methodNode) {
        String methodArgs = "";
        String methodReturnType = "";

        String methodName = methodNode.get("functionName");
        List<Symbol> methodParameters = table.getParameters(methodName);
        List<String> methodParametersNames = new ArrayList<>();

        if (methodParameters != null) {
            for (Symbol param : methodParameters) {
                methodParametersNames.add(param.getName());
            }

            for (int i = 0; i < methodParameters.size(); i++) {
                if (i > 0) {
                    methodArgs+=", ";
                }
                String type = methodParameters.get(i).getType().getName();
                if (methodParameters.get(i).getType().isArray()) {
                    type += "[]";
                }
                methodArgs+=methodParameters.get(i).getName() + "." + getType(type);
            }
        }
    
        Type type = table.getReturnType(methodName);
        String typeS = type.getName();
        if (type.isArray()) {   //FIXME: its not adding the thingy when its array :((
            typeS += "[]";
        }

        methodReturnType+="." +typeS;
        code+="\n\t.method public " + methodName + "(" + methodArgs + ")" + methodReturnType + " {\n";
    }

    private void generateMethodBody(JmmNode node) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            switch (child.getKind()) {
                case "Statement":
                    // generateStatement(child);
                    break;
                case "Return":
                    generateReturn(child, node.get("functionName"));
                    break;
            }
        }
    }

    // TODO: private void generateStatement(JmmNode node) {

    private void generateReturn(JmmNode node, String methodName) {
        String returnType = table.getReturnType(methodName).getName();
        if (table.getReturnType(methodName).isArray()) {
            returnType += "[]";
        }

        JmmNode returnNode = node.getChildren().get(0);
        String varKind = returnNode.getKind();
        String var = null;

        //TODO: COMPLETE THIS WINK WINK

        code += "\n\t\tret." + getType(returnType) + " " + var + "." + getType(varKind) + ";\n";
    }

    private String getType(String type) {
        switch (type) {
            case "int":
                return "i32";
            case "boolean":
                return "bool";
            case "void":
                return "V";
            case "int[]":
                return "array.i32";
            case "String[]":
                return "array.String";
            case "String":
                return "String";
            default:
                return type;
        }
    }
}
