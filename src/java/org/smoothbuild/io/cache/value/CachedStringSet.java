package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.Type.STRING_SET;

import java.util.Iterator;

import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.StringValue;

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
