package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.parse.FakeFunction.function;
import static org.smoothbuild.testing.parse.FakeImportedFunctions.IMPORTED_NAME;
import static org.smoothbuild.testing.parse.FakeModule.module;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.parse.FakeImportedFunctions;
import org.smoothbuild.testing.parse.FakeModule;

import com.google.common.collect.ImmutableMap;

public class FunctionsCollectorTest {

  FakeMessageGroup messages = new FakeMessageGroup();
  SymbolTable importedFunctions = new FakeImportedFunctions();

  @Test
  public void visitedFunctionNamesAreReturned() throws Exception {
    String name1 = "functionA";
    String name2 = "functionB";

    FunctionContext function1 = function(name1);
    FunctionContext function2 = function(name2);
    FakeModule module = module(function1, function2);

    ImmutableMap<String, FunctionContext> expected = ImmutableMap.of(name1, function1, name2,
        function2);
    assertThat(collectFunctions(module)).isEqualTo(expected);
  }

  @Test
  public void illegalFunctionNameIsReported() {
    collectFunctions(module(function("function-name")));
    messages.assertOnlyProblem(IllegalFunctionNameError.class);
  }

  @Test
  public void duplicateFunction() throws Exception {
    collectFunctions(module(function("functionA"), function("functionA")));
    messages.assertOnlyProblem(DuplicateFunctionError.class);
  }

  @Test
  public void overridenBuiltinFunction() throws Exception {
    collectFunctions(module(function(IMPORTED_NAME)));
    messages.assertOnlyProblem(OverridenBuiltinFunctionError.class);
  }

  private Map<String, FunctionContext> collectFunctions(FakeModule module) {
    try {
      return FunctionsCollector.collectFunctions(messages, importedFunctions, module);
    } catch (PhaseFailedException e) {
      return null;
    }
  }
}
