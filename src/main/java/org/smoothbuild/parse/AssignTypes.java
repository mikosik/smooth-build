package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FunctionNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.TypeNode;

public class AssignTypes {
  public static Ast assignTypes(Ast ast) {
    List<FunctionNode> withAssignedTypes = ast
        .functions()
        .stream()
        .map((FunctionNode f) -> assignTypes(f))
        .collect(toList());
    return Ast.ast(withAssignedTypes);
  }

  private static FunctionNode assignTypes(FunctionNode functionNode) {
    List<ParamNode> params = functionNode
        .params()
        .stream()
        .map(p -> assignType(p))
        .collect(toList());
    return functionNode.withParams(params);
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
