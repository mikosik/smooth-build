package org.smoothbuild.parse;

import static org.junit.Assert.fail;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.parse.UnknownFunctionCallsDetector.detectUndefinedFunctions;
import static org.testory.Testory.mock;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.UnknownFunctionCallError;
import org.smoothbuild.testing.message.FakeLoggedMessages;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UnknownFunctionCallsDetectorTest {
  Name name1 = name("function1");
  Name name2 = name("function2");

  FakeLoggedMessages messages = new FakeLoggedMessages();
  Module emptyBuiltinModule = new ImmutableModule(Empty.nameFunctionMap());

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<Name, Set<Dependency>> dependencyMap = Maps.newHashMap();
    detectUndefinedFunctions(messages, emptyBuiltinModule, dependencyMap);
    messages.assertNoProblems();
  }

  @Test
  public void referenceToImportedFunction() {
    Function function = mock(Function.class);
    Module builtinModule = new ImmutableModule(ImmutableMap.of(name2, function));
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2));

    detectUndefinedFunctions(messages, builtinModule, dependencyMap);

    messages.assertNoProblems();
  }

  @Test
  public void referenceToDefinedFunction() {
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2), name2,
        dependencies(name1));

    detectUndefinedFunctions(messages, emptyBuiltinModule, dependencyMap);

    messages.assertNoProblems();
  }

  @Test
  public void referenceToUndefinedFunctionIsLoggedAsError() {
    Map<Name, Set<Dependency>> dependencyMap = ImmutableMap.of(name1, dependencies(name2));

    try {
      detectUndefinedFunctions(messages, emptyBuiltinModule, dependencyMap);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
    }

    messages.assertContainsOnly(UnknownFunctionCallError.class);
  }

  private static Set<Dependency> dependencies(Name... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (Name name : names) {
      result.add(new Dependency(codeLocation(1), name));
    }
    return result;
  }
}
