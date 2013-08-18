package org.smoothbuild.parse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.parse.TestingDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;
import org.smoothbuild.parse.err.UndefinedFunctionError;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.Maps;

public class UndefinedFunctionsDetectorTest {
  ProblemsListener problemsListener = mock(ProblemsListener.class);
  SymbolTable importedFunctions = mock(SymbolTable.class);
  UndefinedFunctionsDetector detector = new UndefinedFunctionsDetector();

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    detector.detect(importedFunctions, map, problemsListener);
    verifyZeroInteractions(problemsListener);
  }

  @Test
  public void referenceToImportedFunction() {
    String imported = "imported";
    when(importedFunctions.containsFunction(imported)).thenReturn(true);
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies(imported));

    detector.detect(importedFunctions, map, problemsListener);
    verifyZeroInteractions(problemsListener);
  }

  @Test
  public void referenceToDefinedFunction() {
    String fun2 = "function2";
    String fun1 = "function1";
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(fun1, dependencies(fun2));
    map.put(fun2, dependencies(fun1));

    detector.detect(importedFunctions, map, problemsListener);
    verifyZeroInteractions(problemsListener);
  }

  @Test
  public void referenceToUndefinedFunctionReportsProblem() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies("function2"));

    detector.detect(importedFunctions, map, problemsListener);
    verify(problemsListener).report(Matchers.isA(UndefinedFunctionError.class));
  }
}
