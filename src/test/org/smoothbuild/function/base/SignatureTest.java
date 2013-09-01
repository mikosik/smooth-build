package org.smoothbuild.function.base;

import static org.smoothbuild.function.base.Name.simpleName;

import org.junit.Test;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class SignatureTest {
  Type type = Type.STRING;
  Name name = simpleName("name");
  ImmutableMap<String, Param> params = Empty.stringParamMap();

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
