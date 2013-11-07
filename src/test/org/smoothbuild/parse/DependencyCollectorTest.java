package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;
import static org.smoothbuild.testing.parse.FakeModuleContext.moduleCtx;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.parse.FakeFunctionContext;
import org.smoothbuild.testing.parse.FakeModuleContext;
import org.smoothbuild.testing.parse.FakePipeContext;

import com.google.common.collect.Maps;

public class DependencyCollectorTest {
  Name name1 = name("funcation1");
  Name name2 = name("funcation2");
  Name name3 = name("funcation3");

  FakeMessageGroup messages = new FakeMessageGroup();

  @Test
  public void emptyMapIsReturnedForEmptyModule() throws Exception {
    assertThat(collectDependencies(messages, moduleCtx())).isEmpty();
  }

  @Test
  public void singleFunction() {

    FakeModuleContext module = moduleCtx();
    FakeFunctionContext function = module.addFunctionCtx(name1.value());
    FakePipeContext pipe = function.addPipeExpressionCtx();
    pipe.addCallCtx(name2.value());
    pipe.addCallCtx(name3.value());

    Map<Name, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2, name3));

    assertThat(collectDependencies(messages, module)).isEqualTo(expected);
  }

  @Test
  public void twoFunctionsWithCycle() {
    FakeModuleContext module = moduleCtx();
    {
      FakeFunctionContext function = module.addFunctionCtx(name1.value());
      FakePipeContext pipe = function.addPipeExpressionCtx();
      pipe.addCallCtx(name2.value());
    }
    {
      FakeFunctionContext function = module.addFunctionCtx(name2.value());
      FakePipeContext pipe = function.addPipeExpressionCtx();
      pipe.addCallCtx(name1.value());
    }

    Map<Name, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2));
    expected.put(name2, dependencies(name1));

    assertThat(collectDependencies(messages, module)).isEqualTo(expected);
  }
}
