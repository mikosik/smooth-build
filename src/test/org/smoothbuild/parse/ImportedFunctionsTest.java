package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.FullyQualifiedName.fullyQualifiedName;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.parse.ImportedFunctions;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.Function;

public class ImportedFunctionsTest {
  ImportedFunctions importedFunctions = new ImportedFunctions();

  @Test
  public void doesNotContainNotAddedType() throws Exception {
    assertThat(importedFunctions.containsFunction("nameA")).isFalse();
  }

  @Test
  public void throwsExceptionWhenQueriedForNotRegisteredType() throws Exception {
    try {
      importedFunctions.getFunction("abc");
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
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
    Function function = function(name);

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
    return new Function(fullyQualifiedName(name), Type.STRING, null);
  }
}
