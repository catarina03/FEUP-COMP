import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.io.StringReader;

import pt.up.fe.comp.TestUtils;

public class ExampleTest {


    @Test
    public void testExpression() {		
		//assertEquals("Program", TestUtils.parse("2+3\n").getRootNode().getKind());		
    //assertEquals("While", TestUtils.parse("while(true)\n").getRootNode().getKind());	
    TestUtils.parse("while(true)\n");	
    System.out.println(TestUtils.parse("while(true)\n").getRootNode().getKind());
	}

}
