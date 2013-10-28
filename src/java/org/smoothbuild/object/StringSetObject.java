package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;

import com.google.common.hash.HashCode;

public class StringSetObject implements StringSet, Value {
  private final ObjectDb objectDb;
  private final HashCode hash;

  public StringSetObject(ObjectDb objectDb, HashCode hash) {
    this.objectDb = checkNotNull(objectDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<StringValue> iterator() {
    return objectDb.stringSetIterable(hash).iterator();
  }
}
