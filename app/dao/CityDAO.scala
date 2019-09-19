package dao

import com.google.inject.ImplementedBy
import model.City

import scala.concurrent.Future

@ImplementedBy(classOf[CityDAOImpl])
trait CityDAO {

  def getAllCities(): Future[Seq[City]]

  def addCity(city: City): Future[String]

  def deleteCity(cityId: Int): Future[Int]

  def deleteCitiesByCountry(countryId: Int): Future[Int]

  def deleteCitiesByCountry(countryIds: List[Int])

}
