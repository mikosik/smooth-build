package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.testing.TestingContext;

public class TestingTypes {
  private static final TestingContext context = new TestingContext();
  public static final ValuesDb valuesDb = context.valuesDb();
  public static final ConcreteType type = valuesDb.typeType();
  public static final ConcreteType bool = valuesDb.boolType();
  public static final ConcreteType string = valuesDb.stringType();
  public static final ConcreteType blob = valuesDb.blobType();
  public static final ConcreteType nothing = valuesDb.nothingType();
  public static final StructType person = context.personType();
  public static final StructType file = context.fileType();
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
    return valuesDb.arrayType(elemType);
  }

  private static GenericArrayType array(GenericType elemType) {
    return new GenericArrayType(elemType);
  }
}
