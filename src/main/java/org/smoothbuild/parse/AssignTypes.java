package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.TypeNode;

public class AssignTypes {
  public static Ast assignTypes(Ast ast) {
    List<FuncNode> withAssignedTypes = ast
        .functions()
        .stream()
        .map((FuncNode f) -> assignTypes(f))
        .collect(toList());
    return new Ast(withAssignedTypes);
  }

  private static FuncNode assignTypes(FuncNode funcNode) {
    List<ParamNode> params = funcNode
        .params()
        .stream()
        .map(p -> assignType(p))
        .collect(toList());
    return funcNode.withParams(params);
  }

  private static ParamNode assignType(ParamNode paramNode) {
    return paramNode.withType(createType(paramNode.typeNode()));
  }

  private static Type createType(TypeNode typeNode) {
    if (typeNode instanceof ArrayTypeNode) {
      TypeNode elementTypeNode = ((ArrayTypeNode) typeNode).elementType();
      return Types.arrayOf(createType(elementTypeNode));
    }
    return Types.basicTypeFromString(typeNode.name());
  }
}
