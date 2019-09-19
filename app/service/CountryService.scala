package service

import com.google.inject.ImplementedBy
import model.{Country}

import scala.concurrent.Future

@ImplementedBy(classOf[CountryServiceImpl])
trait CountryService  {

  def getAllCountries() : Future[Seq[Country]]

  def getAllCountriesByContinent(continentId: Int) : Option[List[Country]]

  def createCountry(countryName: String, continentId: Int): Option[String]

  def deleteCountry(countryId: Int) : Option[String]

  def getContinentByCountry(countryName: String) : Option[String]

  def checkCountriesInSameContinent(firstCountry: String, secondCountry: String) :Option[String]

}
