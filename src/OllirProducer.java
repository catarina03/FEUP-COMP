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

    public List<Symbol> mainParameters;
    public List<Symbol> methodParameters;
    public List<Symbol> classFields;
    public List<Symbol> scopeVariables;

    public int tempVarNum = 0;
    public int objectsCount = 0;


    OllirProducer(SymbolTable table){
        this.table=table;

        this.classFields=table.getFields();
    }


    @Override
    public Object visit(JmmNode node, Object data) {
        switch (node.getKind()) {
            case "Class":
                generateClass(node);
        }

        return defaultVisit(node, "");
    }

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
        generateConstructor();

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

        varType = typeNode.get("type"); 
       
        code += "\n\t.field private " + varName + "." + getType(varType) + ";\n";
    }

    private void generateConstructor() { 
        code+="\n\t.construct " + table.getClassName() + "().V {\n\t\tinvokespecial(this, \"<init>\").V;\n\t}\n";
    }

    private void generateMain(JmmNode node) {

        if (table.getLocalVariables("main") != null) {
            this.scopeVariables = table.getLocalVariables("main");
        }

        generateMainHeader(node);
        generateMainBody(node);
        code+="\t}\n";
    }

    private void generateMainHeader(JmmNode node) {
        String mainArgs="";
        String mainReturnType="";

        mainParameters = table.getParameters("main");
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
            if (node.getChildren().get(i).getKind().equals("MethodBody")){
                generateMethodBody(node.getChildren().get(i));
            }

            /*
            JmmNode child = node.getChildren().get(i);

            switch (child.getKind()) {
                //case "VarDeclaration":    //NOT NEEDED
                case "IDstatement":
                    generateStatement(child);
                    break;
            }

             */
        }
    }

    private void generateMethod(JmmNode node) {

        if (table.getLocalVariables(node.get("functionName")) != null) {
            this.scopeVariables = table.getLocalVariables(node.get("functionName"));
        }

        generateMethodHeader(node);
        generateMethodBody(node);
        code+="\t}\n";
    }

    private void generateMethodHeader(JmmNode methodNode) {
        String methodArgs = "";
        String methodReturnType = "";

        String methodName = methodNode.get("functionName");
        methodParameters = table.getParameters(methodName);
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
        if (type.isArray()) {
            typeS += "[]";
        }

        methodReturnType+="." +getType(typeS);
        code+="\n\t.method public " + methodName + "(" + methodArgs + ")" + methodReturnType + " {\n";
    }

    private void generateMethodBody(JmmNode node) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            switch (child.getKind()) {
                //case "VarDeclaration":    //NOT NEEDED
                case "IDstatement":
                    generateStatement(child);
                    break;
                case "Return":
                    generateReturn(child, node.get("functionName"));
                    break;
            }
        }
    }


    private void generateStatement(JmmNode node) {

        List<String> classFieldsNames = new ArrayList<>();
        if (classFields != null) {
            for (Symbol field : classFields) {
                classFieldsNames.add(field.getName());
            }
        }

        List<String> methodParametersNames = new ArrayList<>();
        if (methodParameters != null) {
            for (Symbol param : methodParameters) {
                methodParametersNames.add(param.getName());
            }
        }


        List<String> scopeVariablesNames = new ArrayList<>();
        if (scopeVariables!= null) {
            for (Symbol param : scopeVariables) {
                scopeVariablesNames.add(param.getName());
            }
        }

        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            String type = null;

            if (i < node.getNumChildren()-1 && node.getChildren().get(i+1).getKind().equals("VarAssignment")) {

                if (child.getKind().equals("ExpressionTerminal") && child.getNumChildren() == 0){
                    type = getNodeType(node);
                    String t = getNodeType(child);
                    if (classFieldsNames.contains(child.get("ID"))) {
                        Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")),
                                "t" + tempVarNum++);

                        if (t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]")
                                || t.equals("boolean")) {
                            Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                            code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t)
                                    + " getfield(" + o.getName() + "." + getType(t) + ", " + child.get("ID")
                                    + "." + getType(t) + ")." + getType(t) + ";\n";
                        } else {
                            code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t)
                                    + " getfield(this" + ", " + child.get("ID") + "." + getType(t) + ")."
                                    + getType(t) + ";\n";
                        }
                        code += "\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                        code += s.getName() + "." + getType(t) + ";\n";
                    }
                    else{
                        if (methodParametersNames.contains(child.get("ID"))) {
                            int idx = methodParametersNames.indexOf(child.get("ID")) + 1;
                            code+="\t\t" + child.get("ID") + "." + getType(type) + " :=."
                                    + getType(type) + " ";
                            code+="$" + idx + "." + child.get("ID") + "." + getType(t) + ";\n";
                        } else {
                            if (scopeVariablesNames.contains(child.get("ID"))){
                                code += generateExpressionTerminal(child);
                            }
                            code+="\t\t" + node.get("ID") + "." + getType(type) + " :=."
                                    + getType(type) + " ";
                            code+=child.get("ID") + "." + getType(t) + ";\n";
                        }
                    }
                }
                else{
                    JmmNode second = child.getChildren().get(0);

                    if (node.getKind().equals("IDstatement")) {
                        type = getNodeType(node);

                        if (child.getKind().equals("ExpressionTerminal")) {
                            //String t = getNodeType(second.getChildren().get(0));

                            second.getChildren().get(0);
                            code +="\t\t" + node.get("ID") + "." + getType(type) + " ";
                            code += generateTerminal(second.getChildren().get(0)) + "\n";

/*
                            //CLASS FIELD
                            //METHOD PARAMETER
                            //LOCAL VAR ASSIGNMENT
                            if (classFieldsNames.contains(second.get("ID"))) {
                                Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")),
                                        "t" + tempVarNum++);

                                if (t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]")
                                        || t.equals("boolean")) {
                                    Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                                    code+="\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t)
                                            + " getfield(" + o.getName() + "." + getType(t) + ", " + second.get("ID")
                                            + "." + getType(t) + ")." + getType(t) + ";\n";
                                } else {
                                    code+="\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t)
                                            + " getfield(this" + ", " + second.get("ID") + "." + getType(t) + ")."
                                            + getType(t) + ";\n";
                                }
                                code+="\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                                code+=s.getName() + "." + getType(t) + ";\n";

                            } else {

                                if (methodParametersNames.contains(second.get("ID"))) {
                                    int idx = methodParametersNames.indexOf(second.get("ID")) + 1;
                                    code+="\t\t" + child.get("ID") + "." + getType(type) + " :=."
                                            + getType(type) + " ";
                                    code+="$" + idx + "." + second.get("ID") + "." + getType(t) + ";\n";
                                } else {
                                    if (scopeVariablesNames.contains(second.get("ID"))){
                                        code += generateExpressionTerminal(second);
                                    }
                                    code+="\t\t" + child.get("ID") + "." + getType(type) + " :=."
                                            + getType(type) + " ";
                                    code+=second.get("ID") + "." + getType(t) + ";\n";
                                }
                            }

                        } else if (second.getChildren().get(second.getNumChildren()-1).getKind().equals("BooleanTrue") || second
                                .getChildren().get(second.getNumChildren() - 1).getKind().equals("BooleanFalse")) {
                            code+="\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                            String boolT;
                            if (second.getChildren().get(second.getNumChildren() - 1).getKind().equals("BooleanTrue")) {
                                boolT = "1";
                            } else {
                                boolT = "0";
                            }
                            code+=boolT + "." + getType(type) + ";\n";

                        } else if (second.getKind().equals("int") || second.getKind().equals("int[]")) {
                            code += "\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                            code += second.get("value") + "." + getType(type) + ";\n";

                        } else if (second.getKind().equals("New")) {
                            code+="\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                            code+="new(" + getType(type) + ")." + getType(type) + ";\n";
                            code+="\t\tinvokespecial(" + child.get("ID") + "." + getType(type) + ", \"<init>\").V\n";

                            //TODO: more cases

                            */

                        }
                    }
                }



            }
            //TODO: dotMethods + arrays
        }
    }

    private void generateReturn(JmmNode node, String methodName) {
        String returnType = table.getReturnType(methodName).getName();
        if (table.getReturnType(methodName).isArray()) {
            returnType += "[]";
        }

        JmmNode returnNode = node.getChildren().get(0);
        String varKind = returnNode.getKind();
        String var = null;

        List<String> classFieldsNames = new ArrayList<>();
        if (classFields != null) {
            for (Symbol field : classFields) {
                classFieldsNames.add(field.getName());
            }
        }

        if(varKind.equals("IDstatement")|| varKind.equals("VarDeclaration")) {
            var = returnNode.get("ID");

            String t = getNodeType(returnNode);
            

            if(classFieldsNames.contains(returnNode.get("ID"))) {
                Symbol s = new Symbol(new Type(t.substring(0, t.length()-2), t.contains("[]")), "t"+tempVarNum++);

                if(t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]") || t.equals("boolean")) {
                    Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                    code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(" + o.getName() + "." + getType(t) + ", " + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                }
                else {
                    code+="\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(this" + ", " + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                }
                var = s.getName();
                varKind = t;
            }
            else {
                List<String> methodParametersNames = new ArrayList<>();
                if (methodParameters != null) {
                    for (Symbol param : methodParameters) {
                        methodParametersNames.add(param.getName());
                    }
                }

                if(methodParametersNames.contains(returnNode.get("ID"))) {
                    int idx = methodParametersNames.indexOf(returnNode.get("ID")) + 1;
                    var = "$" + idx + "." + returnNode.get("ID");
                    varKind = methodParameters.get(idx - 1).getType().getName();
                    if(methodParameters.get(idx - 1).getType().isArray()) {
                        varKind += "[]";
                    }
                }
                else {
                    var = returnNode.get("ID");
                    varKind = t;
                }
            }
        }
        else if(varKind.equals("ExpressionTerminal")){
            if (returnNode.getOptional("ID").isPresent()){
                var = returnNode.get("ID");

                String t = getNodeType(returnNode);


                if(classFieldsNames.contains(returnNode.get("ID"))) {
                    Symbol s = new Symbol(new Type(t.substring(0, t.length()-2), t.contains("[]")), "t"+tempVarNum++);

                    if(t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]") || t.equals("boolean")) {
                        Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                        code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(" + o.getName() + "." + getType(t) + ", " + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                    }
                    else {
                        code+="\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(this" + ", " + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                    }
                    var = s.getName();
                    varKind = t;
                }
                else {
                    List<String> methodParametersNames = new ArrayList<>();
                    if (methodParameters != null) {
                        for (Symbol param : methodParameters) {
                            methodParametersNames.add(param.getName());
                        }
                    }

                    if(methodParametersNames.contains(returnNode.get("ID"))) {
                        int idx = methodParametersNames.indexOf(returnNode.get("ID")) + 1;
                        var = "$" + idx + "." + returnNode.get("ID");
                        varKind = methodParameters.get(idx - 1).getType().getName();
                        if(methodParameters.get(idx - 1).getType().isArray()) {
                            varKind += "[]";
                        }
                    }
                    else {
                        var = returnNode.get("ID");
                        varKind = t;
                    }
                }
            }
            else {
                switch(returnNode.getChildren().get(0).getKind()){
                    case "Terminal":
                        generateTerminal(returnNode.getChildren().get(0));
                        break;
                }


            }

        }
        //TODO: more cases
        
        else {
            var = returnNode.get("ID");
        }

        code += "\n\t\tret." + getType(returnType) + " " + var + "." + getType(varKind) + ";\n";
    }

    private String generateTerminal(JmmNode node){

            switch (node.getKind()){  //TODO: ESTAMOS A ASSUMIR QUE SO TEM UM FILHO
                case "BooleanTrue":
                    return ":=.bool 1.bool;";
                case "BooleanFalse":
                    return ":=.bool 0.bool;";
                case "New":
                    if(node.getChildren().get(0).getKind().equals("TypeObject")){
                        String type = node.getChildren().get(0).get("Object");
                        return ":=."+type+" new("+type+")."+type+";\n\t\tinvokespecial("+node.getParent()
                                .getParent().getParent().get("ID")+"."+type+",\"<init>\").V;";
                    } else if (node.getChildren().get(0).getKind().equals("IntArrayVar")){
                        String length = node.getChildren().get(0).getChildren().get(0).getChildren().get(0).get("Integer");
                        //a3.array.array.array.i32 :=.array.array.array.i32 new(array, 5.i32, 4.i32, 3.i32).array.array.array.i32;
                        return ":=.array.i32 new(array, "+length+".i32).array.i32";
                    }
            }
            return ""; // TODO: IS THIS CORRECT?
        }

        private String generateExpressionTerminal(JmmNode node){
        if (node.getNumChildren() > 0) {
            JmmNode terminalNode = node.getChildren().get(0);
            if (terminalNode.getKind().equals("Terminal")){
                if (terminalNode.getNumChildren() > 0){
                    JmmNode terminalVarNode = terminalNode.getChildren().get(0);
                    switch (terminalVarNode.getKind()) {  //TODO: ESTAMOS A ASSUMIR QUE SO TEM UM FILHO
                        case "BooleanTrue":
                            return ":=.bool 1.bool;";
                        case "BooleanFalse":
                            return ":=.bool 0.bool;";
                    }
                }
            }
        }

        return ""; //TODO: IS THIS CORRECT?
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
            default:
                return type;
        }
    }

    public String getNodeType(JmmNode node) {
        String type;

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
}
