package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.Tanal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class ItemS extends Tanal implements RefableS {
  private final Optional<NamedEvaluableS> defaultValue;

  public ItemS(TypeS type, String name, Optional<NamedEvaluableS> defaultValue, Location location) {
    super(type, name, location);
    this.defaultValue = defaultValue;
  }

  public Optional<NamedEvaluableS> defaultValue() {
    return defaultValue;
  }

  public static ImmutableList<TypeS> toTypes(List<? extends ItemS> items) {
    return map(items, ItemS::type);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "type = " + type().name(),
        "name = " + name(),
        "defaultValue = " + defaultValue,
        "location = " + location()
    );
    return "ItemS(\n" + indent(fields) + "\n)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof ItemS that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValue, that.defaultValue)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), defaultValue, location());
  }
}
