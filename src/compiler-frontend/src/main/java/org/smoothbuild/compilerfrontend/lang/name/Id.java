package org.smoothbuild.compilerfrontend.lang.name;

import static com.google.common.base.Suppliers.memoize;

import com.google.common.base.Supplier;
import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.List;

/**
 * Identifier.
 */
public abstract class Id {
  private final String string;
  private final Supplier<List<Name>> parts = memoize(this::splitToParts);

  protected Id(String string) {
    this.string = string;
  }

  public List<Name> parts() {
    return parts.get();
  }

  protected abstract List<Name> splitToParts();

  public Id append(Id id) {
    return new Fqn(string + CharUtils.SEPARATOR + id.string);
  }

  public String q() {
    return Strings.q(string);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Id that && Objects.equals(this.string, that.string);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(string);
  }

  @Override
  public String toString() {
    return string;
  }
}
