package com.parsley.connect

import com.parsley.connect.connection.{DataBaseConnection, MysqlConnection, SqliteConnection}

import java.sql.{Connection, DriverManager, SQLException, Statement}

protected class DataBaseManager(databaseConnection: DataBaseConnection) {

  private val connection: Connection = {
    val conn = connect()
    conn.setAutoCommit(false)
    conn
  }

  private def connect(): Connection = databaseConnection match {
    case mysqlConnection: MysqlConnection =>
      Class.forName("com.mysql.cj.jdbc.Driver")
      val connectURL = s"jdbc:mysql://${mysqlConnection.address}:${mysqlConnection.port}/${mysqlConnection.database}"
      DriverManager.getConnection(connectURL, mysqlConnection.user, mysqlConnection.password)

    case sqliteConnection: SqliteConnection =>
      Class.forName("org.sqlite.JDBC")
      DriverManager.getConnection(s"jdbc:sqlite:${sqliteConnection.url}")
  }

}

object DataBaseManager {

  def register(databaseConnection: DataBaseConnection): Unit = {
    this.dataBaseManager = new DataBaseManager(databaseConnection)
  }

  // singleton instance
  // not thread safe
  private var dataBaseManager: DataBaseManager = _

  private[parsley] def preparedStatement(sql: String) = this.dataBaseManager.connection.prepareStatement(sql)


  private[parsley] def commit(): Unit = {
    try {
      this.dataBaseManager.connection.commit()
    } catch {
      case e: SQLException =>
        this.dataBaseManager.connection.rollback()
        e.printStackTrace()
    }
  }

}
