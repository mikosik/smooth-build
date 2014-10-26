package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;

public class Conversions {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  public static boolean canConvert(Type<?> from, Type<?> to) {
    return from == to || CONVERSIONS.containsKey(new TypeConversion(from, to));
  }

  public static Name convertFunctionName(Type<?> from, Type<?> to) {
    return CONVERSIONS.get(new TypeConversion(from, to));
  }

  private static ImmutableMap<TypeConversion, Name> createConversions() {
    ImmutableMap.Builder<TypeConversion, Name> builder = ImmutableMap.builder();

    builder.put(new TypeConversion(FILE, BLOB), name("fileToBlob"));
    builder.put(new TypeConversion(FILE_ARRAY, BLOB_ARRAY), name("fileArrayToBlobArray"));
    builder.put(new TypeConversion(NIL, STRING_ARRAY), name("nilToStringArray"));
    builder.put(new TypeConversion(NIL, BLOB_ARRAY), name("nilToBlobArray"));
    builder.put(new TypeConversion(NIL, FILE_ARRAY), name("nilToFileArray"));

    return builder.build();
  }

  private static class TypeConversion {
    private final Type<?> from;
    private final Type<?> to;

    private TypeConversion(Type<?> from, Type<?> to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof TypeConversion)) {
        return false;
      }

      TypeConversion that = (TypeConversion) object;
      return this.from == that.from && this.to == that.to;
    }

    @Override
    public int hashCode() {
      return 31 * from.hashCode() + to.hashCode();
    }
  }
}
