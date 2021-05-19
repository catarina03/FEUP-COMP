import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.specs.util.utilities.StringLines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirstPreorderVisitor extends PreorderJmmVisitor<SymbolTableManager, Boolean> {

    public FirstPreorderVisitor() {
        setDefaultVisit(this::populateSymbolTable);
    }

    private Boolean populateSymbolTable(JmmNode node, SymbolTableManager symbolTable){
        switch(node.getKind()){
            case "Import":
                if (node.getOptional("importObject").isPresent()){
                    symbolTable.addImports(node.get("importObject"));
                }
                break;
            
            case "Class":
                symbolTable.setClassName(node.get("className"));
                break;

            case "Extends":
                symbolTable.setClassExtends(true);
                symbolTable.setClassSuper(node.get("classExtends"));
                break;

            case "MethodDeclaration":
                ClassMethod method = new ClassMethod();
                ArrayList<Symbol> methodArguments = new ArrayList<>();
                ArrayList<Symbol> methodLocalVariables = new ArrayList<>();

                // RETRIEVES INFORMATION ABOUT MAIN METHOD AND STORES IT IN SYMBOL TABLE
                if (node.getChildren().get(0).getKind().equals("Main")) {
                    method.setMethodName("main");
                    method.setReturnType(new Type("void", false));

                    for (int i = 0; i < node.getChildren().size(); i++) {

                        // STORES MAIN ARGUMENTS INFORMATION IN THE SYMBOL TABLE
                        if (node.getChildren().get(i).getKind().equals("MainArguments")) {
                            String argumentName = node.getChildren().get(i).get("variable");
                            boolean argumentTypeIsArray = true;
                            String argumentTypeName = "String";
                            methodArguments.add(new Symbol(new Type(argumentTypeName, argumentTypeIsArray), argumentName));
                        }

                        // STORES MAIN LOCAL VARIABLES INFORMATION IN THE SYMBOL TABLE
                        if (node.getChildren().get(i).getKind().equals("MethodBody")) {
                            for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++) {
                                if (node.getChildren().get(i).getChildren().get(j).getKind().equals("VarDeclaration")) {
                                    String localVariableName = node.getChildren().get(i).getChildren().get(j).get("variable");
                                    boolean localVariableTypeIsArray = false;
                                    String localVariableTypeName = "";

                                    if (node.getChildren().get(i).getChildren().get(j).getChildren().size() > 0) {
                                        localVariableTypeName = node.getChildren().get(i).getChildren().get(j).getChildren().get(0).get("type");

                                        if (node.getChildren().get(i).getChildren().get(j).getChildren().get(0).getChildren().size() > 0) {
                                            if (node.getChildren().get(i).getChildren().get(j).getChildren().get(0).getChildren().get(0).getKind().equals("IntArrayVarType")) {
                                                localVariableTypeIsArray = true;
                                            }
                                        }
                                    }
                                    methodLocalVariables.add(new Symbol(new Type(localVariableTypeName, localVariableTypeIsArray), localVariableName));
                                }
                            }
                        }

                    }
                    method.setMethodParameters(methodArguments);
                    method.setLocalVariables(methodLocalVariables);
                }
                // RETRIVES INFORMATION ABOUT CLASS METHODS AND STORES IT IN THE SYMBOL TABLE
                else {
                    method.setMethodName(node.get("functionName"));
                    String returnTypeName = "";
                    boolean returnTypeIsArray = false;
                    
                    for (int i = 0; i < node.getChildren().size(); i++){

                        // RETRIEVES INFORMATION ABOUT METHOD RETURN TYPE
                        if (node.getChildren().get(i).getKind().equals("ReturnType")){
                            returnTypeName = node.getChildren().get(i).getChildren().get(0).get("type");

                            if(node.getChildren().get(i).getChildren().size() > 0){
                                returnTypeIsArray = true;
                            }
                        }

                        // RETRIEVES INFORMATION ABOUT METHOD ARGUMENTS
                        if (node.getChildren().get(i).getKind().equals("ArgDeclaration")){
                            String argumentName = node.getChildren().get(i).get("variable");
                            boolean argumentTypeIsArray = false;
                            String argumentTypeName = "";

                            if (node.getChildren().get(i).getChildren().size() > 0){
                                argumentTypeName = node.getChildren().get(i).getChildren().get(0).get("type");

                                if (node.getChildren().get(i).getChildren().get(0).getChildren().size() > 0){
                                    if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).getKind().equals("IntArrayVarType")){
                                        argumentTypeIsArray = true;
                                    }
                                }
                            }

                            methodArguments.add(new Symbol(new Type(argumentTypeName, argumentTypeIsArray), argumentName));
                        }

                        // RETRIEVES INFORMATION ABOUT THE METHOD BODY
                        if (node.getChildren().get(i).getKind().equals("MethodBody")){
                            for (int j = 0; j < node.getChildren().get(i).getChildren().size(); j++){
                                // SAVES INFORMATION ABOUT THE LOCAL VARIABLES
                                if (node.getChildren().get(i).getChildren().get(j).getKind().equals("VarDeclaration")){
                                    String localVariableName = node.getChildren().get(i).getChildren().get(j).get("variable");
                                    boolean localVariableTypeIsArray = false;
                                    String localVariableTypeName = "";
                                    if (node.getChildren().get(i).getChildren().get(j).getChildren().size() > 0){
                                        localVariableTypeName = node.getChildren().get(i).getChildren().get(j).getChildren().get(0).get("type");
                                        if (node.getChildren().get(i).getChildren().get(j).getChildren().get(0).getChildren().size() > 0){
                                            if (node.getChildren().get(i).getChildren().get(j).getChildren().get(0).getChildren().get(0).getKind().equals("IntArrayVarType")){
                                                localVariableTypeIsArray = true;
                                            }
                                        }
                                    }
                                    methodLocalVariables.add(new Symbol(new Type(localVariableTypeName, localVariableTypeIsArray), localVariableName));
                                }
                            }
                        }
                    }
                    Type returnType = new Type(returnTypeName, returnTypeIsArray);
                    method.setReturnType(returnType);
                    method.setMethodParameters(methodArguments);
                    method.setLocalVariables(methodLocalVariables);
                }
                symbolTable.addMethod(method);
                break;

            case "VarDeclaration":
                // RETRIEVES CLASS FIELDS AND STORES THEM IN THE SYMBOL TABLE
                if (node.getParent().getKind().equals("Class")) {
                    String fieldName = node.get("variable");
                    boolean fieldIsArray = false;
                    String fieldTypeName = "";
                    for (int i = 0; i < node.getChildren().size(); i++) {
                        fieldTypeName = node.getChildren().get(i).get("type");
                        if (node.getChildren().get(i).getChildren().size() > 0) {
                            if (node.getChildren().get(i).getChildren().get(0).getKind().equals("IntArrayVarType")) {
                                fieldIsArray = true;
                            }
                        }
                    }
                    symbolTable.addField(new Symbol(new Type(fieldTypeName, fieldIsArray), fieldName));
                }
                break;

        }
        return true;
    }

}
