package org.smoothbuild.testing.lang.function.base;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.type.STypes.STRING;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.type.SString;

import com.google.common.collect.ImmutableList;

public class FakeSignature {
  public static Signature<SString> fakeSignature() {
    return fakeSignature("name");
  }

  public static Signature<SString> fakeSignature(String name) {
    return fakeSignature(name(name));
  }

  public static Signature<SString> fakeSignature(Name name) {
    return new Signature<>(STRING, name, ImmutableList.<Param> of());
  }
}
