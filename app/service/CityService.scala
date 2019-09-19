package service

import com.google.inject.ImplementedBy
import model.City

import scala.concurrent.Future

@ImplementedBy(classOf[CityServiceImpl])
trait CityService {

  def getAllCities() : Future[Seq[City]]

  def getAllCitiesByCountry(countryId: Int) : List[City]

  def createCity(cityName: String, countryId: Int): Option[String]

  def deleteCity(cityId: Int) : Option[String]

  def getAllCitiesByContinent(continentName: String):Option[List[String]]

  def getCitiesByGroup(): Map[Char, Seq[String]]

}
