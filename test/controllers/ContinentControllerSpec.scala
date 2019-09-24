package controllers

import akka.util.ByteString
import model.{CityRef, Continent, ContinentRef, ContinentResponse, ContinentStrResponse, CountryRef}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._

import scala.concurrent.Future


class ContinentControllerSpec extends PlaySpec with GuiceOneAppPerSuite {


  "render a page" when {
    "we try to hit the route /" in {
      route(app, FakeRequest(GET, "/")).map(status(_)) mustBe Some(OK)
    }
  }

  /**
   * Continent related test cases
   */

  "get empty continents" should {
    "returns 200 if successfully get zero records" in {
      val request = FakeRequest(GET, "/getContinents").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of continents\",\"status\":\"success\",\"continent\":[]}"
    }
  }

  "Create empty Continent " in {
    val request = route(app, FakeRequest(POST, "/createContinent")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(ContinentRef("")))).get
    status(request) mustBe BAD_REQUEST
    contentType(request) mustBe Some("application/json")
  }

  "Create Continent " in {
    val request = route(app, FakeRequest(POST, "/createContinent")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(ContinentRef("Asia")))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "get all continent" should {
    "returns 200 if successfully get one record" in {
      val request = FakeRequest(GET, "/getContinents").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of continents\",\"status\":\"success\",\"continent\":[{\"continentId\":1,\"continentName\":\"ASIA\"}]}"
    }
  }

  "deleting continent" should {
    "returns 404 if no such record" in {
      val request = FakeRequest(DELETE, "/deleteContinent/10").withHeaders("admin" -> "qvantel")
      val destroy = route(app, request).get
      status(destroy) mustBe NOT_FOUND
    }
  }


  "returns 401 if delete continent by user as not admin" in {
    val request = FakeRequest(DELETE, "/deleteContinent/1").withHeaders("admin" -> "intense")
    val destroy = route(app, request).get
    status(destroy) mustBe UNAUTHORIZED
  }

  "returns 204 if successfully deleted continent and user is Admin" in {
    val request = FakeRequest(DELETE, "/deleteContinent/1").withHeaders("admin" -> "qvantel")
    val destroy = route(app, request).get
    status(destroy) mustBe OK
  }

  /**
   * Countries related test cases
   */

  "get empty countries" should {
    "returns 200 if successfully get zero records" in {
      val request = FakeRequest(GET, "/getCountries").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of all countries\",\"status\":\"success\",\"countries\":[]}"
    }
  }

  "Create empty countries " in {
    val request = route(app, FakeRequest(POST, "/createCountry")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CountryRef("", 1)))).get
    status(request) mustBe BAD_REQUEST
    contentType(request) mustBe Some("application/json")
  }

  "Create Continent for test country " in {
    val request = route(app, FakeRequest(POST, "/createContinent")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(ContinentRef("Asia")))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "check countries lies in same continent" should {
    "returns 200 and both countries not existed " in {
      val request = FakeRequest(GET, "/countriesLieInContinent/India/Kenya").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"Countries INDIA and KENYA are not existed in any continent\",\"status\":\"Success\"}"
    }
  }

  "Create country " in {
    val request = route(app, FakeRequest(POST, "/createCountry")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CountryRef("India", 1)))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "get all countries of specific continent" should {
    "returns 200 if successfully get one record" in {
      val request = FakeRequest(GET, "/getCountriesByContinent/1").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of countries \",\"status\":\"Success\",\"countries\":[{\"countryId\":1,\"countryName\":\"INDIA\",\"continentId\":1}]}"
    }
  }

  "check countries lies in same continent if one country only existed" should {
    "returns 200 and one country not existed " in {
      val request = FakeRequest(GET, "/countriesLieInContinent/India/Kenya").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"Either country INDIA or KENYA not existed in any continent\",\"status\":\"Success\"}"
    }
  }

  "Create country for checking lies in continent " in {
    val request = route(app, FakeRequest(POST, "/createCountry")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CountryRef("Kenya", 1)))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "check countries lies in same continent if both countries existed" should {
    "returns 200 and both in countries existed " in {
      val request = FakeRequest(GET, "/countriesLieInContinent/India/Kenya").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"Countries INDIA and KENYA are in same continent\",\"status\":\"Success\"}"
    }
  }




  "get continent name of country" should {
    "returns 404 if  country not exist " in {
      val request = FakeRequest(GET, "/getContinent/Pakistan").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe NOT_FOUND
      contentAsString(records) mustBe "{\"message\":\"Country Pakistan is not existed\",\"status\":\"Failed\"}"
    }
  }

  "get continent name of country" should {
    "returns 200 if  country exist " in {
      val request = FakeRequest(GET, "/getContinent/India").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"Continent of India is ASIA\",\"status\":\"Success\"}"
    }
  }



  "deleting country" should {
    "returns 404 if no such record" in {
      val request = FakeRequest(DELETE, "/deleteCountry/10").withHeaders("admin" -> "qvantel")
      val destroy = route(app, request).get
      status(destroy) mustBe NOT_FOUND
    }
  }


  "returns 401 if delete country by user as not admin" in {
    val request = FakeRequest(DELETE, "/deleteCountry/1").withHeaders("admin" -> "intense")
    val destroy = route(app, request).get
    status(destroy) mustBe UNAUTHORIZED
  }

  "returns 204 if successfully deleted country india and user is Admin" in {
    val request = FakeRequest(DELETE, "/deleteCountry/1").withHeaders("admin" -> "qvantel")
    val destroy = route(app, request).get
    status(destroy) mustBe OK
  }

  "returns 204 if successfully deleted country kenya and user is Admin" in {
    val request = FakeRequest(DELETE, "/deleteCountry/2").withHeaders("admin" -> "qvantel")
    val destroy = route(app, request).get
    status(destroy) mustBe OK
  }


  /**
   * Cities related test cases
   */

  "get empty cities" should {
    "returns 200 if successfully get zero records" in {
      val request = FakeRequest(GET, "/getCities").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of cities\",\"status\":\"Success\",\"cities\":[]}"
    }
  }

  "Create empty city " in {
    val request = route(app, FakeRequest(POST, "/createCity")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CountryRef("", 1)))).get
    status(request) mustBe BAD_REQUEST
    contentType(request) mustBe Some("application/json")
  }

  "Create Continent for test city " in {
    val request = route(app, FakeRequest(POST, "/createContinent")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(ContinentRef("Asia")))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "Create country for city test " in {
    val request = route(app, FakeRequest(POST, "/createCountry")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CountryRef("India", 1)))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "Create city " in {
    val request = route(app, FakeRequest(POST, "/createCity")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CityRef("Hyderabad", 1)))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
  }

  "get all cities of specific country" should {
    "returns 200 if successfully get one record" in {
      val request = FakeRequest(GET, "/getCitiesByCountry/1").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"List of cities\",\"status\":\"Success\",\"cities\":[{\"cityId\":1,\"cityName\":\"HYDERABAD\",\"countryId\":1}]}"
    }
  }

  "get all cities of specific continent" should {
    "returns 200 if successfully get one record" in {
      val request = FakeRequest(GET, "/getCities/Asia").withHeaders("admin" -> "qvantel")
      val records = route(app, request).get
      status(records) mustBe OK
      contentAsString(records) mustBe "{\"message\":\"Get all cities of continent\",\"status\":\"Success\",\"cities\":[\"HYDERABAD\"]}"
    }
  }

  "Create multiple cities " in {
    val request = route(app, FakeRequest(POST, "/createMultipleCities")
      .withHeaders("admin" -> "qvantel")
      .withJsonBody(Json.toJson(CityRef("Hyderabad,Delhi, Mumbai , Kolkata", 1)))).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
    contentAsString(request) mustBe "{\"message\":\"Created multiple cities successfully \",\"status\":\"Success\",\"cities\":[\"City HYDERABAD already added\",\"City DELHI created successfully\",\"City MUMBAI created successfully\",\"City KOLKATA created successfully\"]}"
  }

  "Grouped cities " in {
    val request = route(app, FakeRequest(GET, "/getCitiesByGroup/h")
      .withHeaders("admin" -> "qvantel")).get
    status(request) mustBe OK
    contentType(request) mustBe Some("application/json")
    contentAsString(request) mustBe "{\"message\":\"Grouped cities\",\"status\":\"Success\",\"cities\":[\"HYDERABAD\"]}"
  }

  "deleting city" should {
    "returns 404 if no such record" in {
      val request = FakeRequest(DELETE, "/deleteCity/10").withHeaders("admin" -> "qvantel")
      val destroy = route(app, request).get
      status(destroy) mustBe NOT_FOUND
    }
  }


  "returns 401 if delete city by user as not admin" in {
    val request = FakeRequest(DELETE, "/deleteCity/1").withHeaders("admin" -> "intense")
    val destroy = route(app, request).get
    status(destroy) mustBe UNAUTHORIZED
  }

  "returns 204 if successfully deleted city and user is Admin" in {
    val request = FakeRequest(DELETE, "/deleteCity/1").withHeaders("admin" -> "qvantel")
    val destroy = route(app, request).get
    status(destroy) mustBe OK
  }


}
