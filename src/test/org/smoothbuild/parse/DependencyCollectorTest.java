package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;
import static org.smoothbuild.testing.parse.FakeModule.module;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.parse.FakeFunction;
import org.smoothbuild.testing.parse.FakeModule;
import org.smoothbuild.testing.parse.FakePipe;

import com.google.common.collect.Maps;

public class DependencyCollectorTest {
  Name name1 = name("funcation1");
  Name name2 = name("funcation2");
  Name name3 = name("funcation3");

  FakeMessageGroup messages = new FakeMessageGroup();

  @Test
  public void emptyMapIsReturnedForEmptyModule() throws Exception {
    assertThat(collectDependencies(messages, module())).isEmpty();
  }

  @Test
  public void singleFunction() {

    FakeModule module = module();
    FakeFunction function = module.addFunction(name1.value());
    FakePipe pipe = function.addPipeExpression();
    pipe.addFunctionCall(name2.value());
    pipe.addFunctionCall(name3.value());

    Map<Name, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2, name3));

    assertThat(collectDependencies(messages, module)).isEqualTo(expected);
  }

  @Test
  public void twoFunctionsWithCycle() {
    FakeModule module = module();
    {
      FakeFunction function = module.addFunction(name1.value());
      FakePipe pipe = function.addPipeExpression();
      pipe.addFunctionCall(name2.value());
    }
    {
      FakeFunction function = module.addFunction(name2.value());
      FakePipe pipe = function.addPipeExpression();
      pipe.addFunctionCall(name1.value());
    }

    Map<Name, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2));
    expected.put(name2, dependencies(name1));

    assertThat(collectDependencies(messages, module)).isEqualTo(expected);
  }
}
