package org.smoothbuild.testing.lang.function.base;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;

import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.util.Empty;

public class FakeSignature {
  public static Signature<SString> fakeSignature() {
    return fakeSignature("name");
  }

  public static Signature<SString> fakeSignature(String name) {
    return fakeSignature(name(name));
  }

  public static Signature<SString> fakeSignature(Name name) {
    return new Signature<>(STRING, name, Empty.paramList());
  }
}
