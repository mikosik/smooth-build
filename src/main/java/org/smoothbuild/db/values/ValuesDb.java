package org.smoothbuild.db.values;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.type.TypeNames.BLOB;
import static org.smoothbuild.lang.type.TypeNames.BOOL;
import static org.smoothbuild.lang.type.TypeNames.NOTHING;
import static org.smoothbuild.lang.type.TypeNames.STRING;
import static org.smoothbuild.lang.type.TypeNames.TYPE;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.type.BlobType;
import org.smoothbuild.lang.type.BoolType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.NothingType;
import org.smoothbuild.lang.type.StringType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TypeType;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.StructBuilder;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

import okio.BufferedSource;

public class ValuesDb {
  private final HashedDb hashedDb;

  private final Map<HashCode, ConcreteType> typesCache;
  private TypeType type;
  private BoolType bool;
  private StringType string;
  private BlobType blob;
  private NothingType nothing;

  @Inject
  public ValuesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.typesCache = new HashMap<>();
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return createArrayBuilder(arrayType(elementType));
  }

  private ArrayBuilder createArrayBuilder(ConcreteArrayType type) {
    return new ArrayBuilder(type, hashedDb);
  }

  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, hashedDb);
  }

  public BlobBuilder blobBuilder() {
    try {
      return new BlobBuilder(blobType(), hashedDb);
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  public SString string(String string) {
    try {
      return new SString(hashedDb.writeString(string), stringType(), hashedDb);
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  public Bool bool(boolean value) {
    return new Bool(writeBool(value), boolType(), hashedDb);
  }

  private HashCode writeBool(boolean value) {
    try (HashingBufferedSink sink = hashedDb.sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  public Value get(HashCode hash) {
    List<HashCode> hashes = readHashes(hash);
    switch (hashes.size()) {
      case 1:
        // If Merkle tree root has only one child then it must
        // be Type("Type") smooth value. getType() will verify it.
        return getType(hash);
      case 2:
        ConcreteType type = getType(hashes.get(0));
        if (type.equals(typeType())) {
          return getType(hash);
        } else {
          return type.newValue(hashes.get(1));
        }
      default:
        throw corruptedValueException(
            hash, "Its Merkle tree root has " + hashes.size() + " children.");
    }
  }

  private List<HashCode> readHashes(HashCode hash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (EOFException e) {
      throw corruptedValueException(hash,
          "Its Merkle tree root is hash of byte sequence which size is not multiple of hash size.");
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  public TypeType typeType() {
    if (type == null) {
      type = new TypeType(writeBasicTypeData(TYPE), this, hashedDb);
      typesCache.put(type.hash(), type);
    }
    return type;
  }

  public BoolType boolType() {
    if (bool == null) {
      bool = new BoolType(writeBasicTypeData(BOOL), typeType(), hashedDb, this);
      typesCache.put(bool.hash(), bool);
    }
    return bool;
  }

  public StringType stringType() {
    if (string == null) {
      string = new StringType(writeBasicTypeData(STRING), typeType(), hashedDb, this);
      typesCache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blobType() {
    if (blob == null) {
      blob = new BlobType(writeBasicTypeData(BLOB), typeType(), hashedDb, this);
      typesCache.put(blob.hash(), blob);
    }
    return blob;
  }

  public NothingType nothingType() {
    if (nothing == null) {
      nothing = new NothingType(writeBasicTypeData(NOTHING), typeType(), hashedDb, this);
      typesCache.put(nothing.hash(), nothing);
    }
    return nothing;
  }

  private HashCode writeBasicTypeData(String name) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(name));
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    HashCode dataHash = writeArray(elementType);
    ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
    return cacheType(
        new ConcreteArrayType(dataHash, typeType(), superType, elementType, hashedDb, this));
  }

  private HashCode writeArray(ConcreteType elementType) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  private ConcreteArrayType possiblyNullArrayType(ConcreteType elementType) {
    return elementType == null ? null : arrayType(elementType);
  }

  public StructType structType(String name, Iterable<Field> fields) {
    HashCode hash = writeStruct(name, fields);
    return cacheType(new StructType(hash, typeType(), name, fields, hashedDb, this));
  }

  private HashCode writeStruct(String name, Iterable<Field> fields) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  private HashCode writeFields(Iterable<Field> fields) throws IOException {
    List<HashCode> fieldHashes = new ArrayList<>();
    for (Field field : fields) {
      fieldHashes.add(writeField(field.name(), field.type()));
    }
    return hashedDb.writeHashes(fieldHashes.toArray(new HashCode[0]));
  }

  private HashCode writeField(String name, ConcreteType type) throws IOException {
    return hashedDb.writeHashes(hashedDb.writeString(name), type.hash());
  }

  private ConcreteType getType(HashCode hash) {
    if (typesCache.containsKey(hash)) {
      return typesCache.get(hash);
    } else {
      try {
        return getTypeImpl(hash);
      } catch (IOException e) {
        throw valuesDbException(e);
      }
    }
  }

  private ConcreteType getTypeImpl(HashCode hash) throws IOException {
    List<HashCode> hashes = hashedDb.readHashes(hash);
    switch (hashes.size()) {
      case 1:
        if (!typeType().hash().equals(hash)) {
          throw corruptedValueException(hash, "Expected value which is instance of 'Type' "
              + "but its Merkle tree has only one child (so it should be Type type) but "
              + "it has different hash.");
        }
        return typeType();
      case 2:
        HashCode typeHash = hashes.get(0);
        if (!typeType().hash().equals(typeHash)) {
          throw corruptedValueException(hash, "Expected value which is instance of 'Type' but "
              + "its Merkle tree's first child is not Type type.");
        }
        HashCode dataHash = hashes.get(1);
        return readFromDataHash(dataHash, hash);
      default:
        throw corruptedValueException(
            hash, "Its Merkle tree root has " + hashes.size() + " children.");
    }
  }

  public ConcreteType readFromDataHash(HashCode typeDataHash, HashCode typeHash)
      throws IOException {
    try (BufferedSource source = hashedDb.source(typeDataHash)) {
      HashCode nameHash = Hash.read(source);
      String name = decodeName(typeHash, nameHash);
      switch (name) {
        case BOOL:
          assertNoMoreData(typeHash, source, name);
          return boolType();
        case STRING:
          assertNoMoreData(typeHash, source, name);
          return stringType();
        case BLOB:
          assertNoMoreData(typeHash, source, name);
          return blobType();
        case NOTHING:
          assertNoMoreData(typeHash, source, name);
          return nothingType();
        case "":
          ConcreteType elementType = getType(Hash.read(source));
          ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
          return cacheType(new ConcreteArrayType(typeDataHash, typeType(), superType, elementType,
              hashedDb, this));
        default:
      }
      Iterable<Field> fields = readFields(Hash.read(source), typeHash);
      assertNoMoreData(typeHash, source, "struct");
      return cacheType(new StructType(typeDataHash, typeType(), name, fields, hashedDb, this));
    }
  }

  private String decodeName(HashCode typeHash, HashCode nameHash) throws IOException {
    try {
      return hashedDb.readString(nameHash);
    } catch (DecodingStringException e) {
      throw corruptedValueException(typeHash, "It is an instance of a Type which name cannot be " +
          "decoded using " + CHARSET + " encoding.");
    }
  }

  private static void assertNoMoreData(HashCode typeHash, BufferedSource source, String typeName)
      throws IOException {
    if (!source.exhausted()) {
      throw corruptedValueException(typeHash,
          "It is " + typeName + " type but its Merkle tree has unnecessary children.");
    }
  }

  private Iterable<Field> readFields(HashCode hash, HashCode typeHash) throws IOException {
    List<Field> result = new ArrayList<>();
    for (HashCode fieldHash : hashedDb.readHashes(hash)) {
      List<HashCode> hashes = hashedDb.readHashes(fieldHash);
      if (hashes.size() != 2) {
        throw corruptedValueException(typeHash,
            "It is struct type but one of its field hashes doesn't have two children but "
                + hashes.size() + ".");
      }
      String name = decodeFieldName(typeHash, hashes.get(0));
      ConcreteType type = getType(hashes.get(1));
      result.add(new Field(type, name, unknownLocation()));
    }
    return result;
  }

  private String decodeFieldName(HashCode typeHash, HashCode nameHash) throws IOException {
    try {
      return hashedDb.readString(nameHash);
    } catch (DecodingStringException e) {
      throw corruptedValueException(typeHash, "It is an instance of a struct Type which field " +
          "name cannot be decoded using " + CHARSET + " encoding.");
    }
  }

  private <T extends ConcreteType> T cacheType(T type) {
    HashCode hash = type.hash();
    if (typesCache.containsKey(hash)) {
      return (T) typesCache.get(hash);
    } else {
      typesCache.put(hash, type);
      return type;
    }
  }
}
