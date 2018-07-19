package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.Field;

public class TestingTypes {
  public static final TypesDb typesDb = new TestingTypesDb();
  public static final Type type = typesDb.type();
  public static final Type string = typesDb.string();
  public static final Type blob = typesDb.blob();
  public static final Type nothing = typesDb.nothing();
  public static final Type a = typesDb.generic("a");
  public static final Type b = typesDb.generic("b");
  public static final StructType personType = personType(typesDb);

  public static ArrayType array4(Type elemType) {
    return array(array(array(array(elemType))));
  }

  public static ArrayType array3(Type elemType) {
    return array(array(array(elemType)));
  }

  public static ArrayType array2(Type elemType) {
    return array(array(elemType));
  }

  public static ArrayType array(Type elemType) {
    return typesDb.array(elemType);
  }

  public static StructType personType(TypesDb typesDb) {
    Type string = typesDb.string();
    return typesDb.struct("Person", list(
        new Field(string, "firstName", unknownLocation()),
        new Field(string, "lastName", unknownLocation())));
  }
}
