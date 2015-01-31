package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.db.objects.marshal.StringMarshaller;

import com.google.common.hash.HashCode;

/**
 * String value in smooth language.
 */
public class SString extends AbstractValue {
  private final StringMarshaller marshaller;

  public SString(HashCode hash, StringMarshaller marshaller) {
    super(STRING, hash);
    this.marshaller = checkNotNull(marshaller);
  }

  public String value() {
    return marshaller.readValue(hash());
  }

  @Override
  public String toString() {
    return value();
  }
}
