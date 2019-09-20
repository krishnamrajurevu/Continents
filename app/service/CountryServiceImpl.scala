package service

import dao.{ContinentDAO, CountryDAO}
import javax.inject.Inject
import model.{Continent, Country}

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._

class CountryServiceImpl @Inject()(countryDAO : CountryDAO, continentDAO : ContinentDAO,cityService: CityService) extends CountryService {


  override def getAllCountries(): Future[Seq[Country]] = {
    countryDAO.getAllCountries()
  }

  override def getAllCountriesByContinent(continentId: Int): Option[List[Country]] = {
    val countries= ListBuffer.empty ++= Await.result(countryDAO.getAllCountries(),5.seconds)
    countries.find(_.continentId == continentId) match {
      case Some(value) => Some(countries.filter(_.continentId == continentId).toList)
      case None => None
    }
  }

  override def createCountry(countryName: String, continentId: Int): Option[String] = {
    val continents = Await.result(continentDAO.getAllContinents, 5.seconds)
    continents.find(_.continentId == continentId) match {
      case Some(value) => val countries= ListBuffer.empty ++= Await.result(countryDAO.getAllCountries(),5.seconds)
        countries.find(_.countryName == countryName) match {
        case Some(value) => Some("Country already existed")
        case None => val country = Country(countries.maxByOption(_.countryId).map(_.countryId + 1).getOrElse(1), countryName, continentId)
          countryDAO.addCountry(country)
          countries+=country
          Some(s"Country $countryName added successfully")
      }
      case None => None
    }

  }

  override def deleteCountry(countryId: Int): Option[String] = {
    val countries= ListBuffer.empty ++= Await.result(countryDAO.getAllCountries(),5.seconds)
    countries.find(_.countryId == countryId) match {
      case Some(value) => {
        val message = cityService.removeCitiesOfCountry(countryId) match {
          case Some(value) => value
          case None => ""
        }
        countries.filterInPlace(_.countryId != countryId)
        countryDAO.deleteCountry(countryId)
        Some(s"Country  successfully deleted")
      }
      case None => None
    }
  }

  override def removeCountriesByContinent(continentId: Int): Option[String] = {
    val countries= ListBuffer.empty ++= Await.result(countryDAO.getAllCountries(),5.seconds)
    countries.find(_.continentId == continentId) match {
      case Some(value) => {
        val countryIds = countries.filter(_.continentId == continentId).map(_.countryId).toList
        val message: Option[String] = countryIds match {
          case ::(head, next) => cityService.removeCitiesOfCountry(countryIds)
          case Nil => None
        }
        countries.filterInPlace(_.continentId != continentId)
        countryDAO.deleteCountriesByContinent(continentId)
        message match {
          case Some(value) => Some("countries and " + value)
          case None => Some("countries ")
        }

      }
      case None => None
    }
  }

  override def getContinentByCountry(countryName: String): Option[String] = {
    val countries= Await.result(countryDAO.getAllCountries(),5.seconds)
    countries.filter(_.countryName == countryName).map(_.continentId).toList match {
      case ::(head, next) => val continents =  ListBuffer.empty ++= Await.result(continentDAO.getAllContinents, 5.seconds)
        continents.filter(_.continentId == head).map(_.continentName).toList match {
        case ::(head, next) => Some(head)
        case Nil => None
      }
      case Nil => None
    }
  }

  override def checkCountriesInSameContinent(firstCountry: String, secondCountry: String): Option[String] = {
    val countries= Await.result(countryDAO.getAllCountries(),5.seconds)
    countries.toList.filter(country => country.countryName == firstCountry || country.countryName == secondCountry).map(_.continentId) match {
      case list =>  if(list.size == 0)  Some(s"Countries $firstCountry and $secondCountry are not existed in any continent")
                    else if (list.size == 1) Some(s"One of country $firstCountry or $secondCountry not existed in any continent")
                    else if (list(0) == list(1)) Some(s"Countries $firstCountry and $secondCountry are in same continent")
                    else Some(s"Countries $firstCountry and $secondCountry are not in same continent")
      case Nil => Some(s"Countries $firstCountry and $secondCountry are not existed in any continent")
    }
  }
}
