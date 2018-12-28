package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.Field;

public class TestingTypes {
  public static final TypesDb typesDb = new TestingTypesDb();
  public static final ConcreteType type = typesDb.type();
  public static final ConcreteType bool = typesDb.bool();
  public static final ConcreteType string = typesDb.string();
  public static final ConcreteType blob = typesDb.blob();
  public static final ConcreteType nothing = typesDb.nothing();
  public static final StructType person = personType(typesDb);
  public static final StructType file = file();
  public static final GenericType a = new GenericType("a");
  public static final GenericType b = new GenericType("b");

  public static final ConcreteArrayType arrayType = array(type);
  public static final ConcreteArrayType arrayBool = array(bool);
  public static final ConcreteArrayType arrayString = array(string);
  public static final ConcreteArrayType arrayBlob = array(blob);
  public static final ConcreteArrayType arrayNothing = array(nothing);
  public static final ConcreteArrayType arrayPerson = array(person);
  public static final ConcreteArrayType arrayFile = array(file);
  public static final GenericArrayType arrayA = array(a);
  public static final GenericArrayType arrayB = array(b);

  public static final ConcreteArrayType array2Type = array(arrayType);
  public static final ConcreteArrayType array2Bool = array(arrayBool);
  public static final ConcreteArrayType array2String = array(arrayString);
  public static final ConcreteArrayType array2Blob = array(arrayBlob);
  public static final ConcreteArrayType array2Nothing = array(arrayNothing);
  public static final ConcreteArrayType array2Person = array(arrayPerson);
  public static final ConcreteArrayType array2File = array(arrayFile);
  public static final GenericArrayType array2A = array(arrayA);
  public static final GenericArrayType array2B = array(arrayB);

  private static ConcreteArrayType array(ConcreteType elemType) {
    return typesDb.array(elemType);
  }

  private static GenericArrayType array(GenericType elemType) {
    return new GenericArrayType(elemType);
  }

  public static StructType personType(TypesDb typesDb) {
    ConcreteType string = typesDb.string();
    return typesDb.struct("Person", list(
        new Field(string, "firstName", unknownLocation()),
        new Field(string, "lastName", unknownLocation())));
  }

  private static StructType file() {
    return typesDb.struct("File", list(
        new Field(blob, "content", unknownLocation()),
        new Field(string, "path", unknownLocation())));
  }
}
