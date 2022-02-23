package com.parsley.macroImpl

import scala.quoted.*

inline def primaryConstructorParamList[T]: List[Tuple2[String, String]] = ${primaryConstructorParamListImpl[T]}

private def primaryConstructorParamListImpl[T](using quotes: Quotes, typed: Type[T]): Expr[List[Tuple2[String, String]]] = {
    import quotes.reflect.*
    Expr(TypeTree.of[T].symbol.primaryConstructor.paramSymss.head.map(x => x.tree match {
        case ValDef(name, tp, _) => (name, tp.tpe.show)
        case x => throw new Exception(s"Illegal Constructor => $x")
    }))
}