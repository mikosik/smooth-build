package org.smoothbuild.function.base;

import static org.smoothbuild.function.base.Name.simpleName;

import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class TestSignature {
  public static Signature testSignature() {
    return testSignature("name");
  }

  public static Signature testSignature(String name) {
    Type type = Type.STRING;
    Name simpleName = simpleName(name);
    ImmutableMap<String, Param> params = Empty.stringParamMap();

    return new Signature(type, simpleName, params);
  }
}
