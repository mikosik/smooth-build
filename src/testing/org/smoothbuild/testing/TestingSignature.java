package org.smoothbuild.testing;

import static org.smoothbuild.function.base.QualifiedName.simpleName;

import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableMap;

public class TestingSignature {
  public static Signature testingSignature() {
    return testingSignature("name");
  }

  public static Signature testingSignature(String name) {
    Type type = Type.STRING;
    QualifiedName simpleName = simpleName(name);
    ImmutableMap<String, Param> params = ImmutableMap.<String, Param> of();

    return new Signature(type, simpleName, params);
  }
}
