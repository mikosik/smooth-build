package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;

import java.util.Iterator;

import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.StringValue;

import com.google.common.hash.HashCode;

public class CachedStringSet extends AbstractValue implements Array<StringValue> {
  private final ValueDb valueDb;

  public CachedStringSet(ValueDb valueDb, HashCode hash) {
    super(STRING_SET, hash);
    this.valueDb = checkNotNull(valueDb);
  }

  @Override
  public Iterator<StringValue> iterator() {
    return valueDb.stringSetIterable(hash()).iterator();
  }
}
