package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.ioException;

import java.io.IOException;

import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

public class SString extends AbstractValue {
  public SString(HashCode dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("String"));
  }

  public String data() {
    try {
      return hashedDb.readString(dataHash());
    } catch (IOException e) {
      throw ioException(e);
    } catch (DecodingStringException e) {
      throw corruptedValueException(hash(), "It is an instance of a String which data cannot be " +
          "decoded using " + CHARSET + " encoding.");
    }
  }
}
