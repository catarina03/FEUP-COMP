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
            case NEW:
                generateNew(instruction);
                //storeStack(instruction.getFirstArg());
                break;
            default:
                addComment("Missing CALL " + instruction.getInvocationType());
        }
    }

    private void generateInvokeStatic(CallInstruction instruction) {
        String arguments = "";
        for (int i = 0; i < instruction.getListOfOperands().size(); i++){
            loadStack(instruction.getListOfOperands().get(i));
            arguments += TypeUtils.parseType(instruction.getListOfOperands().get(i).getType());

            if (i+1 < instruction.getListOfOperands().size()){
                arguments += ", ";
            }
        }
        this.instructionsCode += "\t\tinvokestatic "+((Operand) instruction.getFirstArg()).getName()+"."+((LiteralElement) instruction. getSecondArg()).getLiteral().replaceAll(
                "\"", "")+"(" + arguments + ")"+ TypeUtils
                .parseElementType(instruction.getReturnType().getTypeOfElement())+"\n";
        for (int i = 0; i < instruction.getListOfOperands().size(); i++){
            popStack();
        }
    }

    private void generateInvokeSpecial(CallInstruction instruction) {

        if(((Operand) instruction.getFirstArg()).getName().equals(this.method.getOllirClass().getClassName()) || ((Operand) instruction.getFirstArg()).getName().equals(this.method.getOllirClass().getSuperClass()) ){
            //this.instructionsCode += "\t\tnew " + ((Operand) instruction.getFirstArg()).getName() + "\n";
            loadStack(this.method.getParam(0));
        }
        else{
            loadStack(instruction.getFirstArg());
        }
        if(classUnit.getSuperClass()==null){
            this.instructionsCode += "\t\tinvokespecial java/lang/Object/<init>()"+ TypeUtils.parseElementType(instruction.getReturnType().getTypeOfElement())+"\n";
            /*
            if(!this.method.isConstructMethod()){
                storeStack((instruction.getFirstArg()));
            }

             */
            return;
        }else{
            this.instructionsCode += "\t\tinvokespecial "+ this.classUnit.getSuperClass() +"()"+ TypeUtils.parseElementType(instruction.getReturnType().getTypeOfElement())+"\n"; 
            return; 
        }
    }

    private void generateNew(CallInstruction instruction){
        //loadStack(instruction.getFirstArg());
        putStack();
        if(((Operand) instruction.getFirstArg()).getName().equals(this.method.getOllirClass().getClassName()) || ((Operand) instruction.getFirstArg()).getName().equals(this.method.getOllirClass().getSuperClass()) ){
            this.instructionsCode += "\t\tnew " + ((Operand) instruction.getFirstArg()).getName() + "\n";
            storeStack(this.method.getParam(0));
        }
        else{
            this.instructionsCode += "\t\tnew " + ((Operand) instruction.getFirstArg()).getName() + "\n";
            //this.instructionsCode += "\t\tdup\n";
            //loadStack(instruction.getFirstArg());
            storeStack(instruction.getFirstArg());
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

        switch(instruction.getRhs().getInstType()){
            case NOPER:
                switch (instruction.getTypeOfAssign().getTypeOfElement()) {
                    case INT32:
                        loadStack(((SingleOpInstruction) instruction.getRhs()).getSingleOperand());
                        storeStack((instruction.getDest()));
                        break;
                    default:
                        addComment("ASSIGN TYPE NOPER INSTRUCTION TYPE " + instruction.getTypeOfAssign().getTypeOfElement() + " IS MISSING");
                }
                break;
            case CALL:
                switch (instruction.getTypeOfAssign().getTypeOfElement()){
                    case OBJECTREF:
                        break;
                    default:
                        addComment("ASSIGN TYPE CALL INSTRUCTION TYPE " + instruction.getTypeOfAssign().getTypeOfElement() + " IS MISSING");
                }
                break;
            case BINARYOPER:
                generateExpression((BinaryOpInstruction) instruction.getRhs());
                storeStack((instruction.getDest()));
                break;
            default:
                addComment("Assign instruction " + instruction.getRhs().getInstType() + " is missing");
        }
    }

    private void generateExpression(BinaryOpInstruction instruction){
        loadStack(instruction.getLeftOperand());
        switch (instruction.getUnaryOperation().getOpType()){
            case ADD:
                generateAdd(instruction.getRightOperand());
                break;
            case SUB:
                generateSub(instruction.getRightOperand());
                break;
            case MUL:
                generateMul(instruction.getRightOperand());
                break;
            case DIV:
                generateDiv(instruction.getRightOperand());
            default:
                addComment("Expression operation " + instruction.getUnaryOperation().getOpType() + " is missing");
        }
    }

    private void generateAdd(Element rightSide){
        loadStack(rightSide);
        instructionsCode += "\t\tiadd\n";
    }

    private void generateSub(Element rightSide){
        loadStack(rightSide);
        instructionsCode += "\t\tisub\n";
    }

    private void generateMul(Element rightSide){
        loadStack(rightSide);
        instructionsCode += "\t\timul\n";
    }

    private void generateDiv(Element rightSide){
        loadStack(rightSide);
        instructionsCode += "\t\tidiv\n";
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
        //popStack(); // Operand
    }

    private void loadStack(Element e) {
        if(e.isLiteral()){
            var literal = ((LiteralElement) e).getLiteral();

            if(e.getType().getTypeOfElement().equals(ElementType.INT32)){
                generateIConstant(Integer.parseInt(literal));
            }
            else{
                addComment("ICONSTANT EQUIVALENT FOR LITERAL " + e.getType().getTypeOfElement() + " IS MISSING");
            }
        }
        else{
            var name= ((Operand) e).getName();
            var variable = this.method.getVarTable().get(name);
            if(name.equals("this") ){
                generateAload(0);
            }
            else{
                /*
                if(variable.getScope().equals(VarScope.PARAMETER)){
                    if (variable.getVarType().getTypeOfElement().equals(ElementType.INT32)){
                        generateIload(variable.getVirtualReg());
                    }
                    else{
                        addComment("LOAD TYPE " + variable.getVarType() + " NOT IMPLEMENTED");
                    }
                }

                 */

                if(variable.getScope().equals(VarScope.LOCAL) || variable.getScope().equals(VarScope.PARAMETER)){
                    switch (variable.getVarType().getTypeOfElement()){
                        case INT32:
                        case BOOLEAN:
                            generateIload(variable.getVirtualReg());
                            break;
                        case OBJECTREF:
                            if(((ClassType) variable.getVarType()).getName().equals(this.method.getOllirClass().getClassName()) || ((ClassType) variable.getVarType()).getName().equals(this.method.getOllirClass().getSuperClass())){
                                generateAload(0);
                            }else {
                                generateAload(variable.getVirtualReg());
                            }
                            break;
                        default:
                            addComment("LOAD TYPE " + variable.getVarType() + " NOT IMPLEMENTED");
                    }


                }
            }


        }
    }

    private void storeStack(Element e){
        var name= ((Operand) e).getName();
        var variable = this.method.getVarTable().get(name);

        if(variable.getScope().equals(VarScope.LOCAL) || variable.getScope().equals(VarScope.PARAMETER)) {
            switch (variable.getVarType().getTypeOfElement()){
                case INT32:
                    generateIStore(variable.getVirtualReg());
                    break;
                case OBJECTREF:
                case ARRAYREF:
                    generateAStore(variable.getVirtualReg());
                    break;
                default:
                    addComment("Store stack type " + variable.getVarType().getTypeOfElement() + " is missing");
            }
        } else{
            addComment("Store type " + variable.getScope() + " is missing");
        }
    }

    private void generateIStore(int index){
        popStack();
        this.instructionsCode += "\t\tistore_" + index + "\n";
    }

    private void generateAStore(int index){
        popStack();
        this.instructionsCode += "\t\tastore_" + index + "\n";
    }

    private void generateAload(int index){
        putStack();
        this.instructionsCode+="\t\taload_"+index+"\n";
    }

    private void generateIload(int index){
        putStack();
        this.instructionsCode+="\t\tiload_"+index+"\n";
    }

    private void generateIConstant(int literal){
        putStack();
        if (literal >= 0 && literal <= 5){
            this.instructionsCode += "\t\ticonst_" + literal + "\n";
        } else if (literal == -1){
            this.instructionsCode += "\t\ticonst_m1\n";
        } else if (literal >= -128 && literal <= 127){
            this.instructionsCode += "\t\tbipush " + literal + "\n";
        } else if (literal >= -32768 && literal <= 32767){
            this.instructionsCode += "\t\tsipush " + literal + "\n";
        } else {
            this.instructionsCode += "\t\tldc " + literal + "\n";
        }
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
