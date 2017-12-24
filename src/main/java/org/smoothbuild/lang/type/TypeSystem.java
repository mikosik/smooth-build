package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;

import java.util.Objects;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class TypeSystem implements org.smoothbuild.lang.plugin.Types {
  private static final Type STRING = new StringType();
  private static final Type BLOB = new BlobType();
  private static final StructType FILE = createFileType();
  private static final Type NOTHING = new NothingType();
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  private static StructType createFileType() {
    ImmutableMap<String, Type> fields = ImmutableMap.of(
        "content", BLOB,
        "path", STRING);
    return new StructType("File", fields);
  }

  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);

  @Override
  public Type string() {
    return STRING;
  }

  @Override
  public Type blob() {
    return BLOB;
  }

  @Override
  public StructType file() {
    return FILE;
  }

  @Override
  public Type nothing() {
    return NOTHING;
  }

  public Type basicTypeFromString(String string) {
    for (Type type : BASIC_TYPES) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public boolean canConvert(Type from, Type to) {
    return from.equals(to) || CONVERSIONS.containsKey(new TypeConversion(from, to));
  }

  public Name convertFunctionName(Type from, Type to) {
    return CONVERSIONS.get(new TypeConversion(from, to));
  }

  private static ImmutableMap<TypeConversion, Name> createConversions() {
    ImmutableMap.Builder<TypeConversion, Name> builder = ImmutableMap.builder();

    builder.put(new TypeConversion(FILE, BLOB), new Name("fileToBlob"));
    builder.put(new TypeConversion(arrayOf(FILE), arrayOf(BLOB)), new Name("fileArrayToBlobArray"));
    builder.put(new TypeConversion(arrayOf(NOTHING), arrayOf(STRING)), new Name(
        "nilToStringArray"));
    builder.put(new TypeConversion(arrayOf(NOTHING), arrayOf(BLOB)), new Name("nilToBlobArray"));
    builder.put(new TypeConversion(arrayOf(NOTHING), arrayOf(FILE)), new Name("nilToFileArray"));

    return builder.build();
  }

  private static class TypeConversion {
    private final Type from;
    private final Type to;

    private TypeConversion(Type from, Type to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof TypeConversion && equals((TypeConversion) object);
    }

    private boolean equals(TypeConversion typeConversion) {
      return Objects.equals(from, typeConversion.from)
          && Objects.equals(to, typeConversion.to);
    }

    @Override
    public int hashCode() {
      return Objects.hash(from, to);
    }
  }
}
