import pt.up.fe.comp.jmm.report.Report;

import java.util.List;

public class Analyser {
    private SymbolTableManager symbolTable;
    private List<Report> reports;
    private String result = "";

    public Analyser(SymbolTableManager symbolTable, List<Report> reports) {
        this.symbolTable = symbolTable;
        this.reports = reports;
    }

    public SymbolTableManager getSymbolTable() {
        return symbolTable;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void addReport(Report report){
        reports.add(report);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
