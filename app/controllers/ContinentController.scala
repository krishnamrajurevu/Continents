package controllers

import javax.inject.Inject
import model.{CityListResponse, CityRef, CityResponse, ContinentRef, ContinentResponse, ContinentStrResponse, Country, CountryRef, CountryResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, Request}
import service.{CityService, ContinentService, CountryService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class ContinentController @Inject()(authAction: AuthenticatedUserAction,continentService: ContinentService,countryService : CountryService,cityService : CityService, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def getAllContinents = authAction.async { implicit  request =>
    continentService.getAllContinents.map{ continents =>
      Ok(Json.toJson(ContinentResponse("List of continents","success",continents)))
    }
  }

  def addContinent: Action[JsValue] = authAction.async(parse.json) { request =>
    request.body.validate[ContinentRef].fold(
      error =>{
        Future.successful(BadRequest(Json.toJson(ContinentStrResponse("Create Continent failed","Some thing went wrong::"+error.toString()))))
      },
      continent => {
        if (!Option(continent.continentName.trim).getOrElse("").isEmpty){
          continentService.addContinent(continent.continentName.trim.toUpperCase()) match {
            case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Continent created successfully","Success"))))
            case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Continent already added","Failed"))))
          }
        }else{
          Future.successful(Ok(Json.toJson(ContinentStrResponse("Continent name should not be empty","Failed"))))
        }

      }
    )
  }

  def deleteContinent(continentId : Int) = authAction.async { implicit request =>
    continentService.deleteContinent(continentId) match {
      case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Continent deleted successfully","Success"))))
      case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Continent id not found","Failed"))))
    }
  }

  def getAllCountries() = authAction.async { implicit  request =>
    countryService.getAllCountries().map{  countries =>
      Ok(Json.toJson(CountryResponse("List of all countries","success",countries)))
    }

  }

  def getCountriesOfContinent(continentId: Int) = authAction { implicit request =>
    val listOfCountries = countryService.getAllCountriesByContinent(continentId) match {
      case Some(value) => value
      case None => List[Country]().empty
    }
    Ok(Json.toJson(CountryResponse("List of countries ","Success",listOfCountries)))
  }

  def addCountryToContinent() : Action[JsValue]  =authAction.async(parse.json) {   request =>
    request.body.validate[CountryRef].fold(
      error => {
        Future.successful(BadRequest(Json.toJson(ContinentStrResponse("Add country to continent","Some thing went wrong"+error))))
      },
        country =>{
          if (!Option(country.countryName.trim).getOrElse("").isEmpty){
            countryService.createCountry(country.countryName.trim.toUpperCase(), country.continentId) match {
              case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Add country to continent",value))))
              case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Add country to continent","Given data not found"))))
            }
          }else{
            Future.successful(Ok(Json.toJson(ContinentStrResponse("Add country to continent","Data should not be empty"))))
          }
        }
    )
  }

  def deleteCountry(countryId : Int) = authAction.async { implicit request =>
    countryService.deleteCountry(countryId) match {
      case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Delete country successfully ","Success"))))
      case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Given data not found","Failed"))))
    }
  }

  def getAllCities() = authAction.async { implicit  request =>
    cityService.getAllCities().map{  cities =>
      Ok(Json.toJson(CityResponse("List of cities","Success",cities)))
    }

  }

  def getCitiesOfCountry(countryId: Int) = authAction { implicit  request =>

    val listOfCities = cityService.getAllCitiesByCountry(countryId)
    Ok(Json.toJson(CityResponse("List of cities","Success",listOfCities)))

  }

  def addCityToCountry() : Action[JsValue]  =authAction.async(parse.json) {   request =>
    request.body.validate[CityRef].fold(
      error => {
        Future.successful(BadRequest(Json.toJson(ContinentStrResponse("Add city to country","Some thing went wrong"+error))))
      },
      city =>{
        if (!Option(city.cityName.trim).getOrElse("").isEmpty){
          cityService.createCity(city.cityName.trim.toUpperCase(),city.countryId) match {
            case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Add city to country","Success"))))
            case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Given data not found","Failed"))))
          }
        }else{
          Future.successful(Ok(Json.toJson(ContinentStrResponse("Data should ot be empty","Failed"))))
        }
      }
    )
  }

  def deleteCity(cityId : Int) = authAction.async { implicit request =>
    cityService.deleteCity(cityId) match {
      case Some(value) =>Future.successful(Ok(Json.toJson(ContinentStrResponse("City deleted successfully","Success"))))
      case None =>Future.successful(Ok(Json.toJson(ContinentStrResponse("Given data not found","Failed"))))
    }
  }

  def getContinent(countryName: String) = authAction {
    if (!Option(countryName.trim).getOrElse("").isEmpty){
      countryService.getContinentByCountry(countryName) match {
        case Some(value) =>  Ok(Json.toJson(ContinentStrResponse(s"Continent of $countryName is $value","Success")))
        case None =>  Ok(Json.toJson(ContinentStrResponse(s"Country $countryName is not existed","Failed")))
      }
    }else{
      Ok(Json.toJson(ContinentStrResponse("Data should not be empty","Failed")))
    }

  }

  def getAllCitiesByContinent(continentName: String) = authAction {
    if (!Option(continentName.trim).getOrElse("").isEmpty){
      val cities = cityService.getAllCitiesByContinent(continentName) match {
        case Some(value) => value
        case None => List[String]().empty
      }
      Ok(Json.toJson(CityListResponse("Get all cities of continent","Success",cities)))
    }else{
      Ok(Json.toJson(ContinentStrResponse("Data should not be empty","Failed")))
    }

  }

  def countriesLieInContinent(firstCountry: String, secondCountry: String) = authAction {
    countryService.checkCountriesInSameContinent(firstCountry.toUpperCase(), secondCountry.toUpperCase()) match {
      case Some(value) =>  Ok(Json.toJson(ContinentStrResponse(value,"Success")))
      case None =>Ok(Json.toJson(ContinentStrResponse("Some thing went wrong","Failed")))
    }
  }

  def createMultipleCities() = authAction.async(parse.json) {   request =>

    request.body.validate[CityRef].fold(
      error => {
        Future.successful(BadRequest("Some thing went wrong"+error))
      },
      city =>{
        if (!Option(city.cityName.trim).getOrElse("").isEmpty){
          val citiesList = city.cityName.trim.split(",")
          var result = new ListBuffer[String]
          for (cityName <- citiesList if ! cityName.trim.isEmpty) {
            if (!Option(cityName.trim).getOrElse("").isEmpty){
              val msg = cityService.createCity(cityName.trim.toUpperCase(),city.countryId) match {
                case Some(value) =>  value
                case None => s"Country Id ${city.countryId} not found"
              }
              result += msg
            }
          }
          Future.successful(Ok(Json.toJson(CityListResponse("Create multiple cities ","Success",result.toList))))
        }else{
          Future.successful(Ok(Json.toJson(ContinentStrResponse("Data should not be empty","Failed"))))
        }

      }
    )
  }

  def getGroupedCities(cityFirstChar: Char) = authAction {
    val groupCities: Map[Char, Seq[String]] = cityService.getCitiesByGroup()
    val cities = groupCities.get(cityFirstChar)
    val listOfCities = cities match {
      case Some(cities) => cities.toList
      case None => List[String]().empty
    }
   Ok(Json.toJson(CityListResponse("Grouped cities","Success",listOfCities)))
  }
}
