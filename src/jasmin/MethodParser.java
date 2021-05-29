package jasmin;

import org.specs.comp.ollir.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MethodParser {
    private Method method;
    private ClassUnit classUnit;
    private int stack;
    private String instructionsCode = "";
    private int stackMax;

    public MethodParser(Method method, ClassUnit classUnit) {
        this.method = method;
        this.classUnit = classUnit;

        generateInstructionsCode();
    }

    public String generateJasmin() {
        StringBuilder jasminCode = new StringBuilder();

        // System.out.printf("\tMETHOD NAME: %s\n",
        // method.getMethodName().toUpperCase());

        jasminCode.append("\n.method public");

        if (this.method.isConstructMethod()) {
            jasminCode.append(" <init>");
        } else {
            if (this.method.isStaticMethod()) {
                jasminCode.append(" static");
            }
            if (this.method.isFinalMethod()) {
                jasminCode.append(" final");
            }

            jasminCode.append(" " + method.getMethodName());
        }

        jasminCode.append("(");

        jasminCode.append(buildParameters());

        jasminCode.append(")");

        // Identifies the type of element returned by the method
        jasminCode.append(TypeUtils.parseType(method.getReturnType()));

        var localVars = this.method.getVarTable().values().stream().filter(var -> var.getScope() != VarScope.FIELD)
                .collect(Collectors.toSet());
        // localVars.forEach(var -> {
        //     if(var.getVarType() instanceof ClassType){
        //         System.out.println(((ClassType) var.getVarType()).getName());
        //     }
        // });
        // System.out.println("-----------------------");
        jasminCode.append("\n\t\t.limit locals " + localVars.size() + "\n\t\t.limit stack " + stackMax + "\n");

        jasminCode.append(instructionsCode);

        if(this.method.getReturnType().getTypeOfElement().equals(ElementType.VOID)){
            jasminCode.append("\t\treturn\n");
        }

        jasminCode.append(".end method\n");

        return jasminCode.toString();
    }

    private void generateInstructionsCode() {
        // StringBuilder jasminCode = new StringBuilder();
        for (var instruct : method.getInstructions()) {
            generateInstructionCode(instruct);
        }
    }

    private void generateInstructionCode(Instruction instruct) {
        // StringBuilder jasminCode = new StringBuilder();

        switch (instruct.getInstType()) {
            case CALL:
                generateCall((CallInstruction) instruct);
                break;
            default:
                addComment(instruct.getInstType() + " IS MISSING");
        }

        // return jasminCode.toString();
    }

    private void generateCall(CallInstruction instruction) {
        switch (instruction.getInvocationType()) {
            case invokestatic:
                generateInvokeStatic(instruction);
                break;
            case invokespecial:
                generateInvokeSpecial(instruction);
                break;
            default:
                addComment("Missing CALL " + instruction.getInvocationType());
        }

    }

    private void generateInvokeStatic(CallInstruction instruction) {

        this.instructionsCode += "\t\tinvokestatic "+((Operand) instruction.getFirstArg()).getName()+"."+((LiteralElement) instruction.getSecondArg()).getLiteral().replaceAll(
                "\"", "")+"()"+ TypeUtils
                .parseElementType(instruction.getReturnType().getTypeOfElement())+"\n";
    }

    private void generateInvokeSpecial(CallInstruction instruction) {
        loadStack(instruction.getFirstArg());
        if(classUnit.getSuperClass()==null){
            this.instructionsCode += "\t\tinvokespecial java/lang/Object/<init>()"+ TypeUtils.parseElementType(instruction.getReturnType().getTypeOfElement())+"\n";
            return;
        }else{
            this.instructionsCode += "\t\tinvokespecial "+ this.classUnit.getSuperClass() +"()"+ TypeUtils.parseElementType(instruction.getReturnType().getTypeOfElement())+"\n"; 
            return; 
        }

    }

    private void loadStack(Element e){
        if(e.isLiteral()){

        }
        else{
            var name= ((Operand) e).getName();
            if(name.equals("this")){
                generateAload(0);
            }
        }

    }

    private void generateAload(int index){
        putStack();
        this.instructionsCode+="\t\taload_"+index+"\n";
    }

    private void putStack(){
        stack++;
        if(stack>stackMax) stackMax=stack;
    }

    private void addComment(String comment) {
        this.instructionsCode += "\n; " + comment + "\n";
    }

    private String buildParameters() {
        List<String> parameters = new ArrayList<>();

        this.method.getParams().forEach(parameter -> parameters.add(TypeUtils.parseType(parameter.getType())));
        return String.join("", parameters);
    }

}
