package org.smoothbuild.lang.runtime;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Field;

public class TestingRuntimeTypes extends RuntimeTypes {
  public TestingRuntimeTypes(ValuesDb valuesDb) {
    super(valuesDb);
    struct("File", list(
        new Field(blob(), "content", unknownLocation()),
        new Field(string(), "path", unknownLocation())));
    struct("Message", list(
        new Field(string(), "text", unknownLocation()),
        new Field(string(), "severity", unknownLocation())));
  }
}
