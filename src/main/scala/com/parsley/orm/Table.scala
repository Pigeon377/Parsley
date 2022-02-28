package com.parsley.orm

import com.parsley.connect.DataBaseManager
import com.parsley.connect.execute.ExecuteSQL

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

private class Table[T <: Product](implicit clazzTag: ClassTag[T]) {

    private[parsley] val clazz = clazzTag.runtimeClass
    private[parsley] val name = clazz.getSimpleName
    // column name map to its type
    private[parsley] val columnType: Map[String, String] =
        clazz.getDeclaredConstructors.head.getParameters
            .map(x => (x.getName, x.getType.getSimpleName)).toMap

    private[parsley] val columnAttribute: mutable.HashMap[String, Seq[Attribute]] = mutable.HashMap.empty
//
//    def query(): T = {
//
//    }

}

protected object Table {

    def apply[T <: Product](implicit clazzTag: ClassTag[T]): Table[T] = new Table

    def putAttribute(table: Table[_])(seq: Seq[(String, Seq[Attribute])]): Unit = {
        seq.foreach((name, attributes) => {
            val opt = table.columnType.get(name)
            if (opt.isEmpty) {
                throw new Exception(s"$name is not a column in table: ${table.name}")
            } else {
                table.columnAttribute.put(name, attributes)
            }
        })
    }

}