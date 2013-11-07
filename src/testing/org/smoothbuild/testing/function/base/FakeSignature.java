package org.smoothbuild.testing.function.base;

import static org.smoothbuild.function.base.Name.name;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableList;

public class FakeSignature {
  public static Signature fakeSignature() {
    return fakeSignature("name");
  }

  public static Signature fakeSignature(String name) {
    Type type = Type.STRING;
    Name simpleName = name(name);
    ImmutableList<Param> params = ImmutableList.of();

    return new Signature(type, simpleName, params);
  }
}
