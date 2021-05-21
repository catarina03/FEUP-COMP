package pt.up.fe.comp.jmm.ollir;

import java.util.Collections;
import java.util.List;

import org.specs.comp.ollir.ClassUnit;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsCollections;

/**
 * An OLLIR result returns the parsed OLLIR code and the corresponding symbol table.
 */
public class OllirResult {

//<<<<<<< Updated upstream
//=======
//    private final String ollirCode;
//>>>>>>> Stashed changes
    private final ClassUnit ollirClass;
    private final SymbolTable symbolTable;
    private final List<Report> reports;

    public OllirResult(ClassUnit ollirClass, SymbolTable symbolTable, List<Report> reports) {
//<<<<<<< Updated upstream
//=======

//>>>>>>> Stashed changes
        this.ollirClass = ollirClass;
        this.symbolTable = symbolTable;
        this.reports = reports;
    }

//<<<<<<< Updated upstream
//=======
    public OllirResult(String ollirCode) {
   //     this.ollirCode = ollirCode;
        this.ollirClass = OllirUtils.parse(ollirCode);
        this.symbolTable = null;
        this.reports = Collections.emptyList();
    }

//>>>>>>> Stashed changes
    /**
     * Creates a new instance from the analysis stage results and a String containing OLLIR code.
     * 
     * @param semanticsResult
     * @param ollirCode
     * @param reports
     */
    public OllirResult(JmmSemanticsResult semanticsResult, String ollirCode, List<Report> reports) {
//<<<<<<< Updated upstream
        this(OllirUtils.parse(ollirCode), semanticsResult.getSymbolTable(),
                SpecsCollections.concat(semanticsResult.getReports(), reports));
    }

//=======
      /*  this( OllirUtils.parse(ollirCode), semanticsResult.getSymbolTable(),
                SpecsCollections.concat(semanticsResult.getReports(), reports));
    }*/

    //public String getOllirCode() {
      //  return ollirCode;
    //}

//>>>>>>> Stashed changes
    public ClassUnit getOllirClass() {
        return this.ollirClass;
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public List<Report> getReports() {
        return this.reports;
    }
}
