package org.smoothbuild.function.def;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;

public class DefinedFunctionsCreatorTest {
  Signature signature = testingSignature("name");
  DefinitionNode root = mock(DefinitionNode.class);

  DefinedFunction function = new DefinedFunction(signature, root);

  @Test(expected = NullPointerException.class)
  public void nullRootIsForbidden() {
    new DefinedFunction(signature, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() {
    new DefinedFunction(null, root);
  }
}
