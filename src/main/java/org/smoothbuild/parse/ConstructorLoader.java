package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.lang.function.Constructor;
import org.smoothbuild.lang.function.Parameter;
import org.smoothbuild.lang.function.Signature;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;

public class ConstructorLoader {
  public static Constructor loadConstructor(StructNode struct) {
    ImmutableList<Parameter> parameters = struct
        .fields()
        .stream()
        .map(f -> new Parameter(f.get(Type.class), f.name(), null))
        .collect(toImmutableList());
    Signature signature = new Signature(struct.get(Type.class), struct.name(), parameters);
    return new Constructor(signature, struct.location());
  }
}
