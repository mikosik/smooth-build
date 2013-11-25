package org.smoothbuild.testing.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.STypes.STRING;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.CachedValue;
import org.smoothbuild.lang.type.SString;

public class FakeString extends CachedValue implements SString {
  private final String value;

  public FakeString(String value) {
    super(STRING, Hash.string(value));
    this.value = checkNotNull(value);
  }

  @Override
  public String value() {
    return value;
  }
}
