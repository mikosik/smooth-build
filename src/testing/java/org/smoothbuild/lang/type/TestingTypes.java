package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.Field;

public class TestingTypes {
  public static final TypesDb typesDb = new TestingTypesDb();
  public static final ConcreteType type = typesDb.type();
  public static final ConcreteType string = typesDb.string();
  public static final ConcreteType blob = typesDb.blob();
  public static final ConcreteType nothing = typesDb.nothing();
  public static final StructType personType = personType(typesDb);
  public static final StructType file = file();
  public static final GenericType a = new GenericType("a");
  public static final GenericType b = new GenericType("b");

  public static ConcreteArrayType array4(ConcreteType elemType) {
    return array(array(array(array(elemType))));
  }

  public static ConcreteArrayType array3(ConcreteType elemType) {
    return array(array(array(elemType)));
  }

  public static ConcreteArrayType array2(ConcreteType elemType) {
    return array(array(elemType));
  }

  public static ConcreteArrayType array(ConcreteType elemType) {
    return typesDb.array(elemType);
  }

  public static GenericArrayType array4(GenericType elemType) {
    return array(array(array(array(elemType))));
  }

  public static GenericArrayType array3(GenericType elemType) {
    return array(array(array(elemType)));
  }

  public static GenericArrayType array2(GenericType elemType) {
    return array(array(elemType));
  }

  public static GenericArrayType array(GenericType elemType) {
    return new GenericArrayType(elemType);
  }

  public static StructType personType(TypesDb typesDb) {
    ConcreteType string = typesDb.string();
    return typesDb.struct("Person", list(
        new Field(string, "firstName", unknownLocation()),
        new Field(string, "lastName", unknownLocation())));
  }

  public static StructType file() {
    return typesDb.struct("File", list(
        new Field(blob, "content", unknownLocation()),
        new Field(string, "path", unknownLocation())));
  }
}
