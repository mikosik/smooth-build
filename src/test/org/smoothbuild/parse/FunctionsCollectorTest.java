package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.parse.TestingFunction.function;
import static org.smoothbuild.testing.parse.TestingImportedFunctions.IMPORTED_NAME;
import static org.smoothbuild.testing.parse.TestingModule.module;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportError;
import org.smoothbuild.testing.parse.TestingImportedFunctions;
import org.smoothbuild.testing.parse.TestingModule;
import org.smoothbuild.testing.problem.TestingProblemsListener;

import com.google.common.collect.ImmutableMap;

public class FunctionsCollectorTest {

  TestingProblemsListener problemsListener = new TestingProblemsListener();
  SymbolTable importedFunctions = new TestingImportedFunctions();

  @Test
  public void visitedFunctionNamesAreReturned() throws Exception {
    String name1 = "functionA";
    String name2 = "functionB";

    FunctionContext function1 = function(name1);
    FunctionContext function2 = function(name2);
    TestingModule module = module(function1, function2);

    ImmutableMap<String, FunctionContext> expected = ImmutableMap.of(name1, function1, name2,
        function2);
    assertThat(collectFunctions(module)).isEqualTo(expected);
  }

  @Test
  public void illegalFunctionNameIsReported() {
    collectFunctions(module(function("function-name")));
    problemsListener.assertOnlyProblem(IllegalFunctionNameError.class);
  }

  @Test
  public void duplicateFunction() throws Exception {
    collectFunctions(module(function("functionA"), function("functionA")));
    problemsListener.assertOnlyProblem(DuplicateFunctionError.class);
  }

  @Test
  public void overridenImport() throws Exception {
    collectFunctions(module(function(IMPORTED_NAME)));
    problemsListener.assertOnlyProblem(OverridenImportError.class);
  }

  private Map<String, FunctionContext> collectFunctions(TestingModule module) {
    return FunctionsCollector.collectFunctions(problemsListener, importedFunctions, module);
  }
}
