package dao

import com.google.inject.ImplementedBy
import model.Country

import scala.concurrent.Future

@ImplementedBy(classOf[CountryDAOImpl])
trait CountryDAO {
  def getAllCountries(): Future[Seq[Country]]

  def addCountry(country: Country): Future[String]

  def deleteCountry(countryId: Int): Future[Int]

}
