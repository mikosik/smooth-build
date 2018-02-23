package org.smoothbuild.lang.message;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.TestingValuesDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.collect.ImmutableMap;

public class TestingMessagesDb extends MessagesDb {
  public TestingMessagesDb() {
    this(new TestingHashedDb());
  }

  public TestingMessagesDb(HashedDb hashedDb) {
    this(new TestingValuesDb(hashedDb), new RuntimeTypes(new TypesDb(hashedDb)));
  }

  public TestingMessagesDb(ValuesDb valuesDb, RuntimeTypes types) {
    super(valuesDb, types);
    types.struct("Message", ImmutableMap.of(TEXT, types.string(), SEVERITY, types.string()));
  }
}
