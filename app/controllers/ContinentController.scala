package controllers

import javax.inject.Inject
import model.{City, CityRef, ContinentRef, Country, CountryRef}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, MessagesAbstractController, MessagesControllerComponents}
import service.{CityService, ContinentService, CountryService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContext, Future}

class ContinentController @Inject()(continentService: ContinentService,countryService : CountryService,cityService : CityService, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {



  def getAllContinents = Action.async { implicit  request =>
    continentService.getAllContinents.map{ continents =>
      Ok(Json.toJson(continents))
    }
  }

  def addContinent: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[ContinentRef].fold(
      error =>{
        Future.successful(BadRequest("Some thing went wrong"+error))
      },
      continent => {
        continentService.addContinent(continent.continentName) match {
          case Some(value) =>Future.successful(Ok("successfully added"))
          case None =>Future.successful(Ok("Continent already added"))
        }
      }
    )
  }

  def deleteContinent(continentId : Int) = Action.async { implicit request =>
    continentService.deleteContinent(continentId) match {
      case Some(value) =>Future.successful(Ok("successfully deleted"))
      case None =>Future.successful(Ok("Continent id not found"))
    }
  }

  def getAllCountries() = Action.async { implicit  request =>
    countryService.getAllCountries().map{  countries =>
      Ok(Json.toJson(countries))
    }

  }

  def getCountriesOfContinent(continentId: Int) = Action { implicit request =>
    val listOfCountries = countryService.getAllCountriesByContinent(continentId) match {
      case Some(value) => value
      case None => List[Country]().empty
    }
    Ok(Json.toJson(listOfCountries))
  }

  def addCountryToContinent() : Action[JsValue]  =Action.async(parse.json) {   request =>
    request.body.validate[CountryRef].fold(
      error => {
        Future.successful(BadRequest("Some thing went wrong"+error))
      },
        country =>{
          countryService.createCountry(country.countryName, country.continentId) match {
            case Some(value) =>Future.successful(Ok(value))
            case None =>Future.successful(Ok("Country already added"))
          }
        }
    )
  }

  def deleteCountry(countryId : Int) = Action.async { implicit request =>
    countryService.deleteCountry(countryId) match {
      case Some(value) =>Future.successful(Ok("successfully deleted"))
      case None =>Future.successful(Ok("Country id not found"))
    }
  }

  def getAllCities() = Action.async { implicit  request =>
    cityService.getAllCities().map{  cities =>
      Ok(Json.toJson(cities))
    }

  }

  def getCitiesOfCountry(countryId: Int) = Action { implicit  request =>

    val listOfCities = cityService.getAllCitiesByCountry(countryId)
    Ok(Json.toJson(listOfCities))

  }

  def addCityToCountry() : Action[JsValue]  =Action.async(parse.json) {   request =>
    request.body.validate[CityRef].fold(
      error => {
        Future.successful(BadRequest("Some thing went wrong"+error))
      },
      city =>{
        println("city :"+city)
       cityService.createCity(city.cityName,city.countryId) match {
          case Some(value) =>Future.successful(Ok(value))
          case None =>Future.successful(Ok("City already added"))
        }
      }
    )
  }

  def deleteCity(cityId : Int) = Action.async { implicit request =>
    cityService.deleteCity(cityId) match {
      case Some(value) =>Future.successful(Ok("successfully deleted"))
      case None =>Future.successful(Ok("City id not found"))
    }
  }

  def getContinent(countryName: String) = Action {
    val continentName = countryService.getContinentByCountry(countryName) match {
      case Some(value) => s"Continent of $countryName is $value"
      case None => s"Continent of $countryName is not found"
    }
    Ok(continentName)

  }

  def getAllCitiesByContinent(continentName: String) = Action {
    val cities = cityService.getAllCitiesByContinent(continentName) match {
      case Some(value) => value
      case None => List[String]().empty
    }
    Ok(Json.toJson(cities))
  }

  def countriesLieInContinent(firstCountry: String, secondCountry: String) = Action {
    val message: String = countryService.checkCountriesInSameContinent(firstCountry, secondCountry) match {
      case Some(value) => value
      case None => "Some thing went wrong"
    }
    Ok(message)
  }

  def createMultipleCities() = Action.async(parse.json) {   request =>

    request.body.validate[CityRef].fold(
      error => {
        Future.successful(BadRequest("Some thing went wrong"+error))
      },
      city =>{
        val citiesList = city.cityName.split(",")
        var result = new ListBuffer[String]
        for (cityName <- citiesList if ! cityName.trim.isEmpty) {
          val msg = cityService.createCity(cityName,city.countryId) match {
            case Some(value) =>  value
            case None => s"$cityName not added"
          }
          result += msg
        }
        Future.successful(Ok(Json.toJson(result)))
      }
    )
  }

  def getGroupedCities(cityFirstChar: Char) = Action {
    val groupCities: Map[Char, Seq[String]] = cityService.getCitiesByGroup()
    val cities = groupCities.get(cityFirstChar)
    val listOfCities = cities match {
      case Some(cities) => cities.toList
      case None => List[String]().empty
    }
   Ok(Json.toJson(listOfCities))
  }



}
