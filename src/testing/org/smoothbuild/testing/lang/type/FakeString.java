package org.smoothbuild.testing.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.base.AbstractObject;
import org.smoothbuild.lang.base.SString;

public class FakeString extends AbstractObject implements SString {
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
