import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;

public class ClassMethod {
    private String methodName; //? redundant
    private ArrayList<Symbol> methodParameters;
    private Type returnType;
    private ArrayList<Symbol> localVariables;

    public String getMethodName() {
        return this.methodName;
    }

    public ArrayList<Symbol> getMethodParameters() {
        return this.methodParameters;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public ArrayList<Symbol> getLocalVariables() {
        return this.localVariables;
    }


}
