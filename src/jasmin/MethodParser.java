package jasmin;

import org.specs.comp.ollir.*;

import java.util.ArrayList;
import java.util.List;
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

        var localVars = this.method.getVarTable().values().stream().filter(var -> var.getVirtualReg() != -1)
                .collect(Collectors.toSet());

        int localVarsCount = localVars.size();
        if(!this.method.isStaticMethod()){
            if(!this.method.getVarTable().keySet().contains("this")){
                localVarsCount++;
            }
        }
        jasminCode.append("\n\t\t.limit locals " + localVarsCount + "\n\t\t.limit stack " + stackMax + "\n");

        jasminCode.append(instructionsCode);

        if(this.method.getReturnType().getTypeOfElement().equals(ElementType.VOID)){
            jasminCode.append("\t\treturn\n");
        }

        jasminCode.append(".end method\n");

        return jasminCode.toString();
    }

    private void generateInstructionsCode() {
        for (var instruct : method.getInstructions()) {
            generateInstructionCode(instruct);
        }
    }

    private void generateInstructionCode(Instruction instruct) {
        switch (instruct.getInstType()) {
            case CALL:
                generateCall((CallInstruction) instruct);
                break;
            case ASSIGN:
                generateAssign((AssignInstruction) instruct);
                break;
            case GETFIELD:
                generateGetField((GetFieldInstruction) instruct);
                break;
            case PUTFIELD:
                generatePutField((PutFieldInstruction) instruct);
                break;
            case RETURN:
                generateReturn((ReturnInstruction) instruct);
                break;
            default:
                addComment(instruct.getInstType() + " IS MISSING");
        }
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
        this.instructionsCode += "\t\tinvokestatic "+((Operand) instruction.getFirstArg()).getName()+"."+((LiteralElement) instruction. getSecondArg()).getLiteral().replaceAll(
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

    private void generateReturn(ReturnInstruction instruction){
        switch (instruction.getOperand().getType().getTypeOfElement()) {
            case INT32:
                loadStack(instruction.getOperand());
                this.instructionsCode += "\t\tireturn\n";
                break;
            default:
                addComment("Missing RETURN TYPE " + instruction.getElementType());
                break;
        }
    }

    private void generateAssign(AssignInstruction instruction){
        var name= ((Operand) instruction.getDest()).getName();
        var variable = this.method.getVarTable().get(name);

        switch (instruction.getTypeOfAssign().getTypeOfElement()){
            case INT32:
                if(instruction.getRhs().getInstType().equals(InstructionType.NOPER)){
                    if (((SingleOpInstruction) instruction.getRhs()).getSingleOperand().isLiteral()){
                        //  if (instruction.getRhs().getInstType().equals())
                        this.instructionsCode += "\t\ticonst_" + ((LiteralElement) ((SingleOpInstruction) instruction.getRhs()).getSingleOperand()).getLiteral() + "\n";
                        this.instructionsCode += "\t\tistore_" + variable.getVirtualReg() + "\n";
                    }
                    else{
                        var operandName= ((Operand) ((SingleOpInstruction) instruction.getRhs()).getSingleOperand()).getName();
                        var operandVariable = this.method.getVarTable().get(operandName);

                        if(operandVariable.getScope().equals(VarScope.LOCAL)){
                            this.instructionsCode += "\t\tiload_" + operandVariable.getVirtualReg() + "\n";
                            this.instructionsCode += "\t\tistore_" + variable.getVirtualReg() + "\n";
                        }
                        else{
                            //  this.instructionsCode += "\t\ticonst_"+  (instruction.getRhs()).getSingleOperand().getVirtualReg() + "\n"; instruction.
                            addComment("MISSING ASSIGNMENT OPERATION FOR NON LITERALS SCOPE " + operandVariable.getScope());
                        }
                    }
                }


                break;
            default:
                addComment("Missing ASSIGN TYPE " + instruction.getTypeOfAssign().getTypeOfElement());
        }
    }

    private void generateGetField(GetFieldInstruction instruction) {

        this.instructionsCode += "\t\tgetfield " + instruction.getFirstOperand() + "/" + instruction.getSecondOperand() + TypeUtils.parseType(instruction.getSecondOperand().getType());

            /*
        this.instructionsCode += "\t\tinvokestatic "+((Operand) instruction.getFirstArg()).getName()+"."+((LiteralElement) instruction.getSecondArg()).getLiteral().replaceAll(
                "\"", "")+"()"+ TypeUtils
                .parseElementType(instruction.getReturnType().getTypeOfElement())+"\n";

             */
    }

    private void generatePutField(PutFieldInstruction instruction) {
        loadStack(instruction.getFirstOperand());
        loadStack(instruction.getThirdOperand());

        this.instructionsCode += "\t\tputfield " + method.getOllirClass().getClassName() + "/" + ((Operand) instruction.getSecondOperand()).getName() + " " + TypeUtils.parseType(instruction.getSecondOperand().getType()) + "\n";

        popStack(); //Dest variable
        popStack(); // Operand
    }

    private void loadStack(Element e) {
        if(e.isLiteral()){
            var literal = ((LiteralElement) e).getLiteral();

            if(e.getType().getTypeOfElement().equals(ElementType.INT32)){
                generateIConst(Integer.parseInt(literal));
            }
            else{
                addComment("ICONST EQUIVALENT FOR LITERAL " + e.getType().getTypeOfElement() + " IS MISSING");
            }
        }
        else{
            var name= ((Operand) e).getName();
            var variable = this.method.getVarTable().get(name);
            if(name.equals("this")){
                generateAload(0);
            }
            else{
                if(variable.getScope().equals(VarScope.PARAMETER)){
                    if (variable.getVarType().getTypeOfElement().equals(ElementType.INT32)){
                        generateIload(variable.getVirtualReg());
                    }
                    else{
                        addComment("LOAD TYPE " + variable.getVarType() + " NOT IMPLEMENTED");
                    }
                }

                if(variable.getScope().equals(VarScope.LOCAL)){
                    if (variable.getVarType().getTypeOfElement().equals(ElementType.INT32)) {
                        generateIload(variable.getVirtualReg());
                    }
                    else{
                        addComment("LOAD TYPE " + variable.getVarType() + " NOT IMPLEMENTED");
                    }
                }
            }


        }
    }

    private void generateAload(int index){
        putStack();
        this.instructionsCode+="\t\taload_"+index+"\n";
    }

    private void generateIload(int index){
        putStack();
        this.instructionsCode+="\t\tiload_"+index+"\n";
    }

    private void generateIConst(int index){
        putStack();
        this.instructionsCode += "\t\ticonst_" + index + "\n";
    }

    private void putStack(){
        stack++;
        if(stack>stackMax) stackMax=stack;
    }

    private void popStack(){
        stack--;
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
