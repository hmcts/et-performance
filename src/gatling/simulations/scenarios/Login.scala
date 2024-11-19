package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment.{commonHeader, commonHeader1, postHeader}
import utils.{Common, Environment, Headers, CsrfCheck}

object Login {

  val BaseURL = Environment.baseURL
  val baseURLETUIResp = Environment.baseURLETUIResp
  val IdamUrl = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Manage Case Login
  *=====================================================================================*/

  val XUILogin =

    group("XUI_020_Login") {
      exec(http("XUI_020_005_Login")
        .post(IdamUrl + "/login?client_id=xuiwebapp&redirect_uri=" + BaseURL + "/oauth2/callback&state=#{state}&nonce=#{nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user%20search-user&prompt=")
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("azureLoginEnabled", "true")
        .formParam("mojLoginEnabled", "true")
        .formParam("_csrf", "#{csrf}")
        .headers(commonHeader1)
        .headers(postHeader)
        .check(regex("Manage cases")))

      .exec(Common.configurationui)
      .exec(Common.configJson)
      .exec(Common.TsAndCs)
      .exec(Common.configUI)
      .exec(Common.userDetails)
      .exec(Common.isAuthenticated)
      .exec(Common.monitoringTools)
      .exec(Common.isAuthenticated)
      



      //if there is no in-flight case, set the case to 0 for the activity calls
      .doIf("#{caseId.isUndefined()}") {
        exec(_.set("caseId", "0"))
      }

    .pause(MinThinkTime , MaxThinkTime)

      //.exec(Common.caseActivityGet)
      //  .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    /*  .exec(http("XUI_020_010_Jurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        //.headers(commonHeader)
        .headers(postHeader)
        .header("accept", "application/json")
        .check(substring("id")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.orgDetails)*/
      
    /*  .exec(http("XUI_020_015_WorkBasketInputs")
        .get("/data/internal/case-types/CIVIL/work-basket-inputs")
        .headers(commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8")
        .check(regex("workbasketInputs|Not Found"))
        .check(status.in(200, 404)))

      .exec(http("XUI_020_020_SearchCases")
        .post("/data/internal/searchCases?ctid=CIVIL&use_case=WORKBASKET&view=WORKBASKET&page=1")
        .headers(commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns")))*/

    }


/*====================================================================================
  *Citizen Login (Respondent)
  *=====================================================================================*/

  val CUILogin =

    group("CUI_XXX_Login") {
      exec(http("CUI_XXX_005_Login")
        .post(IdamUrl + "/login?client_id=et-syr&response_type=code&redirect_uri=" + baseURLETUIResp + "/oauth2/callback&state=#{state}&&ui_locales=en")
        .headers(commonHeader1)
        .headers(postHeader)
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "#{csrf}")
        .check(CsrfCheck.save)
        .check(substring("Before you continue")))
    }
}
