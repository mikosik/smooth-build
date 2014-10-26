package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.Types.STRING;

import org.smoothbuild.db.objects.marshal.StringMarshaller;
import org.smoothbuild.lang.base.SString;

import com.google.common.hash.HashCode;

public class StringObject extends AbstractObject implements SString {
  private final StringMarshaller marshaller;

  public StringObject(HashCode hash, StringMarshaller marshaller) {
    super(STRING, hash);
    this.marshaller = checkNotNull(marshaller);
  }

  @Override
  public String value() {
    return marshaller.readValue(hash());
  }

  @Override
  public String toString() {
    return value();
  }
}
