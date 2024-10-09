package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment.commonHeader

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter
import scala.util.Random

object Common {

  val rnd = new Random()
  val now = LocalDate.now()
  val patternDay = DateTimeFormatter.ofPattern("dd")
  val patternMonth = DateTimeFormatter.ofPattern("MM")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")
  val patternDateClaim = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val BaseURL = Environment.baseURL
  val baseURLETUIApp = Environment.baseURLETUIApp

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").circular


  def postcodeLookup =
    feed(postcodeFeeder)
      .exec(http("XUI_Common_000_PostcodeLookup")
        .post(baseURLETUIApp + "/address-lookup")
        .headers(PostHeader)
      //  .formParam("postcode", "#{postcode}")
        .formParam("postcode", "#{postcode}")
        .header("content-type", "application/json")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}"))
       // .check(regex(""""(street1)_.+" : "(.+?)",(?s).*?"(?:street2)" : "(.+?)",.*?"town" : "(.+?)",.*?"country" : "(.+?)"""")
      //    .check(regex("""^"street1:"(.*\,$)""")
       // .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))
       // .check(regex("""<option value="([0-9]+)">""").findRandom.saveAs("addressIndex")))

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def randomNumber(length: Int) = {
    rnd.alphanumeric.filter(_.isDigit).take(length).mkString
  }

  def getDate(): String = {
    now.format(patternDate)
  }

  def getDateClaimAccepted(): String = {
    now.format(patternDateClaim)
  }
  
  def getCurrentDateTime (): String = {
    ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
  }

  def getDay(): String = {
    (1 + rnd.nextInt(28)).toString.format(patternDay).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  def getMonth(): String = {
    (1 + rnd.nextInt(12)).toString.format(patternMonth).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  //Date of Birth >= 35 years
  def getDobYear(): String = {
    now.minusYears(25 + rnd.nextInt(70)).format(patternYear)
  }

  //Date of Birth <= 18 years
  def getJobStartDate(): String = {
    now.minusYears(2 + rnd.nextInt(15)).format(patternYear)
  }

  //Date of Birth <= 18 years
  def getNoticeEndDate(): String = {
    now.plusYears(1).format(patternYear)
  }

  def getPostcode(): String = {
    randomString(2).toUpperCase() + rnd.nextInt(10).toString + " " + rnd.nextInt(10).toString + randomString(2).toUpperCase()
  }

 /*======================================================================================
  * Common XUI Calls
  ======================================================================================*/

  def healthcheck(path: String) =
    exec(http("XUI_Common_000_Healthcheck")
      .get(s"/api/healthCheck?path=${path}")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("""{"healthState":true}""")))

  val activity =
    exec(http("XUI_Common_000_ActivityOptions")
      .options(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 304, 403)))

  val caseActivityGet =
    exec(http("XUI_Common_000_ActivityOptions")
      .options(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 304, 403)))

    .exec(http("XUI_Common_000_ActivityGet")
      .get(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 304, 403)))

  val caseActivityOnlyGet =
    exec(http("XUI_Common_000_ActivityGet")
      .get(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 304, 403)))

  val caseActivityPost =
    exec(http("XUI_Common_000_ActivityOptions")
      .options(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 304, 403)))

    .exec(http("XUI_Common_000_ActivityPost")
      .post(BaseURL + "/activity/cases/#{caseId}/activity")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("sec-fetch-site", "same-site")
      .body(StringBody("{\n  \"activity\": \"view\"\n}"))
      .check(status.in(200, 201, 304, 403)))

  val configurationui =
    exec(http("XUI_Common_000_ConfigurationUI")
      .get(BaseURL + "/external/configuration-ui/")
      .headers(Headers.commonHeader)
      .header("accept", "*/*")
      .check(substring("ccdGatewayUrl")))

  val configJson =
    exec(http("XUI_Common_000_ConfigJson")
      .get(BaseURL + "/assets/config/config.json")
      .header("accept", "application/json, text/plain, */*")
      .check(substring("caseEditorConfig")))

  val TsAndCs =
    exec(http("XUI_Common_000_TsAndCs")
      .get(BaseURL + "/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("false")))

  val userDetails =
    exec(http("XUI_Common_000_UserDetails")
      .get(BaseURL + "/api/user/details")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*"))

  val configUI =
    exec(http("XUI_Common_000_ConfigUI")
      .get(BaseURL + "/external/config/ui")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("ccdGatewayUrl")))

  val isAuthenticated =
    exec(http("XUI_Common_000_IsAuthenticated")
      .get(BaseURL + "/auth/isAuthenticated")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(regex("true|false")))

  val profile =
    exec(http("XUI_Common_000_Profile")
      .get(BaseURL + "/data/internal/profile")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .check(jsonPath("$.user.idam.id").notNull))

  val monitoringTools =
    exec(http("XUI_Common_000_MonitoringTools")
      .get(BaseURL + "/api/monitoring-tools")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(jsonPath("$.key").notNull))

  val caseShareOrgs =
    exec(http("XUI_Common_000_CaseShareOrgs")
      .get(BaseURL + "/api/caseshare/orgs")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(jsonPath("$.name").notNull))

  val orgDetails =
    exec(http("XUI_Common_000_OrgDetails")
      .get(BaseURL + "/api/organisation")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(regex("name|Organisation route error"))
      .check(status.in(200, 304, 403)))

  val waJurisdictions = 
    exec(http("XUI_Common_000_WAJurisdictionsGet")
      .get(BaseURL + "/api/wa-supported-jurisdiction/get")
			.headers(Headers.commonHeader)
      .check(substring("[")))

// CHECK BODY ON THIS REQUEST ********************
  val waUsersByServiceName = 
    exec(http("XUI_Common_000_WAUsersByServiceName")
      .post(BaseURL + "/workallocation/caseworker/getUsersByServiceName")
			.headers(Headers.commonHeader)
      .header("Content-Type", "application/json; charset=utf-8")
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(substring("[")))

  val manageLabellingRoleAssignment =
    exec(http("XUI_Common_000_ManageLabellingRoleAssignments")
      .post(BaseURL + "/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(StringBody("{}"))
      .check(status.is(204))) 
      //No response body is returned, therefore no substring check is possible

  }