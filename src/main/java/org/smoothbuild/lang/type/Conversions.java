package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;

import java.util.Objects;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;

public class Conversions {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  public static boolean canConvert(Type from, Type to) {
    return from.equals(to) || CONVERSIONS.containsKey(new TypeConversion(from, to));
  }

  public static Name convertFunctionName(Type from, Type to) {
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
