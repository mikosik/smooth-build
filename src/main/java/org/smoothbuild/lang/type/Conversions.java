package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;

import java.util.Objects;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;

public class Conversions {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  public static boolean canConvert(Type from, Type to) {
    return from == to || CONVERSIONS.containsKey(new TypeConversion(from, to));
  }

  public static Name convertFunctionName(Type from, Type to) {
    return CONVERSIONS.get(new TypeConversion(from, to));
  }

  private static ImmutableMap<TypeConversion, Name> createConversions() {
    ImmutableMap.Builder<TypeConversion, Name> builder = ImmutableMap.builder();

    builder.put(new TypeConversion(FILE, BLOB), new Name("fileToBlob"));
    builder.put(new TypeConversion(FILE_ARRAY, BLOB_ARRAY), new Name("fileArrayToBlobArray"));
    builder.put(new TypeConversion(NIL, STRING_ARRAY), new Name("nilToStringArray"));
    builder.put(new TypeConversion(NIL, BLOB_ARRAY), new Name("nilToBlobArray"));
    builder.put(new TypeConversion(NIL, FILE_ARRAY), new Name("nilToFileArray"));

    return builder.build();
  }

  private static class TypeConversion {
    private final Type from;
    private final Type to;

    private TypeConversion(Type from, Type to) {
      this.from = from;
      this.to = to;
    }

    public boolean equals(Object object) {
      return object instanceof TypeConversion && equals((TypeConversion) object);
    }

    private boolean equals(TypeConversion typeConversion) {
      return Objects.equals(from, typeConversion.from)
          && Objects.equals(to, typeConversion.to);
    }

    public int hashCode() {
      return Objects.hash(from, to);
    }
  }
}
