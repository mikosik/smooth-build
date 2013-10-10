package org.smoothbuild.testing.function.base;

import static org.smoothbuild.function.base.Name.simpleName;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableList;

public class TestSignature {
  public static Signature testSignature() {
    return testSignature("name");
  }

  public static Signature testSignature(String name) {
    Type type = Type.STRING;
    Name simpleName = simpleName(name);
    ImmutableList<Param> params = ImmutableList.of();

    return new Signature(type, simpleName, params);
  }
}
