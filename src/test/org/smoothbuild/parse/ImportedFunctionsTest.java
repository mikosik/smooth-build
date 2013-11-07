package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.function.base.FakeSignature.fakeSignature;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.function.nativ.exc.FunctionImplementationException;

public class ImportedFunctionsTest {
  ImportedFunctions importedFunctions = new ImportedFunctions(mock(TaskDb.class));

  @Test
  public void doesNotContainNotAddedType() throws Exception {
    assertThat(importedFunctions.containsFunction("nameA")).isFalse();
  }

  @Test
  public void returnsNullWhenQueriedForNotRegisteredType() throws Exception {
    assertThat(importedFunctions.getFunction("abc")).isNull();
  }

  @Test
  public void containsImportedFunction() throws FunctionImplementationException {
    String name = "nameA";
    importedFunctions.add(function(name));
    assertThat(importedFunctions.containsFunction(name)).isTrue();
  }

  @Test
  public void returnsAddedType() throws FunctionImplementationException {
    String name = "nameA";
    NativeFunction function = function(name);

    importedFunctions.add(function);

    assertThat(importedFunctions.getFunction(name)).isEqualTo(function);
  }

  @Test
  public void namesReturnsNamesOfAllAddedFunctions() throws FunctionImplementationException {
    String name1 = "name1";
    String name2 = "name2";
    importedFunctions.add(function(name1));
    importedFunctions.add(function(name2));

    assertThat(importedFunctions.names()).containsOnly(name1, name2);
  }

  @Test
  public void cannotRegisterTwiceUnderTheSameName() throws Exception {
    String name = "nameA";

    importedFunctions.add(function(name));
    try {
      importedFunctions.add(function(name));
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  private static NativeFunction function(String name) {
    return new NativeFunction(fakeSignature(name), mock(Invoker.class), false);
  }
}
