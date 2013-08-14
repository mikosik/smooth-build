package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.lang.function.CanonicalName;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportWarning;
import org.smoothbuild.registry.ImportedFunctions;
import org.smoothbuild.registry.instantiate.Function;

import com.google.common.collect.ImmutableMap;

public class FunctionsCollectorTest {
  private static final String IMPORTED_FUNCTION_NAME = "importedFunction";
  TestingProblemsListener problemsListener = new TestingProblemsListener();
  ImportedFunctions importedFunctions = createImportedFunctions();

  FunctionsCollector functionsCollector = new FunctionsCollector(problemsListener,
      importedFunctions);

  @Test
  public void visitedFunctionNamesAreReturned() throws Exception {
    String name1 = "functionA";
    String name2 = "functionB";

    FunctionContext function1 = createFunction(name1);
    FunctionContext function2 = createFunction(name2);

    functionsCollector.visitFunction(function1);
    functionsCollector.visitFunction(function2);

    ImmutableMap<String, FunctionContext> expected = ImmutableMap.of(name1, function1, name2,
        function2);
    assertThat(functionsCollector.foundFunctions()).isEqualTo(expected);
  }

  @Test
  public void illegalFunctionNameIsReported() {
    functionsCollector.visitFunction(createFunction("function-name"));
    problemsListener.assertOnlyProblem(IllegalFunctionNameError.class);
  }

  @Test
  public void duplicateFunction() throws Exception {
    functionsCollector.visitFunction(createFunction("functionA"));
    functionsCollector.visitFunction(createFunction("functionA"));

    problemsListener.assertOnlyProblem(DuplicateFunctionError.class);
  }

  @Test
  public void overridenImport() throws Exception {
    functionsCollector.visitFunction(createFunction(IMPORTED_FUNCTION_NAME));
    problemsListener.assertOnlyProblem(OverridenImportWarning.class);
  }

  private FunctionContext createFunction(String name) {
    FunctionContext function = new FunctionContext(null, 0);
    FunctionNameContext functionName = new FunctionNameContext(function, 0);
    functionName.addChild(stringChild(name));
    functionName.start = token();
    functionName.stop = token();
    function.addChild(functionName);
    return function;
  }

  private ParserRuleContext stringChild(final String string) {
    return new ParserRuleContext() {
      @Override
      public String getText() {
        return string;
      }
    };
  }

  private static Token token() {
    CommonToken token = new CommonToken(0);
    token.setStartIndex(13);
    token.setLine(11);
    token.setStopIndex(17);
    return token;
  }

  private static ImportedFunctions createImportedFunctions() {
    ImportedFunctions imported = new ImportedFunctions();
    imported.add(new Function(CanonicalName.simpleName(IMPORTED_FUNCTION_NAME), Type.FILE, null));
    return imported;
  }
}
