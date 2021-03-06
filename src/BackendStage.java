import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jasmin.MethodParser;
import jasmin.TypeUtils;

import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class BackendStage implements JasminBackend {
    private static final String PARAM_DELIM1 = "(";
    private static final String PARAM_DELIM2 = ")";

    private StringBuilder jasminCode;
    private List<Report> reportList;
    private int locals = 0;
    private int stack = 0;
    private int stackMax = 0;


    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit ollirClass = ollirResult.getOllirClass(); 

        try {
            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            // ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code
            //String jasminCode = ""; // Convert node ...
            jasminCode = new StringBuilder();

            // More reports from this stage
            reportList = new ArrayList<>();

            // TODO: OllirClass needs to implements "getMethods()" with returns a List or Set with
            //    the methods present in the program at hand.

            jasminCode.append(".class public "+ollirClass.getClassName()+"\n");

            if(ollirClass.getSuperClass()==null)
            {
                jasminCode.append(".super java/lang/Object\n");
            }
            else {
                jasminCode.append(".super " + ollirClass.getSuperClass() + "\n");
            }

            //class fields
            ollirClass.getFields().forEach(field ->{
                jasminCode.append(TypeUtils.parseField(field));
            });

            for (var method : ollirClass.getMethods()) {
                MethodParser methodParser = new MethodParser(method, ollirClass);
                jasminCode.append(methodParser.generateJasmin());
            }

            return new JasminResult(ollirResult, jasminCode.toString(), reportList);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }





}
