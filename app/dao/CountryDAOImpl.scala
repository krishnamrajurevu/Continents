package dao

import com.google.inject.Inject
import model.Country
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CountryDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  extends CountryDAO {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class CountryTable(tag: Tag) extends Table[Country](tag, "country") {

    def countryId = column[Int]("countryId", O.PrimaryKey)

    def countryName = column[String]("countryName")

    def continentId = column[Int]("continentId")

    def * = (countryId,countryName,continentId) <> ((Country.apply _).tupled, Country.unapply)
  }

  private val countries = TableQuery[CountryTable]

  override def getAllCountries(): Future[Seq[Country]] = {
    db.run(countries.result)
  }

  override def addCountry(country: Country): Future[String] = {
    db.run(countries += country).map(res => "Country successfully added").recover {
      case ex : Exception => ex.getCause.getMessage
    }
  }

  override def deleteCountry(countryId: Int): Future[Int] = {
    db.run(countries.filter(_.countryId === countryId).delete)
  }

  override def deleteCountriesByContinent(continentId: Int):  Future[Int] ={
    db.run(countries.filter(_.continentId === continentId).delete)
  }
}
