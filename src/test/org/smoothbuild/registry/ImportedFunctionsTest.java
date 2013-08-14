package org.smoothbuild.registry;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.CanonicalName.canonicalName;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.Function;

public class ImportedFunctionsTest {
  ImportedFunctions importedFunctions = new ImportedFunctions();

  @Test
  public void doesNotContainNotAddedType() throws Exception {
    assertThat(importedFunctions.contains("nameA")).isFalse();
  }

  @Test
  public void throwsExceptionWhenQueriedForNotRegisteredType() throws Exception {
    try {
      importedFunctions.get("abc");
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void containsImportedFunction() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    String name = "nameA";
    importedFunctions.add(function(name));
    assertThat(importedFunctions.contains(name)).isTrue();
  }

  @Test
  public void returnsAddedType() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    String name = "nameA";
    Function function = function(name);

    importedFunctions.add(function);

    assertThat(importedFunctions.get(name)).isEqualTo(function);
  }

  @Test
  public void cannotRegisterTwiceUnderTheSameName() throws Exception {
    String name = "nameA";

    importedFunctions.add(function(name));
    try {
      importedFunctions.add(function(name));
      Assert.fail("exception should be thrown");
    } catch (FunctionAlreadyRegisteredException e) {
      // expected
    }
  }

  public static class MyFunction implements FunctionDefinition {
    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  public static class MyFunction2 implements FunctionDefinition {
    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  private static Function function(String name) {
    return new Function(canonicalName(name), Type.STRING, null);
  }
}
