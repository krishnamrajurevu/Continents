package dao

import com.google.inject.Inject
import model.Continent
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class ContinentDAOImpl  @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  extends ContinentDAO  {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  private class ContinentTable(tag: Tag) extends Table[Continent](tag, "continent") {

    def continentId = column[Int]("continentId", O.PrimaryKey)

    def continentName = column[String]("continentName")

    def * = (continentId,continentName) <> ((Continent.apply _).tupled, Continent.unapply)
  }

  private val continents = TableQuery[ContinentTable]


  override def getAllContinents: Future[Seq[Continent]] =  {
    db.run(continents.result)
  }

  override def getContinent(continentId: Int): Future[Option[Continent]] =  {
    db.run(continents.filter(_.continentId === continentId).result.headOption)
  }

  override def addContinent(continent: Continent): Future[String] = {
    db.run(continents += continent).map(res => "User successfully added").recover {
      case ex : Exception => ex.getCause.getMessage
    }
  }

  override def deleteContinent(continentId: Int): Future[Int] = {
    db.run(continents.filter(_.continentId === continentId).delete)
  }
}
