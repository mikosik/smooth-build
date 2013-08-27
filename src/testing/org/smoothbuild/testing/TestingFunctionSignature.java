package org.smoothbuild.testing;

import static org.smoothbuild.function.base.FullyQualifiedName.simpleName;

import org.smoothbuild.function.base.FullyQualifiedName;
import org.smoothbuild.function.base.FunctionSignature;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableMap;

public class TestingFunctionSignature {
  public static FunctionSignature testingSignature() {
    return testingSignature("name");
  }

  public static FunctionSignature testingSignature(String name) {
    Type type = Type.STRING;
    FullyQualifiedName simpleName = simpleName(name);
    ImmutableMap<String, Param> params = ImmutableMap.<String, Param> of();

    return new FunctionSignature(type, simpleName, params);
  }
}
