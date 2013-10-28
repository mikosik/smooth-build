package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;

import com.google.common.hash.HashCode;

public class StringSetObject implements StringSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public StringSetObject(ValueDb valueDb, HashCode hash) {
    this.valueDb = checkNotNull(valueDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<StringValue> iterator() {
    return valueDb.stringSetIterable(hash).iterator();
  }
}
