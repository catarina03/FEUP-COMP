import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.specs.util.utilities.StringLines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirstPreorderVisitor extends PreorderJmmVisitor<SymbolTableManager, Boolean> {
    //private final String identifierAttribute;

    public FirstPreorderVisitor() {
        //super(FirstPreorderVisitor::reduce);

        //this.identifierAttribute = identifierAttribute;

        //addVisit(, this::dealWithIdentifier);
        setDefaultVisit(this::populateSymbolTable);
    }

    private Boolean populateSymbolTable(JmmNode node, SymbolTableManager symbolTable){
        System.out.println("I'M HERE IN NODE: " + node.getKind());

        switch(node.getKind()){
            case "Import":
                symbolTable.addImports(node.get("importObject"));
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
                
                if(node.getChildren().get(0).getKind().equals("Main"))
                {
                    method.setMethodName("main");
                    method.setReturnType(new Type("void", false));
                }
                else {
                    method.setMethodName(node.get("functionName"));

                    System.out.println(node.get("functionName"));
                    System.out.println(node.getKind());
                    System.out.println(node.getChildren());
                    System.out.println(node.getAttributes());
                    //System.out.println(node.getOptional("arguments") + "\n");

                    String returnTypeName = "";
                    boolean returnTypeIsArray = false;
                    //ArrayList<String> methodArgumentNames = new ArrayList<>();//= node.get("arguments");
                    //ArrayList<Type> methodArgumentTypes = new ArrayList<Type>();
                    ArrayList<Symbol> methodArguments = new ArrayList<>();
                    
                    for (int i = 0; i < node.getChildren().size(); i++){
                        if (node.getChildren().get(i).getKind().equals("ReturnType")){
                            returnTypeName = node.getChildren().get(i).getChildren().get(0).get("type");

                            if(node.getChildren().get(i).getChildren().size() == 2){
                                returnTypeIsArray = true;
                            }
                        }

                        if (node.getChildren().get(i).getKind().equals("ArgDeclaration")){
                            System.out.println("Arg name: " + node.getChildren().get(i).get("variable"));
                            String argumentName = node.getChildren().get(i).get("variable");

                            boolean argumentTypeIsArray = false;
                            String argumentTypeName = "";

                            if (node.getChildren().get(i).getChildren().size() > 0){
                                System.out.println("VAR HAS CHILDREN");
                                
                                System.out.println("Arg type - 2: " + node.getChildren().get(i).getChildren().get(0).getChildren());
                                
                                if (node.getChildren().get(i).getChildren().get(0).getChildren().size() > 0){
                                    System.out.println("NEM SEI O QUE É ISTO MAS TEM CHILDRENYAY");
                                    System.out.println("Arg type - 1: " + node.getChildren().get(i).getChildren().get(0).getChildren().get(0));
                                }
                                
                                
                            }

/*
                            if (node.getChildren().get(i).getChildren().get(0).getChildren().get(0).equals("IntArrayVarType")){
                                argumentTypeName = "int";
                                argumentTypeIsArray = true;
                            }
                            else {
                                //System.out.println("Arg type: " + node.getChildren().get(i).getChildren().get(0).getChildren().get(0).get("type"));
                                //argumentTypeName = node.getChildren().get(i).getChildren().get(0).getChildren().get(0).get("type");
                                //argumentTypeIsArray = Boolean.parseBoolean(node.getChildren().get(i).getChildren().get(0).getChildren().get(1).getOptional("isArray").get());

                            }
                            */

                            methodArguments.add(new Symbol(new Type(argumentTypeName, argumentTypeIsArray), argumentName));
                        }
                    }

                    Type returnType = new Type(returnTypeName, returnTypeIsArray);
                    method.setReturnType(returnType);

                    method.setMethodParameters(methodArguments);

                    //for (int i = 0; i < methodArgumentNames.size(); i++){
                     //   methodArguments.add(new Symbol(methodArgumentTypes.get(i), methodArgumentNames.get(i)));
                    //}

                }
                symbolTable.addMethod(method);
                break;
        }

        return true;
    }










/*














    public String dealWithIdentifier(JmmNode node, String space) {
        if (node.get(identifierAttribute).equals("this")) {
            return space + "THIS_ACCESS";
        }

        return defaultVisit(node, space);
    }

    private String defaultVisit(JmmNode node, String space) {
        String content = space + node.getKind();
        String attrs = node.getAttributes()
                .stream()
                .filter(a -> !a.equals("line"))
                .map(a -> a + "=" + node.get(a))
                .collect(Collectors.joining(", ", "[", "]"));

        content += ((attrs.length() > 2) ? attrs : "");

        return content;
    }

    private static String reduce(String nodeResult, List<String> childrenResults) {
        var content = new StringBuilder();

        content.append(nodeResult).append("\n");

        for (var childResult : childrenResults) {
            var childContent = StringLines.getLines(childResult).stream()
                    .map(line -> " " + line + "\n")
                    .collect(Collectors.joining());

            content.append(childContent);
        }

        return content.toString();
    }
    */

}
