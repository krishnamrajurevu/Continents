package service


import dao.{CityDAO, ContinentDAO, CountryDAO}
import javax.inject.Inject
import model.City

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._

class CityServiceImpl @Inject()(cityDAO : CityDAO,countryService: CountryService,countryDAO: CountryDAO,continentDAO: ContinentDAO) extends CityService {
  override def getAllCities(): Future[Seq[City]] = {
    cityDAO.getAllCities()
  }

  override def getAllCitiesByCountry(countryId: Int): List[City] = {
    val cities=  Await.result(cityDAO.getAllCities(),5.seconds)
    for (city <- cities.toList if city.countryId == countryId) yield city
  }

  override def createCity(cityName: String, countryId: Int): Option[String] = {
    val countries= Await.result(countryDAO.getAllCountries(),5.seconds)
    println("countries ::"+countries)
    countries.find(_.countryId == countryId) match {
      case Some(value) => val cities = ListBuffer.empty ++=  Await.result(cityDAO.getAllCities(),5.seconds)
        println("cities ::"+cities)
        cities.find(_.cityName == cityName) match {
          case Some(value) =>  Some(s"City $cityName already added")
          case None => val city = City(cities.maxByOption(_.cityId).map(_.cityId + 1).getOrElse(1), cityName, countryId)
            println("city ::"+city)
            cityDAO.addCity(city)
            Some(s"City $cityName created successfully")
        }
      case None => None
    }
  }

  override def deleteCity(cityId: Int): Option[String] = {
    val cities = ListBuffer.empty ++=  Await.result(cityDAO.getAllCities(),5.seconds)
    cities.find(_.cityId == cityId) match {
      case Some(city) =>
        cities.filterInPlace(_.cityId != cityId)
        cityDAO.deleteCity(cityId)
        Some("City successfully deleted")
      case None => None
    }
  }

  override def getAllCitiesByContinent(continentName: String): Option[List[String]] = {
    val continents= ListBuffer.empty ++= Await.result(continentDAO.getAllContinents,5.seconds)
    val continentIds: List[Int] = continents.filter(_.continentName == continentName).map(_.continentId).toList
    continentIds match {
      case ::(head, next) => countryService.getAllCountriesByContinent(head) match {
        case Some(value) => val countryIds = value.map(_.countryId)
          countryIds match {
            case ::(head, next) => val cities = Await.result(cityDAO.getAllCities(),5.seconds)
              Some(cities.filter(city => countryIds.contains(city.countryId)).map(_.cityName).toList)
            case Nil => None
          }
        case None => None
      }
      case Nil => None
    }
  }

  override def getCitiesByGroup(): Map[Char, Seq[String]] = {
    val cities = Await.result(cityDAO.getAllCities(),5.seconds)
    cities.map(_.cityName).groupBy(_.charAt(0))
  }
}
