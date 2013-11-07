package org.smoothbuild.parse;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.UndefinedFunctionError;
import org.smoothbuild.testing.message.FakeMessageGroup;

import com.google.common.collect.Maps;

public class UndefinedFunctionsDetectorTest {
  Name name1 = name("function1");
  Name name2 = name("function2");

  FakeMessageGroup messageGroup = new FakeMessageGroup();
  SymbolTable importedFunctions = mock(SymbolTable.class);

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    detectUndefinedFunctions(messageGroup, importedFunctions, map);
    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToImportedFunction() {
    Name imported = name("imported");
    when(importedFunctions.containsFunction(imported.value())).thenReturn(true);
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(imported));

    detectUndefinedFunctions(messageGroup, importedFunctions, map);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToDefinedFunction() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(name2));
    map.put(name2, dependencies(name1));

    detectUndefinedFunctions(messageGroup, importedFunctions, map);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToUndefinedFunctionReportsProblem() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(name2));

    try {
      detectUndefinedFunctions(messageGroup, importedFunctions, map);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
    }

    messageGroup.assertOnlyProblem(UndefinedFunctionError.class);
  }
}
