package org.smoothbuild.function.base;

import static org.smoothbuild.function.base.FullyQualifiedName.simpleName;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class FunctionSignatureTest {
  Type type = Type.STRING;
  FullyQualifiedName name = simpleName("name");
  ImmutableMap<String, Param> params = ImmutableMap.<String, Param> of();

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() {
    new FunctionSignature(null, name, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() {
    new FunctionSignature(type, null, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullParamsIsForbidden() {
    new FunctionSignature(type, name, null);
  }
}
