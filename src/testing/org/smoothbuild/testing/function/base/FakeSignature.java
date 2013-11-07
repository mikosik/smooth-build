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
    return fakeSignature(name(name));
  }

  public static Signature fakeSignature(Name name) {
    return new Signature(Type.STRING, name, ImmutableList.<Param> of());
  }
}
