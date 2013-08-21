package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.FullyQualifiedName.fullyQualifiedName;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.function.Function;
import org.smoothbuild.function.FunctionSignature;
import org.smoothbuild.function.exc.FunctionImplementationException;
import org.smoothbuild.lang.function.Type;

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

  private static Function function(String name) {
    FunctionSignature signature = new FunctionSignature(Type.STRING, fullyQualifiedName(name), null);
    return new Function(signature, null);
  }
}
