package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

object RespondentLogin {
  
  val IdamUrl = Environment.idamURL
  val XuiUrl = Environment.respLoginURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val XUIHomePage =

    exec(flushHttpCache)
      .exec(flushCookieJar)

      .group("XUI_010_Homepage") {
        exec(http("XUI_010_005_Homepage")
          .get("/")
          .headers(Headers.commonHeader)
          .header("sec-fetch-site", "none"))

          .exec(Common.configurationui)

          .exec(Common.configJson)

          .exec(Common.TsAndCs)

          .exec(Common.configUI)

          .exec(Common.userDetails)

          .exec(Common.isAuthenticated)

          .exec(http("XUI_010_010_AuthLogin")
            .get(XuiUrl + "/auth/login")
            //.get("https://manage-case.#{env}.platform.hmcts.net")
            .headers(Headers.commonHeader)
            .check(regex("/oauth2/callback&amp;state=(.*)&amp;nonce=").saveAs("state"))
            .check(regex("&nonce=(.*)&response_type").saveAs("nonce")))
      }

      .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Manage Case Login
  *=====================================================================================*/

  val XUILogin =

    group("XUI_020_Login") {
      exec(http("XUI_020_005_Login")
        .post(IdamUrl + "/login?client_id=xuiwebapp&redirect_uri=" + XuiUrl + "/oauth2/callback&state=${state}&nonce=${nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user%20search-user&prompt=")
        .formParam("username", "${user}")
        .formParam("password", "${password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrf}")
        //.headers(Headers.commonHeader)
        .headers(Headers.postHeader)
        .check(regex("Manage cases")))

        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.TsAndCs)

        .exec(Common.configUI)

        .exec(Common.userDetails)

        .exec(Common.isAuthenticated)

        .exec(Common.monitoringTools)

          //if there is no in-flight case, set the case to 0 for the activity calls
      .doIf("${caseId.isUndefined()}") {
        exec(_.set("caseId", "0"))
      }

      .exec(http("XUI_020_010_Jurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("id")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(XuiUrl.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.orgDetails)
      
      .exec(http("XUI_020_015_WorkBasketInputs")
        .get("/data/internal/case-types/${caseType}/work-basket-inputs")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8")
        .check(regex("workbasketInputs|Not Found"))
        .check(status.in(200, 404)))

      .exec(http("XUI_020_020_SearchCases")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns")))


    }
    .pause(MinThinkTime , MaxThinkTime)



}