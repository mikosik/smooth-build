package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.testing.parse.TestingFunction.function;
import static org.smoothbuild.testing.parse.TestingImportedFunctions.IMPORTED_NAME;

import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportWarning;
import org.smoothbuild.testing.parse.TestingImportedFunctions;

import com.google.common.collect.ImmutableMap;

public class FunctionsCollectorTest {

  TestingProblemsListener problemsListener = new TestingProblemsListener();
  SymbolTable importedFunctions = new TestingImportedFunctions();

  FunctionsCollector functionsCollector = new FunctionsCollector(problemsListener,
      importedFunctions);

  @Test
  public void visitedFunctionNamesAreReturned() throws Exception {
    String name1 = "functionA";
    String name2 = "functionB";

    FunctionContext function1 = function(name1);
    FunctionContext function2 = function(name2);

    functionsCollector.visitFunction(function1);
    functionsCollector.visitFunction(function2);

    ImmutableMap<String, FunctionContext> expected = ImmutableMap.of(name1, function1, name2,
        function2);
    assertThat(functionsCollector.foundFunctions()).isEqualTo(expected);
  }

  @Test
  public void illegalFunctionNameIsReported() {
    functionsCollector.visitFunction(function("function-name"));
    problemsListener.assertOnlyProblem(IllegalFunctionNameError.class);
  }

  @Test
  public void duplicateFunction() throws Exception {
    functionsCollector.visitFunction(function("functionA"));
    functionsCollector.visitFunction(function("functionA"));

    problemsListener.assertOnlyProblem(DuplicateFunctionError.class);
  }

  @Test
  public void overridenImport() throws Exception {
    functionsCollector.visitFunction(function(IMPORTED_NAME));
    problemsListener.assertOnlyProblem(OverridenImportWarning.class);
  }

}
