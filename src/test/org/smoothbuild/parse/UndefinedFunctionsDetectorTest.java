package org.smoothbuild.parse;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;
import static org.smoothbuild.testing.parse.TestDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.UndefinedFunctionError;
import org.smoothbuild.testing.message.TestMessageGroup;

import com.google.common.collect.Maps;

public class UndefinedFunctionsDetectorTest {
  TestMessageGroup messageGroup = new TestMessageGroup();
  SymbolTable importedFunctions = mock(SymbolTable.class);

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    detectUndefinedFunctions(messageGroup, importedFunctions, map);
    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToImportedFunction() {
    String imported = "imported";
    when(importedFunctions.containsFunction(imported)).thenReturn(true);
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies(imported));

    detectUndefinedFunctions(messageGroup, importedFunctions, map);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToDefinedFunction() {
    String fun2 = "function2";
    String fun1 = "function1";
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(fun1, dependencies(fun2));
    map.put(fun2, dependencies(fun1));

    detectUndefinedFunctions(messageGroup, importedFunctions, map);

    messageGroup.assertNoProblems();
  }

  @Test
  public void referenceToUndefinedFunctionReportsProblem() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies("function2"));

    try {
      detectUndefinedFunctions(messageGroup, importedFunctions, map);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
    }

    messageGroup.assertOnlyProblem(UndefinedFunctionError.class);
  }
}
