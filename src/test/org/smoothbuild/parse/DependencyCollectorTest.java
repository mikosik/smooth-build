package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.testing.parse.TestDependency.dependencies;
import static org.smoothbuild.testing.parse.TestModule.module;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.testing.parse.TestFunction;
import org.smoothbuild.testing.parse.TestModule;
import org.smoothbuild.testing.parse.TestPipe;

import com.google.common.collect.Maps;

public class DependencyCollectorTest {

  @Test
  public void emptyMapIsReturnedForEmptyModule() throws Exception {
    assertThat(collectDependencies(module())).isEmpty();
  }

  @Test
  public void singleFunction() {
    String name = "funcation1";
    String dep1 = "dep1";
    String dep2 = "dep2";

    TestModule module = module();
    TestFunction function = module.addFunction(name);
    TestPipe pipe = function.addPipeExpression();
    pipe.addFunctionCall(dep1);
    pipe.addFunctionCall(dep2);

    Map<String, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name, dependencies(dep1, dep2));

    assertThat(collectDependencies(module)).isEqualTo(expected);
  }

  @Test
  public void twoFunctionsWithCycle() {
    String name1 = "funcation1";
    String name2 = "funcation2";

    TestModule module = module();
    {
      TestFunction function = module.addFunction(name1);
      TestPipe pipe = function.addPipeExpression();
      pipe.addFunctionCall(name2);
    }
    {
      TestFunction function = module.addFunction(name2);
      TestPipe pipe = function.addPipeExpression();
      pipe.addFunctionCall(name1);
    }

    Map<String, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2));
    expected.put(name2, dependencies(name1));

    assertThat(collectDependencies(module)).isEqualTo(expected);
  }
}
