package org.smoothbuild.parse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;
import static org.smoothbuild.testing.parse.TestDependency.dependencies;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.parse.err.UndefinedFunctionError;

import com.google.common.collect.Maps;

public class UndefinedFunctionsDetectorTest {
  MessageListener messageListener = mock(MessageListener.class);
  SymbolTable importedFunctions = mock(SymbolTable.class);

  @Test
  public void emptyFunctionSetHasNoProblems() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    detectUndefinedFunctions(messageListener, importedFunctions, map);
    verifyZeroInteractions(messageListener);
  }

  @Test
  public void referenceToImportedFunction() {
    String imported = "imported";
    when(importedFunctions.containsFunction(imported)).thenReturn(true);
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies(imported));

    detectUndefinedFunctions(messageListener, importedFunctions, map);
    verifyZeroInteractions(messageListener);
  }

  @Test
  public void referenceToDefinedFunction() {
    String fun2 = "function2";
    String fun1 = "function1";
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(fun1, dependencies(fun2));
    map.put(fun2, dependencies(fun1));

    detectUndefinedFunctions(messageListener, importedFunctions, map);
    verifyZeroInteractions(messageListener);
  }

  @Test
  public void referenceToUndefinedFunctionReportsProblem() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put("function1", dependencies("function2"));

    detectUndefinedFunctions(messageListener, importedFunctions, map);
    verify(messageListener).report(Matchers.isA(UndefinedFunctionError.class));
  }
}
