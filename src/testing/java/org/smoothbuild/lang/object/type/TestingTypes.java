package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TestingTypes {
  private static final TestingContext context = new TestingContext();
  public static final ObjectDb objectDb = context.objectDb();
  public static final ConcreteType type = objectDb.typeType();
  public static final ConcreteType bool = objectDb.boolType();
  public static final ConcreteType string = objectDb.stringType();
  public static final ConcreteType blob = objectDb.blobType();
  public static final ConcreteType nothing = objectDb.nothingType();
  public static final StructType person = context.personType();
  public static final StructType file = context.fileType();

  public static final ConcreteArrayType arrayType = array(type);
  public static final ConcreteArrayType arrayBool = array(bool);
  public static final ConcreteArrayType arrayString = array(string);
  public static final ConcreteArrayType arrayBlob = array(blob);
  public static final ConcreteArrayType arrayNothing = array(nothing);
  public static final ConcreteArrayType arrayPerson = array(person);
  public static final ConcreteArrayType arrayFile = array(file);

  public static final ConcreteArrayType array2Type = array(arrayType);
  public static final ConcreteArrayType array2Bool = array(arrayBool);
  public static final ConcreteArrayType array2String = array(arrayString);
  public static final ConcreteArrayType array2Blob = array(arrayBlob);
  public static final ConcreteArrayType array2Nothing = array(arrayNothing);
  public static final ConcreteArrayType array2Person = array(arrayPerson);
  public static final ConcreteArrayType array2File = array(arrayFile);

  private static ConcreteArrayType array(ConcreteType elemType) {
    return objectDb.arrayType(elemType);
  }
}
