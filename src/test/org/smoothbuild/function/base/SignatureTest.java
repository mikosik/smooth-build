package org.smoothbuild.function.base;

import static org.smoothbuild.function.base.QualifiedName.simpleName;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SignatureTest {
  Type type = Type.STRING;
  QualifiedName name = simpleName("name");
  ImmutableMap<String, Param> params = ImmutableMap.<String, Param> of();

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() {
    new Signature(null, name, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() {
    new Signature(type, null, params);
  }

  @Test(expected = NullPointerException.class)
  public void nullParamsIsForbidden() {
    new Signature(type, name, null);
  }
}
