package org.smoothbuild.testing.lang.function.value;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.AbstractValue;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.StringValue;

public class FakeString extends AbstractValue implements StringValue {
  private final String value;

  public FakeString(String value) {
    super(Type.STRING, Hash.string(value));
    this.value = checkNotNull(value);
  }

  @Override
  public String value() {
    return value;
  }
}
