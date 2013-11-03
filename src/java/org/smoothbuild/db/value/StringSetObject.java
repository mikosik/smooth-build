package org.smoothbuild.db.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class StringSetObject implements StringSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public StringSetObject(ValueDb valueDb, HashCode hash) {
    this.valueDb = checkNotNull(valueDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.STRING_SET;
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
