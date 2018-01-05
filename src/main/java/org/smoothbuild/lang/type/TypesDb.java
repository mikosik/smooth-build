package org.smoothbuild.lang.type;

import static org.smoothbuild.util.Lists.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.db.values.Values;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.plugin.Types;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TypesDb implements Types {
  private static final ImmutableMap<TypeConversion, Name> CONVERSIONS = createConversions();

  private final HashedDb hashedDb;
  private final Map<HashCode, Type> cache;
  private final Instantiator instantiator;
  private TypeType type;
  private StringType string;
  private BlobType blob;
  private NothingType nothing;

  @Inject
  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new HashMap<>();
    this.instantiator = new Instantiator(hashedDb, this);
  }

  public TypesDb() {
    this(new HashedDb());
  }

  public Type nonArrayTypeFromString(String string) {
    for (Type type : list(type(), string(), blob(), nothing(), file())) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public TypeType type() {
    if (type == null) {
      type = new TypeType(writeBasicTypeData("Type"), this, hashedDb);
      cache.put(type.hash(), type);
    }
    return type;
  }

  @Override
  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicTypeData("String"), type(), hashedDb);
      cache.put(string.hash(), string);
    }
    return string;
  }

  @Override
  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicTypeData("Blob"), type(), hashedDb);
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  @Override
  public NothingType nothing() {
    if (nothing == null) {
      nothing = new NothingType(writeBasicTypeData("Nothing"), type(), hashedDb);
      cache.put(nothing.hash(), nothing);
    }
    return nothing;
  }

  private HashCode writeBasicTypeData(String name) {
    return hashedDb.writeHashes(hashedDb.writeString(name));
  }

  @Override
  public ArrayType array(Type elementType) {
    HashCode dataHash = hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    ArrayType superType = possiblyNullArrayType(elementType.superType());
    return cache(new ArrayType(dataHash, type(), superType, elementType, instantiator, hashedDb));
  }

  private ArrayType possiblyNullArrayType(Type elementType) {
    return elementType == null ? null : array(elementType);
  }

  @Override
  public StructType file() {
    return struct("File", ImmutableMap.of("content", blob(), "path", string()));
  }

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    return cache(new StructType(hash, type(), name, fields, instantiator, hashedDb));
  }

  private HashCode writeFields(ImmutableMap<String, Type> fields) {
    return hashedDb.writeHashes(
        fields
            .entrySet()
            .stream()
            .map(f -> writeField(f.getKey(), f.getValue()))
            .toArray(HashCode[]::new));
  }

  private HashCode writeField(String name, Type type) {
    return hashedDb.writeHashes(hashedDb.writeString(name), type.hash());
  }

  public Type read(HashCode hash) {
    if (cache.containsKey(hash)) {
      return cache.get(hash);
    } else {
      List<HashCode> hashes = hashedDb.readHashes(hash);
      switch (hashes.size()) {
        case 1:
          if (!type().hash().equals(hash)) {
            throw new CorruptedValueException(
                "Expected " + type() + " value but got value which hash is " + hash);
          }
          return type();
        case 2:
          HashCode typeHash = hashes.get(0);
          if (!type().hash().equals(typeHash)) {
            throw new CorruptedValueException(
                "Expected " + type() + " value but got value which hash is " + typeHash);
          }
          HashCode dataHash = hashes.get(1);
          return readFromDataHash(dataHash);
        default:
          throw newCorruptedMerkleRootException(hash, hashes.size());
      }
    }
  }

  protected Type readFromDataHash(HashCode typeDataHash) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(typeDataHash)) {
      String name = hashedDb.readString(unmarshaller.readHash());
      switch (name) {
        case "String":
          return string();
        case "Blob":
          return blob();
        case "Nothing":
          return nothing();
        case "":
          Type elementType = read(unmarshaller.readHash());
          ArrayType superType = possiblyNullArrayType(elementType.superType());
          return cache(new ArrayType(typeDataHash, type(), superType, elementType, instantiator,
              hashedDb));
        default:
          ImmutableMap<String, Type> fields = readFields(unmarshaller.readHash());
          return cache(new StructType(typeDataHash, type(), name, fields, instantiator, hashedDb));
      }
    }
  }

  private ImmutableMap<String, Type> readFields(HashCode hash) {
    ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
    for (HashCode fieldHash : hashedDb.readHashes(hash)) {
      List<HashCode> hashes = hashedDb.readHashes(fieldHash);
      if (hashes.size() != 2) {
        throw newCorruptedMerkleRootException(hash, hashes.size());
      }
      String fieldName = hashedDb.readString(hashes.get(0));
      Type fieldType = read(hashes.get(1));
      builder.put(fieldName, fieldType);
    }
    return builder.build();
  }

  private <T extends Type> T cache(T type) {
    HashCode hash = type.hash();
    if (cache.containsKey(hash)) {
      return (T) cache.get(hash);
    } else {
      cache.put(hash, type);
      return type;
    }
  }

  private CorruptedValueException newCorruptedMerkleRootException(HashCode hash, int childCount) {
    return new CorruptedValueException(
        hash, "Its Merkle tree root has " + childCount + " children.");
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
