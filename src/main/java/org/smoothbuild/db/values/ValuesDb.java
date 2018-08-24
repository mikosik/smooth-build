package org.smoothbuild.db.values;

import static org.smoothbuild.db.values.ValuesDbException.readException;
import static org.smoothbuild.db.values.ValuesDbException.writeException;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Instantiator;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.StructBuilder;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class ValuesDb {
  private final HashedDb hashedDb;
  private final TypesDb typesDb;
  private final Instantiator instantiator;

  @Inject
  public ValuesDb(@Values HashedDb hashedDb, TypesDb typesDb) {
    this.hashedDb = hashedDb;
    this.typesDb = typesDb;
    this.instantiator = new Instantiator(hashedDb, typesDb);
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return createArrayBuilder(typesDb.array(elementType));
  }

  private ArrayBuilder createArrayBuilder(ConcreteArrayType type) {
    return new ArrayBuilder(type, hashedDb);
  }

  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, hashedDb);
  }

  public BlobBuilder blobBuilder() {
    try {
      return new BlobBuilder(typesDb.blob(), hashedDb);
    } catch (IOException e) {
      throw readException(e);
    }
  }

  public SString string(String string) {
    try {
      return new SString(hashedDb.writeString(string), typesDb.string(), hashedDb);
    } catch (IOException e) {
      throw writeException(e);
    }
  }

  public Value get(HashCode hash) {
    return instantiator.instantiate(hash);
  }
}
