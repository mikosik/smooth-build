package org.smoothbuild.parse;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.TypeNode;

public class AssignTypes {
  public static Ast assignTypes(Ast ast) {
    ast.functions().stream().forEach(f -> assignTypes(f));
    return ast;
  }

  private static void assignTypes(FuncNode func) {
    func.params().stream().forEach(AssignTypes::assignType);
  }

  private static void assignType(ParamNode param) {
    TypeNode type = param.type();
    type.set(Type.class, createType(type));
  }

  private static Type createType(TypeNode typeNode) {
    if (typeNode instanceof ArrayTypeNode) {
      TypeNode elementTypeNode = ((ArrayTypeNode) typeNode).elementType();
      return Types.arrayOf(createType(elementTypeNode));
    }
    return Types.basicTypeFromString(typeNode.name());
  }
}
