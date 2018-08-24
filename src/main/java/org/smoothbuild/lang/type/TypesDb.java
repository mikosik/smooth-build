package org.smoothbuild.lang.type;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.values.ValuesDbException.corruptedHashSequenceException;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.type.TypeNames.BLOB;
import static org.smoothbuild.lang.type.TypeNames.NOTHING;
import static org.smoothbuild.lang.type.TypeNames.STRING;
import static org.smoothbuild.lang.type.TypeNames.TYPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.NotEnoughBytesException;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.Values;
import org.smoothbuild.db.values.ValuesDbException;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.base.Field;

import com.google.common.hash.HashCode;

public class TypesDb {
  private final HashedDb hashedDb;
  private final Map<HashCode, ConcreteType> cache;
  private final Instantiator instantiator;
  private TypeType type;
  private StringType string;
  private BlobType blob;
  private NothingType nothing;

  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new HashMap<>();
    this.instantiator = new Instantiator(hashedDb, this);
  }

  public TypeType type() {
    if (type == null) {
      type = new TypeType(writeBasicTypeData(TYPE), this, hashedDb);
      cache.put(type.hash(), type);
    }
    return type;
  }

  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicTypeData(STRING), type(), hashedDb, this);
      cache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicTypeData(BLOB), type(), hashedDb, this);
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  public NothingType nothing() {
    if (nothing == null) {
      nothing = new NothingType(writeBasicTypeData(NOTHING), type(), hashedDb, this);
      cache.put(nothing.hash(), nothing);
    }
    return nothing;
  }

  private HashCode writeBasicTypeData(String name) {
    return hashedDb.writeHashes(hashedDb.writeString(name));
  }

  public ConcreteArrayType array(ConcreteType elementType) {
    HashCode dataHash = hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
    return cache(new ConcreteArrayType(dataHash, type(), superType, elementType, instantiator,
        hashedDb, this));
  }

  private ConcreteArrayType possiblyNullArrayType(ConcreteType elementType) {
    return elementType == null ? null : array(elementType);
  }

  public StructType struct(String name, Iterable<Field> fields) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    return cache(new StructType(hash, type(), name, fields, instantiator, hashedDb, this));
  }

  private HashCode writeFields(Iterable<Field> fields) {
    return hashedDb.writeHashes(
        stream(fields)
            .map(f -> writeField(f.name(), f.type()))
            .toArray(HashCode[]::new));
  }

  private HashCode writeField(String name, ConcreteType type) {
    return hashedDb.writeHashes(hashedDb.writeString(name), type.hash());
  }

  public ConcreteType read(HashCode hash) {
    if (cache.containsKey(hash)) {
      return cache.get(hash);
    } else {
      List<HashCode> hashes = readHashes(hash, hash);
      switch (hashes.size()) {
        case 1:
          if (!type().hash().equals(hash)) {
            throw corruptedValueException(hash, "Expected value which is instance of 'Type' "
                + "but its Merkle tree has only one child (so it should be Type type) but "
                + "it has different hash.");
          }
          return type();
        case 2:
          HashCode typeHash = hashes.get(0);
          if (!type().hash().equals(typeHash)) {
            throw corruptedValueException(hash, "Expected value which is instance of 'Type' but "
                + "its Merkle tree's first child is not Type type.");
          }
          HashCode dataHash = hashes.get(1);
          return readFromDataHash(dataHash, typeHash);
        default:
          throw newCorruptedMerkleRootException(hash, hashes.size());
      }
    }
  }

  protected ConcreteType readFromDataHash(HashCode typeDataHash, HashCode typeHash) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(typeDataHash)) {
      String name = hashedDb.readString(readHash(unmarshaller, typeHash));
      switch (name) {
        case STRING:
          return string();
        case BLOB:
          return blob();
        case NOTHING:
          return nothing();
        case "":
          ConcreteType elementType = read(readHash(unmarshaller, typeHash));
          ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
          return cache(new ConcreteArrayType(typeDataHash, type(), superType, elementType,
              instantiator, hashedDb, this));
        default:
      }
      Iterable<Field> fields = readFields(readHash(unmarshaller, typeHash), typeHash);
      return cache(new StructType(typeDataHash, type(), name, fields, instantiator, hashedDb,
          this));
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private Iterable<Field> readFields(HashCode hash, HashCode typeHash) {
    List<Field> result = new ArrayList<>();
    for (HashCode fieldHash : readHashes(hash, typeHash)) {
      List<HashCode> hashes = readHashes(fieldHash, typeHash);
      if (hashes.size() != 2) {
        throw newCorruptedMerkleRootException(hash, hashes.size());
      }
      String name = hashedDb.readString(hashes.get(0));
      ConcreteType type = read(hashes.get(1));
      result.add(new Field(type, name, unknownLocation()));
    }
    return result;
  }

  private <T extends ConcreteType> T cache(T type) {
    HashCode hash = type.hash();
    if (cache.containsKey(hash)) {
      return (T) cache.get(hash);
    } else {
      cache.put(hash, type);
      return type;
    }
  }

  private static ValuesDbException newCorruptedMerkleRootException(HashCode hash, int childCount) {
    return corruptedValueException(
        hash, "Its Merkle tree root has " + childCount + " children.");
  }

  private List<HashCode> readHashes(HashCode hash, HashCode typeHash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (NotEnoughBytesException e) {
      throw corruptedHashSequenceException(hash);
    }
  }

  private static HashCode readHash(Unmarshaller unmarshaller, HashCode typeHash) {
    try {
      return unmarshaller.readHash();
    } catch (NotEnoughBytesException e) {
      throw corruptedHashSequenceException(typeHash);
    }
  }
}
