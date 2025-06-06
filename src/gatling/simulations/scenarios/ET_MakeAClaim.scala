package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{AuthCheck, Common, CsrfCheck, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

import scala.concurrent.duration._
import scala.util.Random
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random


object ET_MakeAClaim {

  val BaseURL = Environment.baseURL
  val baseURLETUIApp = Environment.baseURLETUIApp
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Headers.commonHeader
  val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

  exec(_.setAll(
      "ETRandomString" -> (Common.randomString(7))))

    /*======================================================================================
    * Load the ET UI SYA home page
    ======================================================================================*/

    .exec(flushHttpCache)
    .exec(flushCookieJar)
    .exec(flushSessionCookies)

    .group("ET_010_Home") {
      exec(http("ET_010_005_Home")
        .get(baseURLETUIApp)
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .check(substring("Make a claim to an employment tribunal")))
    }

    .exec(getCookieValue(CookieKey("et-sya-session").withDomain(baseURLETUIApp.replace("https://", "")).withSecure(true).saveAs("etSession")))

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Click on 'Start now'
    ======================================================================================*/

    .group("ET_020_Start") {
      exec(http("ET_020_005_Start")
        .get(baseURLETUIApp + "/checklist")
        .headers(CommonHeader)
        .check(substring("Before you continue")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    /*======================================================================================
    * Click on 'Continue'
    ======================================================================================*/

   .group("ET_030_Before_You_Continue") {
      exec(http("ET_030_005_Before_You_Continue")
        .get(baseURLETUIApp + "/lip-or-representative?lng=en")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Claiming on your own")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    
    /*======================================================================================
    * Are you making the claim for yourself, or representing someone else? - my own claim
    ======================================================================================*/

    .group("ET_050_Claim_Yourself") {
      exec(http("ET_050_005_Claim_Yourself")
        .post(baseURLETUIApp + "/lip-or-representative?lng=en")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimantRepresentedQuestion", "No")
        .check(CsrfCheck.save)
        .check(substring("Are you making a claim on your own or with others?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Are you making a claim on your own or with others? - Own claim
    ======================================================================================*/

    .group("ET_060_Claim_Own_Or_Others") {
      exec(http("ET_060_005_Claim_Own_Or_Others")
        .post(baseURLETUIApp + "/single-or-multiple-claim?lng=en")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("caseType", "Single")
        .check(CsrfCheck.save)
        .check(substring("Where you can make your claim")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Where you can make your claim ? - England & Wales
    ======================================================================================*/

    .group("ET_065_Where_You_Can_Make_Claim") {
      exec(http("ET_065_005_ET_070_Where_You_Can_Make_Claim")
        .post(baseURLETUIApp + "/claim-jurisdiction-selection?lng=en")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimJurisdiction", "ET_EnglandWales")
        .check(CsrfCheck.save)
        .check(substring("Acas early conciliation certificate")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Do you have an ‘Acas early conciliation certificate’ for the respondent or respondents you're claiming against? - Yes
    ===============================================================================================*/

    .group("ET_070_ACAS_Certificate") {
      exec(http("ET_070_005_ACAS_Certificate")
        .post(baseURLETUIApp + "/do-you-have-an-acas-no-many-resps?lng=en")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("acasMultiple", "Yes")
        .check(CsrfCheck.save)
        .check(regex("""callback&state=(\w{8}-\w{4}-\w{4}-\w{4}-\w{12}-en)""").saveAs("state"))
        .check(substring("Sign in or create an account")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Log in
    ===============================================================================================*/

    .group("ET_090_Log_In") {
      exec(http("ET_090_005_Log_In")
        .post(IdamURL + "/login?client_id=et-sya&response_type=code&redirect_uri=" + baseURLETUIApp + "/oauth2/callback&state=#{state}&ui_locales=en")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .check(substring("You do not have to complete your claim in one go")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*===============================================================================================
    * You do not have to complete your claim in one go - Continue
    ===============================================================================================*/

      .group("ET_100_One_Go") {
        exec(http("ET_100_005_One_Go")
          .get(baseURLETUIApp + "/steps-to-making-your-claim")
          .headers(CommonHeader)
          .check(substring("Steps to making your claim")))
      }
      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Personal Details - Click Link
    ===============================================================================================*/

    .group("ET_110_Personal_Details") {
      exec(http("ET_110_005_Personal_Details")
        .get(baseURLETUIApp + "/dob-details")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What is your date of birth?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What is your date of birth?
    ===============================================================================================*/

    .group("ET_120_DoB") {
      exec(http("ET_120_005_DoB")
        .post(baseURLETUIApp + "/dob-details")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("dobDate-day", Common.getDay())
        .formParam("dobDate-month", Common.getMonth())
        .formParam("dobDate-year", Common.getDobYear())
        .check(CsrfCheck.save)
        .check(substring("Sex and preferred title")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Sex and preferred title
    ===============================================================================================*/

    .group("ET_130_Sex") {
      exec(http("ET_130_005_Sex")
        .post(baseURLETUIApp + "/sex-and-title")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimantSex", "Male")
        .formParam("preferredTitle", "")
        .check(CsrfCheck.save)
        .check(substring("What is your contact or home address?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What is your contact or home address? - Postcode look up
    ===============================================================================================*/

    .group("ET_140_Your_Address_LookUp") {
        exec(Common.postcodeLookup)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Enter address details
    ===============================================================================================*/

    .group("ET_145_Your_Address_Select") {
      exec(http("ET_145_005_Your_Address_Select")
        .post(baseURLETUIApp + "/address-details")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("address1", "address1" +  "#{ETRandomString}")
        .formParam("address2", "address2" + "#{ETRandomString}")
        .formParam("addressTown", "addressTown" + "#{ETRandomString}")
        .formParam("addressCountry", "addressCountry" + "#{ETRandomString}")
        .formParam("addressPostcode", "#{postcode}")
        .check(CsrfCheck.save)
        .check(substring("What is your telephone number")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What is your telephone number?
    ===============================================================================================*/

    .group("ET_150_Telephone_Number") {
      exec(http("ET_150_005_Telephone_Number")
        .post(baseURLETUIApp + "/telephone-number")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("telNumber", ("07712" + Common.randomNumber(6)))
        .check(CsrfCheck.save)
        .check(substring("What format would you like to be contacted in?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * How would you like to be contacted about your claim?
    ===============================================================================================*/

    .group("ET_160_Contact_Method") {
      exec(http("ET_160_005_Contact_Method")
        .post(baseURLETUIApp + "/how-would-you-like-to-be-updated-about-your-claim")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimantContactPreference", "Email")
        .check(CsrfCheck.save)
        .check(substring("Would you be able to take part in hearings by video and phone?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Would you be able to take part in hearings by video and phone? - Yes, I can take part in video hearings
    ===============================================================================================*/

    .group("ET_170_Hearing_Participation") {
      exec(http("ET_170_005_Hearing_Participation")
        .post(baseURLETUIApp + "/would-you-want-to-take-part-in-video-hearings")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("hearingPreferences", "Video")
        .formParam("hearingAssistance", "")
        .check(CsrfCheck.save)
        .check(substring("Do you have a physical, mental or learning disability or long term health condition that means you need support during your case?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Extra support during your case? - No, I do not need any extra support at this time
    ===============================================================================================*/

    .group("ET_180_Need_Support") {
      exec(http("ET_180_005_Need_Support")
        .post(baseURLETUIApp + "/reasonable-adjustments")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("reasonableAdjustmentsDetail", "")
        .formParam("reasonableAdjustments", "No")
        .check(CsrfCheck.save)
        .check(substring("Have you completed this section?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Have you completed this section?
    ===============================================================================================*/

    .group("ET_190_Contact_Completed") {
      exec(http("ET_190_005_Contact_Completed")
        .post(baseURLETUIApp + "/personal-details-check")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("personalDetailsCheck", "Yes")
        .check(substring("Personal details\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Contact details\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Your preferences\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Steps to making your claim")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)



}
