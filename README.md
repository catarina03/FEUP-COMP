# Compilers Project

## Evaluation 
**GROUP: <identifier of the group>**

| Name | Student number | Grade | Contribution |
| ---- | -------------- | ----- | ------------ |
| Alexandra Ferreira | 201806784 | 15 | 11% |
| Catarina Fernandes | 201806610 | 17 | 32% |
| Flávia Carvalhido | 201806857 | 17 | 32% |
| Teresa Corado | 201806479 | 16 | 25% |

**GLOBAL Grade of the project: 16**

**SUMMARY: (Describe what your tool does and its main features.)**
The tool built serves as a compiler of .jmm files, written in the Java-- language, a subset of the Java language. All programs that are valid in the Java-- language are also valid in the Java language. 
The generated AST and symbol table, all together are tools for the next layers of the compiler: semantic analysis, OLLIR and Jasmin generation.
Starting with the grammar, it allows to create a AST with all the nodes contained in the original jmm file, making it easier to traverse the code. The symbol table also stores valuable information about the methods and variables available in each scope of the analysed class.
The compiler generates OLLIR code from the previously generated AST. It is able to parse the OLLIR code and detect errors in it. 
The compiler generates files of the classes with instructions accepted by jasmin, based on the generated OLLIR equivalent. The generated classes given Java-- code can be integrated in a Java application.

**DEALING WITH SYNTACTIC ERRORS:**
- Deals with up to 10 errors inside the while condition
- Generates a report for each of those errors
- After more than 10 errors throws a RuntimeException

**SEMANTIC ANALYSIS:**
- verify if operations are made with operands of the same type (e.g. int + boolean is not possible)
- It's not possible to use arrays directly in arithmetic operations (e.g. array1 + array2)
- Array access has to be done on an array (e.g. 1[10] not allowed) 
- verify if the assignee value is of the same type of the assigned (a_int = b_boolean is not allowed)
- Array index has to be an integer (e.g. a[true] not allowed)
- Boolean operations (&&, < ou !) are only done on boolean variables
- Conditional expressions have to result in a boolean
- The number of arguments in the method invocation has to be the same as in the declaration
- The type of parameters has to be the same as the arguments

**CODE GENERATION:**  
OLLIR code is generated from .jmm files, which will be used afterwards to generate the Jasmin code.
In each situation, the program opts by the most efficient instruction, and the limit of the stack and local variables is generated based on the chosen instructions for each method.  

Our code starts by generating the class structure with a constructor, and then the method stubs with parameters and return types, followed by the method bodies such as variable assignments, arithmetic operations and method invocations.
Afterwards, it generates the fields of the class and the incrementation of variables.

**TASK DISTRIBUTION:**
- CHECKPOINT 1 (Everyone did a bit of everything)
    - grammar 
    - error treatment and recovery mechanisms for while conditions
    - generating annotated AST (.jjt file)
- CHECKPOINT 2:
    - implementing symbol table interface (Catarina Fernandes)
    - semantic analysis (Catarina Fernandes, Flávia Carvalhido, Teresa Corado)
    - ollir (Flávia Carvalhido)
    - jasmin (Alexandra Ferreira, Teresa Corado)
- CHECKPOINT 3:
    - ollir - if/else/loops/arrays (Catarina Fernandes, Flávia Carvalhido)
    - jasmin (Alexandra Ferreira, Catarina Fernandes, Flávia Carvalhido, Teresa Corado)


**PROS: (Identify the most positive aspects of your tool)**
- LL1 grammar with only 1 local lookahead
- Generates reports for errors inside the while condtion
- Only throws a RuntimeException after 10 errors inside the while condition
  - Semantic analysis
    
- Is able to access variables from all scopes
- Can access other functions in the same class and perform operations inside it
- Can perform arithmetic and boolean operations
- Can access libraries and use them in the code (for example: showing output through the ioPlus library)
- [Optimization] Using instrution types such as bipush, sipush and tldc

**CONS: (Identify the most negative aspects of your tool)**
- Semantic analysis is not fully implemented
- Ollir cannot deal with array access in expressions and has some problem with dot method calls inside conditions
- Jasmin code cannot deal with if/else conditions, loops and array access

## Requirements

For this project, you need to [install Gradle](https://gradle.org/install/)

## Project setup

Copy your ``.jjt`` file to the ``javacc`` folder. If you change any of the classes generated by ``jjtree`` or ``javacc``, you also need to copy them to the ``javacc`` folder.

Copy your source files to the ``src`` folder, and your JUnit test files to the ``test`` folder.

## Compile

To compile the program, run ``gradle build``. This will compile your classes to ``classes/main/java`` and copy the JAR file to the root directory. The JAR file will have the same name as the repository folder.

### Run

To run you have two options: Run the ``.class`` files or run the JAR.

### Run ``.class``

To run the ``.class`` files, do the following:

```cmd
java -cp "./build/classes/java/main/" <class_name> <arguments>
```

Where ``<class_name>`` is the name of the class you want to run and ``<arguments>`` are the arguments to be passed to ``main()``.

### Run ``.jar``

To run the JAR, do the following command:

```cmd
java -jar <jar filename> <arguments>
```

Where ``<jar filename>`` is the name of the JAR file that has been copied to the root folder, and ``<arguments>`` are the arguments to be passed to ``main()``.

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening ``build/reports/tests/test/index.html``.

## Checkpoint 1
For the first checkpoint the following is required:

1. Convert the provided e-BNF grammar into JavaCC grammar format in a .jj file
2. Resolve grammar conflicts (projects with global LOOKAHEAD > 1 will have a penalty)
3. Proceed with error treatment and recovery mechanisms for the while expression
4. Convert the .jj file into a .jjt file
5. Include missing information in nodes (i.e. tree annotation). E.g. include class name in the class Node.
6. Generate a JSON from the AST

### JavaCC to JSON
To help converting the JavaCC nodes into a JSON format, we included in this project the JmmNode interface, which can be seen in ``src-lib/pt/up/fe/comp/jmm/JmmNode.java``. The idea is for you to use this interface along with your SimpleNode class. Then, one can easily convert the JmmNode into a JSON string by invoking the method JmmNode.toJson().

Please check the SimpleNode included in this repository to see an example of how the interface can be implemented, which implements all methods except for the ones related to node attributes. How you should store the attributes in the node is left as an exercise.

### Reports
We also included in this project the class ``src-lib/pt/up/fe/comp/jmm/report/Report.java``. This class is used to generate important reports, including error and warning messages, but also can be used to include debugging and logging information. E.g. When you want to generate an error, create a new Report with the ``Error`` type and provide the stage in which the error occurred.


### Parser Interface

We have included the interface ``src-lib/pt/up/fe/comp/jmm/JmmParser.java``, which you should implement in a class that has a constructor with no parameters (please check ``src/Main.java`` for an example). This class will be used to test your parser. The interface has a single method, ``parse``, which receives a String with the code to parse, and returns a JmmParserResult instance. This instance contains the root node of your AST, as well as a List of Report instances that you collected during parsing.

To configure the name of the class that implements the JmmParser interface, use the file ``parser.properties``.