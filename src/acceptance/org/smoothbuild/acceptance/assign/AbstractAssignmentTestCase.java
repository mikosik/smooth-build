package org.smoothbuild.acceptance.assign;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import com.googlecode.junittoolbox.ParallelRunner;

@RunWith(ParallelRunner.class)
public abstract class AbstractAssignmentTestCase {
  @Test
  public void assignment_tests() throws IOException {
    illegalAssignment("Nothing", "'abc'", "String", "");
    illegalAssignment("Nothing", "MyStruct('abc')", "MyStruct", "MyStruct{String field}");
    illegalAssignment("Nothing", "['abc']", "[String]", "");
    illegalAssignment("Nothing", "[MyStruct('abc')]", "[MyStruct]", "MyStruct{String field}");

    allowedAssignment("String", "'abc'", "",
        test -> assertEquals("abc", test.artifactArray("result")));
    allowedAssignment("String", "MyStruct('abc')", "MyStruct{String field}\n",
        test -> assertEquals("abc", test.artifactArray("result")));
    illegalAssignment("String", "['abc']", "[String]", "");
    illegalAssignment("String", "[MyStruct('abc')]", "[MyStruct]", "MyStruct{String field}");
    illegalAssignment("String", "[['abc']]", "[[String]]", "");
    illegalAssignment("String", "[[MyStruct('abc')]]", "[[MyStruct]]", "MyStruct{String field}");

    illegalAssignment("Blob", "'abc'", "String", "");
    illegalAssignment("Blob", "MyStruct('abc')", "MyStruct", "MyStruct{String field}");
    illegalAssignment("Blob", "['abc']", "[String]", "");
    illegalAssignment("Blob", "[MyStruct('abc')]", "[MyStruct]", "MyStruct{String field}");

    illegalAssignment("[Nothing]", "'abc'", "String", "");
    illegalAssignment("[Nothing]", "MyStruct('abc')", "MyStruct", "MyStruct{String field}");
    illegalAssignment("[Nothing]", "['abc']", "[String]", "");
    illegalAssignment("[Nothing]", "[MyStruct('abc')]", "[MyStruct]", "MyStruct{String field}");

    illegalAssignment("[String]", "'abc'", "String", "");
    illegalAssignment("[String]", "MyStruct('abc')", "MyStruct", "MyStruct{String field}");
    allowedAssignment("[String]", "[]", "",
        test -> assertEquals(list(), test.artifactArray("result")));
    allowedAssignment("[String]", "['abc', 'def']", "",
        test -> assertEquals(list("abc", "def"), test.artifactArray("result")));
    allowedAssignment("[String]", "[MyStruct('abc')]", "MyStruct{String field}\n",
        test -> assertEquals(list("abc"), test.artifactArray("result")));
    illegalAssignment("[String]", "[['abc']]", "[[String]]", "");

    allowedAssignment("[Blob]", "[]", "",
        test -> assertEquals(list(), test.artifactArray("result")));

    allowedAssignment("[MyStruct]", "[]", "MyStruct{String field}\n",
        test -> assertEquals(list(), test.artifactArray("result")));

    allowedAssignment("[[String]]", "[]", "",
        test -> assertEquals(list(), test.artifactArray("result")));
    allowedAssignment("[[String]]", "[[]]", "",
        test -> assertEquals(list(list()), test.artifactArray("result")));

    allowedAssignment("[[[String]]]", "[]", "",
        test -> assertEquals(list(), test.artifactArray("result")));
    allowedAssignment("[[[String]]]", "[[]]", "",
        test -> assertEquals(list(list()), test.artifactArray("result")));
    allowedAssignment("[[[String]]]", "[[[]]]", "",
        test -> assertEquals(list(list(list())), test.artifactArray("result")));
  }

  private void allowedAssignment(String type, String value, String declarations,
      Consumer<AcceptanceTestCase> artifactAsserter) throws IOException {
    executeTest(type, value, declarations, test -> {
      test.thenFinishedWithSuccess();
      artifactAsserter.accept(test);
    });
  }

  private void illegalAssignment(String type, String value, String valueType,
      String declarations) throws IOException {
    executeTest(type, value, declarations, (test) -> {
      test.thenFinishedWithError();
      thenAssignmentError(test, type, valueType);
    });
  }

  private void executeTest(String type, String value, String declarations,
      Consumer<AcceptanceTestCase> asserter) throws IOException {
    AcceptanceTestCase test = new AcceptanceTestCase() {};
    test.init();
    try {
      test.givenScript(createTestScript(type, value) + "\n" + declarations);
      test.whenSmoothBuild("result");
      asserter.accept(test);
    } finally {
      test.destroy();
    }
  }

  protected abstract String createTestScript(String type, String value);

  protected abstract void thenAssignmentError(AcceptanceTestCase test, String type, String value);
}
