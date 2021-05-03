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

    public SymbolTableManager(){
        this.className = "";
        this.classExtends = false;
        this.classSuper = "";
        this.imports = new ArrayList<>();
        this.classFields = new ArrayList<>();
        this.classMethods = new HashMap<>();
    }

    @Override
    public List<String> getImports() {
        return this.imports;
    }

    public void addImports(String importObject){
        imports.add(importObject);
    }
    
    public void addMethod(ClassMethod classMethod){
        classMethods.putIfAbsent(classMethod.getMethodName(), classMethod);
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className){
        this.className = className;
    }

    public Boolean getClassExtends() {
        return classExtends;
    }

    public void setClassExtends(Boolean classExtends){
        this.classExtends = classExtends;
    }

    public void setClassSuper(String classSuper){
        this.classSuper = classSuper;
    }


    @Override
    public String getSuper() {
        if (this.getClassExtends()){
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

    public void addField(Symbol field){
        this.classFields.add(field);
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(this.classMethods.keySet());
    }

    public ClassMethod getMethod(String methodKey){
        return this.classMethods.get(methodKey);
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

    @Override
    public String toString(){
        return "ClassName: " + this.className + "\n" +
                "Extends a class: " + this.classExtends + "\n" +
                "Class super: " + this.classSuper + "\n" + 
                "Imports: " + this.imports + "\n" + 
                "Class fields: " + this.classFields + "\n" + 
                "Class methods: " + this.classMethods + "\n";
    }
}
