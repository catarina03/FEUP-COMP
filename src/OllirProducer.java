import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class OllirProducer implements JmmVisitor {
    public SymbolTableManager table;
    public List<Report> reports;
    public String code = "";

    public List<Symbol> mainParameters;
    public List<Symbol> methodParameters;
    public List<Symbol> classFields;
    public List<Symbol> scopeVariables;
    public String currentMethodName;

    public int tempVarNum = 0;
    public int objectsCount = 0;
    public int whileCounter = 0;
    public int ifCounter = 0;

    OllirProducer(SymbolTableManager table, List<Report> reports) {
        this.table = table;
        this.reports = reports;

        this.classFields = table.getFields();
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
    public void setDefaultVisit(BiFunction method) {
    }

    @Override
    public void addVisit(String kind, BiFunction method) {
    }

    private void generateClass(JmmNode classNode) {
        code += table.getClassName() + "{\n";

        generateClassFields(classNode);
        generateConstructor();

        List<JmmNode> children = classNode.getChildren();
        for (JmmNode child : children) {
            switch (child.getChildren().get(0).getKind()) { // get methods, if first child of method is returnType its a
                                                            // method, else its main
                case "ReturnType":
                    tempVarNum = 0;
                    generateMethod(child);
                    break;
                case "Main":
                    tempVarNum = 0;
                    this.currentMethodName = "main";
                    generateMain(child);
                    break;
            }
        }
        code += "}";
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
        code += "\n\t.construct " + table.getClassName() + "().V {\n\t\tinvokespecial(this, \"<init>\").V;\n\t}\n";
    }

    private void generateMain(JmmNode node) {

        if (table.getLocalVariables("main") != null) {
            this.scopeVariables = table.getLocalVariables("main");
        }

        generateMainHeader(node);
        generateMainBody(node);
        code += "\t}\n";
    }

    private void generateMainHeader(JmmNode node) {
        String mainArgs = "";
        String mainReturnType = "";

        mainParameters = table.getParameters("main");
        List<String> mainParametersNames = new ArrayList<>();

        for (Symbol param : mainParameters) {
            mainParametersNames.add(param.getName());
        }

        for (int i = 0; i < mainParameters.size(); i++) {
            if (i > 0) {
                mainArgs += ", ";
            }
            String type = mainParameters.get(i).getType().getName();
            if (mainParameters.get(i).getType().isArray()) {
                type += "[]";
            }
            mainArgs += mainParametersNames.get(i) + "." + getType(type);
        }

        mainReturnType += ".V";
        code += "\n\t.method public static main (" + mainArgs + ")" + mainReturnType + " {\n";
    }

    public void generateMainBody(JmmNode node) {
        for (int i = 1; i < node.getNumChildren(); i++) {
            if (node.getChildren().get(i).getKind().equals("MethodBody")) {
                generateMethodBody(node.getChildren().get(i));
            }

            /*
             * JmmNode child = node.getChildren().get(i);
             * 
             * switch (child.getKind()) { //case "VarDeclaration": //NOT NEEDED case
             * "IDstatement": generateStatement(child); break; }
             * 
             */
        }
    }

    private void generateMethod(JmmNode node) {

        if (table.getLocalVariables(node.get("functionName")) != null) {
            this.scopeVariables = table.getLocalVariables(node.get("functionName"));
        }
        this.currentMethodName = node.get("functionName");

        generateMethodHeader(node);

        // ------------------Experiment: original
        // generateMethodBody(node);
        // ------------------Experiment: other (this works better but needs the generate
        // return here)
        for (int i = 1; i < node.getNumChildren(); i++) {
            if (node.getChildren().get(i).getKind().equals("MethodBody")) {
                generateMethodBody(node.getChildren().get(i));
            } else if (node.getChildren().get(i).getKind().equals("Return")) {
                generateReturn(node.getChildren().get(i), node.get("functionName"));
            }
        }

        code += "\t}\n";
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
                    methodArgs += ", ";
                }
                String type = methodParameters.get(i).getType().getName();
                if (methodParameters.get(i).getType().isArray()) {
                    type += "[]";
                }
                methodArgs += methodParameters.get(i).getName() + "." + getType(type);
            }
        }

        Type type = table.getReturnType(methodName); //FIXME: im showing arrays where I shouldnt
        String typeS = type.getName();
        if (type.isArray()) {
            typeS += "[]";
        }

        methodReturnType += "." + getType(typeS);
        code += "\n\t.method public " + methodName + "(" + methodArgs + ")" + methodReturnType + " {\n";
    }

    private void generateMethodBody(JmmNode node) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            switch (child.getKind()) {
                // case "VarDeclaration": //NOT NEEDED
                case "IDstatement":
                    generateStatement(child);
                    break;
                case "If":
                    generateIf(child);
                    break;
                case "While":
                    generateWhile(child);
                    break;
                // case "Return": //return now goes to generateMethod
                // generateReturn(child, node.get("functionName"));
                // break;
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
        if (scopeVariables != null) {
            for (Symbol param : scopeVariables) {
                scopeVariablesNames.add(param.getName());
            }
        }

        for (int i = 0; i < node.getNumChildren(); i++) {
            JmmNode child = node.getChildren().get(i);
            String type = null;

            if (i < node.getNumChildren() - 1 && node.getChildren().get(i + 1).getKind().equals("VarAssignment")) {

                if (child.getKind().equals("ExpressionTerminal") && child.getNumChildren() == 0) {
                    type = getNodeType(node);
                    String t = getNodeType(child);
                    if (classFieldsNames.contains(child.get("ID"))) {
                        Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")),
                                "t" + tempVarNum++);
                        /*
                         * if (t.equals("int") || t.equals("int[]") || t.equals("String") ||
                         * t.equals("String[]") || t.equals("boolean")) { Symbol o = new
                         * Symbol(s.getType(), "o" + objectsCount++); code += "\t\t" + s.getName() + "."
                         * + getType(t) + " :=." + getType(t) + " getfield(" + o.getName() + "." +
                         * getType(t) + ", " + child.get("ID") + "." + getType(t) + ")." + getType(t) +
                         * ";\n"; } else {
                         */
                        code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(this" + ", "
                                + child.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                        /* } */
                        code += "\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                        code += s.getName() + "." + getType(t) + ";\n";

                        // TODO ARRAY ACCESS IN CLASS FIELD
                    } else if (node.getOptional("ID").isPresent() && classFieldsNames.contains(node.get("ID"))) { // putField
                        if (methodParametersNames.contains(child.get("ID"))) { // class field=method parameter
                            int index = methodParametersNames.indexOf(child.get("ID")) + 1;
                            code += "\t\tputfield(this, " + node.get("ID") + "." + getType(type) + ", $" + index + "."
                                    + child.get("ID") + "."
                                    + getType(methodParameters.get(index - 1).getType().getName()) + ").V;\n";

                        } else if (scopeVariablesNames.contains(child.get("ID"))) { // class field=scope variable //
                                                                                    // TODO: this.a=b[4] arrays
                            int index = scopeVariablesNames.indexOf(child.get("ID"));
                            code += "\t\tputfield(this, " + node.get("ID") + "." + getType(type) + ", "
                                    + child.get("ID") + "." + getType(scopeVariables.get(index).getType().getName())
                                    + ").V;\n";
                        }
                    } else {
                        if (methodParametersNames.contains(child.get("ID"))) {
                            int idx = methodParametersNames.indexOf(child.get("ID")) + 1;
                            code += "\t\t" + child.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                            code += "$" + idx + "." + child.get("ID") + "." + getType(t) + ";\n";

                            // TODO ARRAY ACCESS IN METHOD ARG

                        } else {
                            if (scopeVariablesNames.contains(child.get("ID"))) {
                                code += generateExpressionTerminal(child);
                            }
                            code += "\t\t" + node.get("ID") + "." + getType(type) + " :=." + getType(type) + " ";
                            code += child.get("ID") + "." + getType(t) + ";\n";
                        }
                    }
                } else {
                    JmmNode second = child.getChildren().get(0);

                    if (child.getKind().equals("ExpressionTerminal")) {

                        if (node.getOptional("ID").isPresent() && classFieldsNames.contains(node.get("ID"))) { // putField
                                                                                                               // class
                                                                                                               // field
                                                                                                               // = some
                                                                                                               // number/variable
                            type = getNodeType(node);
                            if (second.getNumChildren() == 0) {// integer
                                code += "\t\tputfield(this, " + node.get("ID") + "." + getType(type) + ", "
                                        + second.get("Integer") + ".i32).V;\n";
                            } else {// booleans
                                if (second.getChildren().get(0).getKind().equals("BooleanTrue")) {
                                    code += "\t\tputfield(this, " + node.get("ID") + "." + getType(type)
                                            + ", 1.bool).V;\n";
                                } else if (second.getChildren().get(0).getKind().equals("BooleanFalse")) {
                                    code += "\t\tputfield(this, " + node.get("ID") + "." + getType(type)
                                            + ", 0.bool).V;\n";
                                }
                            }
                        } else if (second.getNumChildren() != 0) {
                            generateTerminal(second.getChildren().get(0));
                        } else { // FIXME: só para terminals com integers
                            code += "\t\t" + child.getParent().get("ID") + ".i32 :=.i32 " + second.get("Integer")
                                    + ".i32;\n";
                        }
                    } else if (child.getKind().equals("ArrayAccess")) {
                        generateArrayAccess(child, methodParametersNames);
                    } else if (isExpression(child)) {
                        Analyser analyser = new Analyser(table, reports);
                        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
                        expressionVisitor.tempVarNum = this.tempVarNum;
                        expressionVisitor.visit(child, analyser);
                        code += expressionVisitor.code;
                    }
                }
            }else if(node.getChildren().get(i).getKind().equals("DotMethodCall")){ //dotMethodCall
                generateDotMethodCall(node.getChildren().get(i), methodParametersNames, classFieldsNames);
            }
            
        }
    }

    private boolean isExpression(JmmNode node) {
        switch (node.getKind()) {
            case "And":
            case "Less":
            case "Plus":
            case "Minus":
            case "Mul":
            case "Div":
            case "Not":
                return true;
            default:
                return false;
        }
    }

    /*
     * private void generateExpression(JmmNode node){ switch (node.getKind()){ case
     * "And": //Checkar se a var direita é var //Checkar se a var esquerda é var
     * //var1.bool &&.bool var2.bool case "Less":
     * 
     * } }
     * 
     */

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

        if (varKind.equals("IDstatement") || varKind.equals("VarDeclaration")) {
            var = returnNode.get("ID");

            String t = getNodeType(returnNode);

            if (classFieldsNames.contains(returnNode.get("ID"))) {
                Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")), "t" + tempVarNum++);

                if (t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]")
                        || t.equals("boolean")) {
                    Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                    code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(" + o.getName()
                            + "." + getType(t) + ", " + returnNode.get("ID") + "." + getType(t) + ")." + getType(t)
                            + ";\n";
                } else {
                    code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(this" + ", "
                            + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                }
                var = s.getName();
                varKind = t;
            } else {
                List<String> methodParametersNames = new ArrayList<>();
                if (methodParameters != null) {
                    for (Symbol param : methodParameters) {
                        methodParametersNames.add(param.getName());
                    }
                }

                if (methodParametersNames.contains(returnNode.get("ID"))) {
                    int idx = methodParametersNames.indexOf(returnNode.get("ID")) + 1;
                    var = "$" + idx + "." + returnNode.get("ID");
                    varKind = methodParameters.get(idx - 1).getType().getName();
                    if (methodParameters.get(idx - 1).getType().isArray()) {
                        varKind += "[]";
                    }
                } else {
                    var = returnNode.get("ID");
                    varKind = t;
                }
            }
        } else if (varKind.equals("ExpressionTerminal")) {
            if (returnNode.getOptional("ID").isPresent()) {
                var = returnNode.get("ID");

                String t = getNodeType(returnNode);

                if (classFieldsNames.contains(returnNode.get("ID"))) {
                    Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")),
                            "t" + tempVarNum++);

                    if (t.equals("int") || t.equals("int[]") || t.equals("String") || t.equals("String[]")
                            || t.equals("boolean")) {
                        Symbol o = new Symbol(s.getType(), "o" + objectsCount++);
                        code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield("
                                + o.getName() + "." + getType(t) + ", " + returnNode.get("ID") + "." + getType(t) + ")."
                                + getType(t) + ";\n";
                    } else {
                        code += "\t\t" + s.getName() + "." + getType(t) + " :=." + getType(t) + " getfield(this" + ", "
                                + returnNode.get("ID") + "." + getType(t) + ")." + getType(t) + ";\n";
                    }
                    var = s.getName();
                    varKind = t;
                } else {
                    List<String> methodParametersNames = new ArrayList<>();
                    if (methodParameters != null) {
                        for (Symbol param : methodParameters) {
                            methodParametersNames.add(param.getName());
                        }
                    }

                    if (methodParametersNames.contains(returnNode.get("ID"))) {
                        int idx = methodParametersNames.indexOf(returnNode.get("ID")) + 1;
                        var = "$" + idx + "." + returnNode.get("ID");
                        varKind = methodParameters.get(idx - 1).getType().getName();
                        if (methodParameters.get(idx - 1).getType().isArray()) {
                            varKind += "[]";
                        }
                    } else {
                        var = returnNode.get("ID");
                        varKind = t;
                    }
                }
            } else {
                switch (returnNode.getChildren().get(0).getKind()) {
                    case "Terminal":
                        if (returnNode.getChildren().get(0).getNumChildren() == 0) {
                            generateTerminal(returnNode.getChildren().get(0));
                            return;
                        } else {
                            generateTerminal(returnNode.getChildren().get(0).getChildren().get(0));
                        }
                        break;
                }

            }

        }
        // TODO: more cases

        else {
            var = returnNode.get("ID");
        }

        code += "\n\t\tret." + getType(returnType) + " " + var + "." + getType(varKind) + ";\n";
    }

    private void generateTerminal(JmmNode node) {
        if (node.getKind().equals("Terminal")) {
            if (node.getOptional("Integer").isPresent() && node.getParent().getParent().getKind().equals("Return")) {
                // hmm, same problem, we need to bring the left part inside of the function

                String value = node.getOptional("Integer").get();

                this.tempVarNum++;
                code += "\t\t" + "aux" + this.tempVarNum + ".i32 :=.i32 " + value + ".i32;\n";
                value = "aux" + this.tempVarNum;

                String t = "int";
                Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")), "t" + tempVarNum++);

                code += "\t\tret.i32 " + value + ".i32;\n";
            }
        } else {
            JmmNode ancestor = node.getParent().getParent().getParent();

            if (ancestor.getKind().equals("IDstatement")) {
                String type = getNodeType(ancestor);

                switch (node.getKind()) {
                    case "BooleanTrue":
                        code += "\t\t" + ancestor.get("ID") + "." + getType(type) + " :=.bool 1.bool;\n";
                        break;
                    case "BooleanFalse":
                        code += "\t\t" + ancestor.get("ID") + "." + getType(type) + " :=.bool 0.bool;\n";
                        break;
                    case "New":
                        if (node.getChildren().get(0).getKind().equals("TypeObject")) {
                            String typeObj = node.getChildren().get(0).get("Object");
                            code += "\t\t" + ancestor.get("ID") + "." + getType(type) + " :=." + typeObj + " new("
                                    + typeObj + ")." + typeObj + ";\n";
                            code += "\t\tinvokespecial(" + node.getParent().getParent().getParent().get("ID") + "."
                                    + typeObj + ",\"<init>\").V;\n";
                        } else if (node.getChildren().get(0).getKind().equals("IntArrayVar")) {
                            String length = node.getChildren().get(0).getChildren().get(0).getChildren().get(0)
                                    .get("Integer");
                            code += "\t\t" + ancestor.get("ID") + "." + getType(type) + " :=.array.i32 new(array, "
                                    + length + ".i32).array.i32;\n";
                        }
                        break;
                    case "Not":
                        if (node.getChildren().get(0).getKind().equals("ExpressionTerminal")) {
                            String child = node.getChildren().get(0).get("ID");
                            // String child = node.getChildren().get(0).getChildren().get(0).get("ID");
                            code += "\t\t" + ancestor.get("ID") + "." + getType(type) + " :=.bool " + child
                                    + ".bool !.bool " + child + ".bool;\n";
                        } else if (node.getChildren().get(0).getKind().equals("BooleanFalse")) {
                            code += "\t\t" + ancestor.get("ID") + "." + getType(type)
                                    + " :=.bool 0.bool !.bool 0.bool;\n";
                        } else if (node.getChildren().get(0).getKind().equals("BooleanTrue")) {
                            code += "\t\t" + ancestor.get("ID") + "." + getType(type)
                                    + " :=.bool 1.bool !.bool 1.bool;\n";

                        }
                        break;
                }
            }
        }

    }

    private void generateArrayAccess(JmmNode node, List<String> methodParameterNames) {
        JmmNode child = node.getChildren().get(0); // ExpressionTerminal

        String type = getNodeType(node.getParent());

        String index = null;
        boolean isInt = false;
        if (child.getOptional("ID").isEmpty()) {
            JmmNode target = child.getChildren().get(0); // Terminal

            if (target.getOptional("Integer").isPresent()) {
                index = target.getOptional("Integer").get();
                isInt = true;
            }
        } else {
            index = child.get("ID");
        }

        boolean isStatic = false;
        if (this.currentMethodName.equals("main")) {
            isStatic = true;
            methodParameterNames.clear();

            if (mainParameters != null) {
                for (Symbol param : mainParameters) {
                    methodParameterNames.add(param.getName());
                }
            }
        }

        String t = "int[]";
        Symbol s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")), "t" + tempVarNum++);

        JmmNode accessedVar = getUpperSibling(node).get();
        String name = accessedVar.get("ID");

        int argNumber = -1234;
        for (int i = 0; i < methodParameterNames.size(); i++) {
            if (methodParameterNames.get(i).equals(name)) {
                argNumber = i;
            }
        }
        if (!isStatic)
            argNumber++;

        if (isStatic && argNumber == 1) {
            t = "String[]";
            s = new Symbol(new Type(t.substring(0, t.length() - 2), t.contains("[]")), "t" + tempVarNum++);
        }

        String prefix = "";
        if (argNumber >= 0) {
            prefix = "$" + argNumber + ".";
        }

        if (isInt) {
            this.tempVarNum++;
            code += "\t\t" + "aux" + this.tempVarNum + ".i32 :=.i32 " + index + ".i32;\n";
            index = "aux" + this.tempVarNum;
        }

        code += "\t\t" + node.getParent().get("ID") + "." + getType(type) + " :=." + getType(s.getType().getName())
                + " " + prefix + name + "[" + index + ".i32]." + getType(s.getType().getName()) + ";\n";
    }

    private String generateExpressionTerminal(JmmNode node) {
        if (node.getNumChildren() > 0) {
            JmmNode terminalNode = node.getChildren().get(0);
            if (terminalNode.getKind().equals("Terminal")) {
                if (terminalNode.getNumChildren() > 0) {
                    JmmNode terminalVarNode = terminalNode.getChildren().get(0);
                    switch (terminalVarNode.getKind()) { // TODO: ESTAMOS A ASSUMIR QUE SO TEM UM FILHO
                        case "BooleanTrue":
                            return ":=.bool 1.bool;";
                        case "BooleanFalse":
                            return ":=.bool 0.bool;";
                    }
                }
            }
        }

        return ""; // TODO: IS THIS CORRECT?
    }

    private void generateDotMethodCall(JmmNode node, List<String> methodParameterNames, List<String> classFieldsNames){
        //métodos DESTA CLASSE  invokevirtual
        if(table.getMethods().contains(node.get("DotMethodCall"))){
            if (node.getNumChildren() == 0) { // sem argumentos
                code += "\t\tinvokevirtual(" + node.getParent().get("ID") + "." + getType(getNodeType(node.getParent()))+ ", \"" + node.get("DotMethodCall")
                        + "\").V;\n";
            } else {// com argumentos
                code += "\t\tinvokevirtual(" + node.getParent().get("ID") + "." + getType(getNodeType(node.getParent()))+ ", \"" + node.get("DotMethodCall") + "\"";
                for (int i = 0; i < node.getNumChildren(); i++) {
                    // expression terminal with ID
                    if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getOptional("ID").isPresent()) {
                        if (methodParameterNames.contains(node.getChildren().get(i).get("ID"))) {
                            int index = methodParameterNames.indexOf(node.getChildren().get(i).get("ID"));
                            code += ", $" + index + "." + node.getChildren().get(i).get("ID") + "."
                                    + getType(getNodeType(node.getChildren().get(i)));

                        } else if (classFieldsNames.contains(node.getChildren().get(i).get("ID"))) {
                            code += ", getfield(this, " + node.getChildren().get(i).get("ID") + "."
                                    + getType(getNodeType(node.getChildren().get(i))) + ")";
                        } else {
                            code += ", " + node.getChildren().get(i).get("ID") + "."
                                    + getType(getNodeType(node.getChildren().get(i)));
                        }
                    }
                    // expression terminal withoug ID and a terminal kid
                    else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getOptional("Integer").isPresent()) {
                        code += ", " + node.getChildren().get(i).getChildren().get(0).get("Integer") + ".i32";
                    } else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren() == 1
                            && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getNumChildren() == 1) { // terminal kids
                                                                                                       // with boolean
                                                                                                       // kids
                        if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanTrue")) {
                            code += ", 1.bool";
                        } else if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind()
                                .equals("BooleanFalse")) {
                            code += ", 0.bool";
                        }
                    }
                }
                code += ").V;\n";
            }
        
        }else{ // metodos de OUTRAS CLASSES

            if(node.getNumChildren()==0){  //sem argumentos
                code += "\t\tinvokestatic(" + node.getParent().get("ID")+ ", \""+node.get("DotMethodCall")+"\").V;\n";
            }else{//com argumentos
                code += "\t\tinvokestatic(" + node.getParent().get("ID")+ ", \""+node.get("DotMethodCall")+"\"";
                for(int i=0; i<node.getNumChildren();i++){
                    //expression terminal with ID
                    if(node.getChildren().get(i).getKind().equals("ExpressionTerminal") && node.getChildren().get(i).getOptional("ID").isPresent()){
                        if(methodParameterNames.contains(node.getChildren().get(i).get("ID"))){
                            int index = methodParameterNames.indexOf(node.getChildren().get(i).get("ID"));
                            code += ", $"+index+"." + node.getChildren().get(i).get("ID") + "."
                                    + getType(getNodeType(node.getChildren().get(i)));

                        }else if(classFieldsNames.contains(node.getChildren().get(i).get("ID"))){
                            code += ", getfield(this, " + node.getChildren().get(i).get("ID") + "."
                                    + getType(getNodeType(node.getChildren().get(i)))+")";
                        }else{
                            code+=", "+node.getChildren().get(i).get("ID") + "." + getType(getNodeType(node.getChildren().get(i)));
                        }
                    }
                    //expression terminal withoug ID and a terminal kid
                    else if (node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren()==1 && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal") && 
                            node.getChildren().get(i).getChildren().get(0).getOptional("Integer").isPresent()) {
                        code += ", " + node.getChildren().get(i).getChildren().get(0).get("Integer") + ".i32";
                    }else if(node.getChildren().get(i).getKind().equals("ExpressionTerminal")
                            && node.getChildren().get(i).getNumChildren()==1 && node.getChildren().get(i).getChildren().get(0).getKind().equals("Terminal")
                            && node.getChildren().get(i).getChildren().get(0).getNumChildren()==1){  //terminal kids with boolean kids
                        if(node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind().equals("BooleanTrue")){
                            code += ", 1.bool";
                        }else if(node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind().equals("BooleanFalse")){
                            code += ", 0.bool";
                        }
                    }
                }
                code+=").V;\n";
            }
        }
    }

    private void generateIf(JmmNode node) {

        generateIfCondition(node.getChildren().get(0));

        // if (i.i32 ==.i32 $2.N.i32) goto Then;
        // goto endif;
        // Then:all.bool :=.bool 1.bool;
        // endif:

        
        if (node.getChildren().get(2).getKind().equals("Else")) {
            generateElseBody(node.getChildren().get(2));
            code += "\t\tgoto Endif" + ifCounter + ";\n";

        }
        if (node.getChildren().get(1).getKind().equals("Then")) {
            code += "\t\tThen" + ifCounter + ":\n";
            generateIfBody(node.getChildren().get(1));
            code += "\t\tEndif" + ifCounter + ":\n";

        }
    }

    private void generateIfCondition(JmmNode node) {
        code += "\t\tif" + "(";
        Analyser analyser = new Analyser(table, reports);
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        expressionVisitor.tempVarNum = this.tempVarNum;
        expressionVisitor.visit(node.getChildren().get(0), analyser);
        code += expressionVisitor.conditionCode;
        code += ") goto Then" + ifCounter + ";\n";

    }

    private void generateIfBody(JmmNode node) {
        for(int i=0;i<node.getNumChildren();i++){
            code += "\t";
            generateStatement(node.getChildren().get(i));
        }
    }

    private void generateElseBody(JmmNode node) {
        for (int i = 0; i < node.getNumChildren(); i++){
            code += "\t";
            generateStatement(node.getChildren().get(i));
        }
    }

    private void generateWhile(JmmNode node){
        code+= "\t\tLoop" + whileCounter + ":\n";
        generateWhileCondition(node.getChildren().get(0));

        code += "\t\tBody"+whileCounter+":\n";
        generateWhileBody(node);
        code += "\t\tEndLoop"+whileCounter+":\n";
    }

    private void generateWhileCondition(JmmNode node) {
        code += "\t\tif" + " (";
        Analyser analyser = new Analyser(table, reports);
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        expressionVisitor.tempVarNum = this.tempVarNum;
        expressionVisitor.visit(node.getChildren().get(0), analyser);
        code += expressionVisitor.conditionCode;
        code += ") goto Body" + whileCounter + ";\n";
        code += "\t\tgoto EndLoop" + whileCounter + ";\n";
    }

    private void generateWhileBody(JmmNode node) {
        for (int i = 1; i < node.getNumChildren(); i++){
            code += "\t";
            generateStatement(node.getChildren().get(i));
        }
        code += "\t\tgoto Loop"+whileCounter+";\n";
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