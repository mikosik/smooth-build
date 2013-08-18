package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.parse.TestingDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.testing.parse.TestingFunction;
import org.smoothbuild.testing.parse.TestingModule;
import org.smoothbuild.testing.parse.TestingPipeExpression;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class DependencyCollectorTest {
  ProblemsListener problemsListener = mock(ProblemsListener.class);

  DependencyCollector dependencyCollector = new DependencyCollector(problemsListener);

  @Test
  public void emptyMapIsReturnedForEmptyModule() throws Exception {

    TestingModule module = TestingModule.testingModule();

    dependencyCollector.visitModule(module);

    ImmutableMap<String, Set<Dependency>> expected = ImmutableMap.of();
    assertThat(dependencyCollector.dependencies()).isEqualTo(expected);
  }

  @Test
  public void singleFunction() {
    String name = "funcation1";
    String dep1 = "dep1";
    String dep2 = "dep2";

    TestingModule module = TestingModule.testingModule();
    TestingFunction function = module.addFunction(name);
    TestingPipeExpression pipe = function.addPipeExpression();
    pipe.addFunctionCall(dep1);
    pipe.addFunctionCall(dep2);

    dependencyCollector.visitModule(module);

    Map<String, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name, dependencies(dep1, dep2));

    assertThat(dependencyCollector.dependencies()).isEqualTo(expected);
  }

  @Test
  public void twoFunctionsWithCycle() {
    String name1 = "funcation1";
    String name2 = "funcation2";

    TestingModule module = TestingModule.testingModule();
    {
      TestingFunction function = module.addFunction(name1);
      TestingPipeExpression pipe = function.addPipeExpression();
      pipe.addFunctionCall(name2);
    }
    {
      TestingFunction function = module.addFunction(name2);
      TestingPipeExpression pipe = function.addPipeExpression();
      pipe.addFunctionCall(name1);
    }

    dependencyCollector.visitModule(module);

    Map<String, Set<Dependency>> expected = Maps.newHashMap();
    expected.put(name1, dependencies(name2));
    expected.put(name2, dependencies(name1));

    assertThat(dependencyCollector.dependencies()).isEqualTo(expected);
  }
}
