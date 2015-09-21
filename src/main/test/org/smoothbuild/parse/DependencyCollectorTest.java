package org.smoothbuild.parse;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.testing.parse.FakeModuleContext.moduleCtx;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.testing.parse.FakeFunctionContext;
import org.smoothbuild.testing.parse.FakeModuleContext;
import org.smoothbuild.testing.parse.FakePipeContext;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DependencyCollectorTest {
  Name name1 = name("funcation1");
  Name name2 = name("funcation2");
  Name name3 = name("funcation3");

  LoggedMessages messages = new LoggedMessages();

  @Test
  public void emptyMapIsReturnedForEmptyModule() throws Exception {
    assertThat(collectDependencies(messages, moduleCtx()).keySet(), empty());
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

    assertEquals(expected, collectDependencies(messages, module));
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

    assertEquals(expected, collectDependencies(messages, module));
  }

  private static Set<Dependency> dependencies(Name... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (Name name : names) {
      result.add(new Dependency(codeLocation(1), name));
    }
    return result;
  }
}
