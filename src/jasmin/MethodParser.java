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

    public String generateJasmin(){
        StringBuilder jasminCode = new StringBuilder();





      //  System.out.printf("\tMETHOD NAME: %s\n", method.getMethodName().toUpperCase());

        jasminCode.append("\n\n.method public");

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

        //Identifies the type of element returned by the method at hand.
        jasminCode.append(TypeUtils.parseType(method.getReturnType()));


        var localVars =
                this.method.getVarTable().values().stream().filter(var -> var.getScope() != VarScope.FIELD).collect(Collectors.toSet());
       // locals = listLocalVar.size();
        jasminCode.append("\n\t\t.limit locals "+ localVars.size() + "\n\t\t.limit stack "+ stackMax + "\n");


        jasminCode.append(instructionsCode);

        jasminCode.append("\n.end method");

        return jasminCode.toString();
    }

    private void generateInstructionsCode(){
       // StringBuilder jasminCode = new StringBuilder();
        for (var instruct : method.getInstructions()) {
            generateInstructionCode(instruct);
        }
    }

    private void generateInstructionCode(Instruction instruct) {
        //StringBuilder jasminCode = new StringBuilder();

        switch (instruct.getInstType()) {
            case CALL:
                generateCall((CallInstruction) instruct);

            default:
                addComment(instruct.getInstType() + " IS MISSING");
        }

       // return jasminCode.toString();
    }

    private void generateCall(CallInstruction instruction){
        switch (instruction.getInvocationType()){
            case invokestatic:
                generateInvokeStatic();

            default:
                addComment("Missing CALL "+instruction.getInvocationType());
        }

    }

    private void generateInvokeStatic(){

    }

    private void addComment(String comment){
        this.instructionsCode += "\n; "+comment+"\n";
    }


    private String buildParameters() {
        List<String> parameters = new ArrayList<>();

        this.method.getParams().forEach(parameter -> parameters.add(TypeUtils.parseType(parameter.getType())));
        return String.join("", parameters);
    }

}
