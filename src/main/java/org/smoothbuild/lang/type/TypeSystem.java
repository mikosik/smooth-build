package org.smoothbuild.lang.type;

import static org.smoothbuild.util.Lists.list;

import java.util.Objects;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;

public class TypeSystem implements org.smoothbuild.lang.plugin.Types {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  private final TypesDb typesDb;

  @Inject
  public TypeSystem(TypesDb typesDb) {
    this.typesDb = typesDb;
  }

  public TypeSystem() {
    this(new TypesDb(new HashedDb()));
  }

  public Type type() {
    return typesDb.type();
  }

  @Override
  public Type string() {
    return typesDb.string();
  }

  @Override
  public Type blob() {
    return typesDb.blob();
  }

  @Override
  public Type nothing() {
    return typesDb.nothing();
  }

  @Override
  public ArrayType array(Type elementType) {
    return typesDb.array(elementType);
  }

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    return typesDb.struct(name, fields);
  }

  @Override
  public StructType file() {
    ImmutableMap<String, Type> fields = ImmutableMap.of(
        "content", typesDb.blob(),
        "path", typesDb.string());
    return typesDb.struct("File", fields);
  }

  public Type nonArrayTypeFromString(String string) {
    for (Type type : list(string(), blob(), nothing(), file())) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public boolean canConvert(Type from, Type to) {
    return from.equals(to) || CONVERSIONS.containsKey(new TypeConversion(from.name(), to.name()));
  }

  public Name convertFunctionName(Type from, Type to) {
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
