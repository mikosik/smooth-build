package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;

public class TypeConversions {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  public static boolean canConvert(Type from, Type to) {
    return from.equals(to) || CONVERSIONS.containsKey(new TypeConversion(from.name(), to.name()));
  }

  public static Name convertFunctionName(Type from, Type to) {
    return CONVERSIONS.get(new TypeConversion(from.name(), to.name()));
  }

  private static ImmutableMap<TypeConversion, Name> createConversions() {
    ImmutableMap.Builder<TypeConversion, Name> builder = ImmutableMap.builder();
    builder.put(new TypeConversion("File", "Blob"), new Name("fileToBlob"));
    builder.put(new TypeConversion("[File]", "[Blob]"), new Name("fileArrayToBlobArray"));
    builder.put(new TypeConversion("[Nothing]", "[String]"), new Name("nilToStringArray"));
    builder.put(new TypeConversion("[Nothing]", "[Blob]"), new Name("nilToBlobArray"));
    builder.put(new TypeConversion("[Nothing]", "[File]"), new Name("nilToFileArray"));
    return builder.build();
  }

  private static class TypeConversion {
    private final String from;
    private final String to;

    private TypeConversion(String from, String to) {
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
