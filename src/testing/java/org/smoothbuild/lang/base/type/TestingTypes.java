package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;

public class TestingTypes {
  public static final BoolType BOOL = bool();
  public static final StringType STRING = string();
  public static final BlobType BLOB = blob();
  public static final NothingType NOTHING = nothing();
  public static final Location FAKE_LOCATION =
      location(moduleLocation(USER, Path.of("fake/path")), 1);
  public static final StructType PERSON = struct(
      "Person", FAKE_LOCATION, List.of(
          new Field(0, STRING, "firstName", internal()),
          new Field(1, STRING, "lastName", internal())));
  public static final Type A = generic("A");
  public static final Type B = generic("B");

  public static final ArrayType ARRAY_BOOL = array(BOOL);
  public static final ArrayType ARRAY_STRING = array(STRING);
  public static final ArrayType ARRAY_BLOB = array(BLOB);
  public static final ArrayType ARRAY_NOTHING = array(NOTHING);
  public static final ArrayType ARRAY_PERSON = array(PERSON);
  public static final ArrayType ARRAY_A = array(A);
  public static final ArrayType ARRAY_B = array(B);

  public static final ArrayType ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayType ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArrayType ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayType ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayType ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayType ARRAY2_A = array(ARRAY_A);
  public static final ArrayType ARRAY2_B = array(ARRAY_B);
}
