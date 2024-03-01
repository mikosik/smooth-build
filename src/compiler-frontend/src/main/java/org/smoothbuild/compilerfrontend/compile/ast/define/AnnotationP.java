package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import com.google.common.base.Objects;
import org.smoothbuild.compilerfrontend.lang.base.NalImpl;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

/**
 * Annotation.
 */
public final class AnnotationP extends NalImpl {
  private final StringP value;

  public AnnotationP(String name, StringP value, Location location) {
    super(name, location);
    this.value = value;
  }

  public StringP value() {
    return value;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AnnotationP that
        && this.name().equals(that.name())
        && this.value().equals(that.value())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name(), this.value, this.location());
  }

  @Override
  public String toString() {
    var fields =
        list("name = " + name(), "value = " + value, "location = " + location()).toString("\n");
    return "AnnotationP(\n" + indent(fields) + "\n)";
  }
}
