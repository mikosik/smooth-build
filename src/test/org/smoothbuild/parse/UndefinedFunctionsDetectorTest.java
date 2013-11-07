package org.smoothbuild.parse;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.ImmutableModule;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.UndefinedFunctionError;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class UndefinedFunctionsDetectorTest {
  Name name1 = name("function1");
  Name name2 = name("function2");

  FakeMessageGroup messageGroup = new FakeMessageGroup();
  Module emptyBuiltinModule = new ImmutableModule(Empty.nameToFunctionMap());

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<Name, Set<Dependency>> dependencyMap = Maps.newHashMap();
    detectUndefinedFunctions(messageGroup, emptyBuiltinModule, dependencyMap);
    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToImportedFunction() {
    Module builtinModule = new ImmutableModule(ImmutableMap.of(name2, mock(Function.class)));
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2));

    detectUndefinedFunctions(messageGroup, builtinModule, dependencyMap);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToDefinedFunction() {
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2), name2,
        dependencies(name1));

    detectUndefinedFunctions(messageGroup, emptyBuiltinModule, dependencyMap);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToUndefinedFunctionReportsProblem() {
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2));

    try {
      detectUndefinedFunctions(messageGroup, emptyBuiltinModule, dependencyMap);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
    }

    messageGroup.assertOnlyProblem(UndefinedFunctionError.class);
  }
}
