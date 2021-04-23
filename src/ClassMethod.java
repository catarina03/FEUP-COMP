import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;

public class ClassMethod {
    private String methodName; //? redundant
    private ArrayList<Symbol> methodParameters;
    private Type returnType;
    private ArrayList<Symbol> localVariables;

    public ClassMethod(){
        this.methodName = "";
        this.methodParameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
    }

    public void setMethodName(String name) {
        this.methodName = name;
    }

    public String getMethodName()  {
        return this.methodName;
    }
    
    public void setMethodParameters(ArrayList<Symbol> params) {
        this.methodParameters = params;
    }

    public ArrayList<Symbol> getMethodParameters() {
        return this.methodParameters;
    }

    public void setReturnType(Type type) {
        this.returnType = type;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public void setLocalVariables(ArrayList<Symbol> localVars) {
        this.localVariables = localVars;
    }

    public ArrayList<Symbol> getLocalVariables() {
        return this.localVariables;
    }

    @Override
    public String toString(){
        return "Method name: " + this.methodName + "\n" +
                "Method parameters: " + this.methodParameters + "\n" + 
                "Return type: " + this.returnType + "\n" +
                "Local variables: " + this.localVariables + "\n";
    }
}