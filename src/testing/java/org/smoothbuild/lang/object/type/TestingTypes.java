package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TestingTypes {
  private static final TestingContext context = new TestingContext();
  public static final ObjectDb objectDb = context.objectDb();
  public static final BinaryType type = objectDb.typeType();
  public static final BinaryType bool = objectDb.boolType();
  public static final BinaryType string = objectDb.stringType();
  public static final BinaryType blob = objectDb.blobType();
  public static final BinaryType nothing = objectDb.nothingType();
  public static final StructType person = context.personType();
  public static final StructType file = context.fileType();

  public static final ArrayType arrayType = array(type);
  public static final ArrayType arrayBool = array(bool);
  public static final ArrayType arrayString = array(string);
  public static final ArrayType arrayBlob = array(blob);
  public static final ArrayType arrayNothing = array(nothing);
  public static final ArrayType arrayPerson = array(person);
  public static final ArrayType arrayFile = array(file);

  public static final ArrayType array2Type = array(arrayType);
  public static final ArrayType array2Bool = array(arrayBool);
  public static final ArrayType array2String = array(arrayString);
  public static final ArrayType array2Blob = array(arrayBlob);
  public static final ArrayType array2Nothing = array(arrayNothing);
  public static final ArrayType array2Person = array(arrayPerson);
  public static final ArrayType array2File = array(arrayFile);

  private static ArrayType array(BinaryType elemType) {
    return objectDb.arrayType(elemType);
  }
}
