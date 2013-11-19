package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.testing.parse.FakeFunctionContext.functionCtx;
import static org.smoothbuild.testing.parse.FakeModuleContext.moduleCtx;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.ImmutableModule;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.parse.FakeModuleContext;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class FunctionsCollectorTest {
  Name name1 = name("funcation1");
  Name name2 = name("funcation2");

  FakeMessageGroup messages = new FakeMessageGroup();

  @Test
  public void visitedFunctionNamesAreReturned() throws Exception {
    FunctionContext function1 = functionCtx(name1.value());
    FunctionContext function2 = functionCtx(name2.value());
    FakeModuleContext module = moduleCtx(function1, function2);

    ImmutableMap<Name, FunctionContext> expected = ImmutableMap.of(name1, function1, name2,
        function2);
    assertThat(collectFunctions(module)).isEqualTo(expected);
  }

  @Test
  public void illegalFunctionNameIsReported() {
    collectFunctions(moduleCtx(functionCtx("function^name")));
    messages.assertOnlyProblem(IllegalFunctionNameError.class);
  }

  @Test
  public void duplicateFunction() throws Exception {
    collectFunctions(moduleCtx(functionCtx("functionA"), functionCtx("functionA")));
    messages.assertOnlyProblem(DuplicateFunctionError.class);
  }

  @Test
  public void overridenBuiltinFunction() throws Exception {
    Function function = mock(Function.class);
    Module builtinModule = new ImmutableModule(ImmutableMap.of(name1, function));
    collectFunctions(moduleCtx(functionCtx(name1.value())), builtinModule);
    messages.assertOnlyProblem(OverridenBuiltinFunctionError.class);
  }

  private Map<Name, FunctionContext> collectFunctions(FakeModuleContext moduleContext) {
    ImmutableModule emptyBuiltinModule = new ImmutableModule(Empty.nameToFunctionMap());
    return collectFunctions(moduleContext, emptyBuiltinModule);
  }

  private Map<Name, FunctionContext> collectFunctions(FakeModuleContext moduleContext,
      Module builtinModule) {
    try {
      return FunctionsCollector.collectFunctions(messages, builtinModule, moduleContext);
    } catch (PhaseFailedException e) {
      return null;
    }
  }
}
