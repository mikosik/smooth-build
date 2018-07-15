package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

public class TestingStructType {
  public static StructType personType(TypesDb typesDb) {
    Type string = typesDb.string();
    return typesDb.struct("Person", list(
        new Field(string, "firstName", unknownLocation()),
        new Field(string, "lastName", unknownLocation())));
  }
}
