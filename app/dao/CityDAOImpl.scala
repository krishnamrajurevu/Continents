package dao

import com.google.inject.Inject
import model.City
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CityDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  extends CityDAO {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class CityTable(tag: Tag) extends Table[City](tag, "city") {

    def cityId = column[Int]("cityId", O.PrimaryKey)

    def cityName = column[String]("cityName")

    def countryId = column[Int]("countryId")

    def * = (cityId,cityName,countryId) <> ((City.apply _).tupled, City.unapply)
  }

  private val cities = TableQuery[CityTable]


  override def getAllCities(): Future[Seq[City]] = {
    db.run(cities.result)
  }

  override def addCity(city: City): Future[String] = {
    db.run(cities += city).map(res => "City successfully added").recover {
      case ex : Exception => ex.getCause.getMessage
    }
  }

  override def deleteCity(cityId: Int): Future[Int] = {
    db.run(cities.filter(_.cityId === cityId).delete)
  }
}
