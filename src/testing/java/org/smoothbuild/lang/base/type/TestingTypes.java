package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.ModulePath.modulePath;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.base.type.Types.type;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.lang.base.Location;

public class TestingTypes {
  public static final ConcreteType type = type();
  public static final ConcreteType bool = bool();
  public static final ConcreteType string = string();
  public static final ConcreteType blob = blob();
  public static final ConcreteType nothing = nothing();
  public static final Location FAKE_LOCATION =
      location(modulePath(USER, Path.of("fake/path"), "shortPath"), 1);
  public static final StructType person = struct(
      "Person", FAKE_LOCATION, List.of(
          new Field(string, "firstName", internal()),
          new Field(string, "lastName", internal())));
  public static final GenericType a = generic("A");
  public static final GenericType b = generic("B");

  public static final ConcreteArrayType arrayType = array(type);
  public static final ConcreteArrayType arrayBool = array(bool);
  public static final ConcreteArrayType arrayString = array(string);
  public static final ConcreteArrayType arrayBlob = array(blob);
  public static final ConcreteArrayType arrayNothing = array(nothing);
  public static final ConcreteArrayType arrayPerson = array(person);
  public static final GenericArrayType arrayA = array(a);
  public static final GenericArrayType arrayB = array(b);

  public static final ConcreteArrayType array2Type = array(arrayType);
  public static final ConcreteArrayType array2Bool = array(arrayBool);
  public static final ConcreteArrayType array2String = array(arrayString);
  public static final ConcreteArrayType array2Blob = array(arrayBlob);
  public static final ConcreteArrayType array2Nothing = array(arrayNothing);
  public static final ConcreteArrayType array2Person = array(arrayPerson);
  public static final GenericArrayType array2A = array(arrayA);
  public static final GenericArrayType array2B = array(arrayB);
}

