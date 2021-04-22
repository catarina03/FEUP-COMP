import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableManager implements SymbolTable {
    private String className;
    private Boolean classExtends;
    private String classSuper;
    private ArrayList<String> imports;
    private ArrayList<Symbol> classFields;
    private HashMap<String, ClassMethod> classMethods;

    @Override
    public List<String> getImports() {
        return this.imports;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getSuper() {
        if (this.classExtends){
            return this.classSuper;
        }
        else{
            return null;
        }
    }

    @Override
    public List<Symbol> getFields() {
        return this.classFields;
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(this.classMethods.keySet());
    }

    @Override
    public Type getReturnType(String methodName) {
        return this.classMethods.get(methodName).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return this.classMethods.get(methodName).getMethodParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return this.classMethods.get(methodName).getLocalVariables();
    }
}
