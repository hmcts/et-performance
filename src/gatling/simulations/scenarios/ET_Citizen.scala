package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers, CsrfCheck}

import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._

object ET_Citizen {

  val IdamURL = Environment.idamURL
  val baseUrlET = Environment.baseURLETUIResp

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val CitUILandingHeader = Headers.citUILandingHeader
  val CitUICommonHeader = Headers.citUICommonHeader
  val postcodeFeeder = csv("postcodes.csv").circular

  val RespondentIntroduction =

  exec(_.setAll(
      "ETCZRandomString" -> (Common.randomString(7)),
      "ETCZRandomNumber" -> (Common.randomNumber(10))))
  
  /*======================================================================================
  https://et-syr.perftest.platform.hmcts.net/ - Landing Page --> Start Now
  ==========================================================================================*/

    .exec(http("ET_CTZ_010_LandingPage")
      .get(baseUrlET + "/")
      .headers(CitUILandingHeader)
      .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*=========================================================================================
  ET3 Start Now
  ==========================================================================================*/

  .group("ET_CTZ_020_StartNow") {
    exec(http("ET_CTZ_020_StartNow")
      .get(baseUrlET + "/interruption-card")
      .headers(CitUICommonHeader)
      .check(status.is(200))
      //.check(jsonPath("$.event_token").optional.saveAs("event_token"))
    )
  }
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Interruption Card --> Continue
  ==========================================================================================*/

  .group("ET_CTZ_030_InterruptionCardContinue") {
    exec(http("ET_CTZ_030_InterruptionCardContinue")
      .get(baseUrlET + "/case-list-check")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(regex("/oauth2/callback&amp;state=(.*)&amp;ui_locales=").saveAs("state"))
      .check(substring("Sign in"))
      .check(status.is(200))
    )
  }
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentCaseNumberCheck = 

   exec(_.setAll(
      "ETCZRandomString" -> (Common.randomString(7)),
      "ETCZRandomNumber" -> (Common.randomNumber(10))))
  
  /*======================================================================================
  https://et-syr.perftest.platform.hmcts.net/ - Landing Page --> Start Now
  ==========================================================================================*/

    .exec(http("ET_CTZ_010_LandingPage")
      .get(baseUrlET + "/")
      .headers(CitUILandingHeader)
      .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)
 
  /*======================================================================================
  Select start now --> Case number check
  ==========================================================================================*/

    .exec(http("ET_CTZ_032_CaseNumberCheckLanding")
      .get(baseUrlET + "/case-number-check")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(status.is(200))
      .check(substring("What is the case number of the case"))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Enter Case Number 
  ==========================================================================================*/

   .group("ET_CTZ_033_EnterCaseNumber") {
      exec(http("ET_CTZ_033_005_EnterCaseNumber")
        .post(baseUrlET + "/case-number-check")
        .headers(CitUICommonHeader)
        .header("Origin", "https://et-syr.perftest.platform.hmcts.net")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("ethosCaseReference", "#{ethosCaseRef}")
        .formParam("hiddenErrorField", "")
        .check(CsrfCheck.save)
        .check(regex("/oauth2/callback&amp;state=(.*)&amp;ui_locales=").saveAs("state"))
        .check(substring("Sign in or create an account"))
        //.check(status.is(200))
      )
   }
  .pause(MinThinkTime, MaxThinkTime)

  val BeforeYouContinue =

  /*======================================================================================
  Before you continue -> Continue
  ==========================================================================================*/

  group("ET_CTZ_034_ChecklistContinue") {
    exec(http("ET_CTZ_034_005_ChecklistContinue")
      .get(baseUrlET + "/case-list-check")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(status.is(200))
      .check(substring("Case Details").count.saveAs("noCaseAssignedToUserCount"))
      .check(substring("ET3 Responses").count.saveAs("casesAssignedToUserCount"))
      .check(substring("#{caseId}").count.saveAs("correctCaseAssignedToUserCount"))
      .check(regex("""<td class="govuk-table__cell"> <a href="/case-details/#{caseId}/([a-z0-9\-]{36})\?lng=en"""").optional.saveAs("caseDetailID"))
    )
  }
  .pause(MinThinkTime, MaxThinkTime)


  val RespondentNewClaimReply =

  /*======================================================================================
  Select reply to new claim
  ==========================================================================================*/

    exec(http("ET_CTZ_035_ReplyToNewClaim")
      .get(baseUrlET + "/self-assignment-form?lng=en")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(status.is(200))
      .check(substring("Case Details"))
    )
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentSelfAssignment =

  /*======================================================================================
  Self Assignment --> Enter details and Continue
  ==========================================================================================*/

   group("ET_CTZ_040_SelfAssignment") {
      exec(http("ET_CTZ_040_005_SelfAssignment")
        .post(baseUrlET + "/self-assignment-form")
        .headers(CitUICommonHeader)
        .header("Origin", "https://et-syr.perftest.platform.hmcts.net")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("id", "#{caseId}")
        .formParam("respondentName", "#{respondentName}")
        .formParam("firstName", "#{applicantFirstName}") 
        .formParam("lastName", "#{applicantLastName}") 
        .formParam("hiddenErrorField", "")
        //.check(CsrfCheck.save)
        .check(substring("Check and submit"))
        //.check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Self Assignment --> Details Correct --> Yes, Continue
  ==========================================================================================*/

  .group("ET_CTZ_050_SelfAssignmentCheck") {
      exec(http("ET_CTZ_050_005_SelfAssignmentCheck")
        .post(baseUrlET + "/self-assignment-check")
        //.headers(CitUICommonHeader)
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .header("Accept-Encoding", "gzip, deflate, br, zstd")
        .header("Accept-Language", "en-GB,en;q=0.9")
        .header("Priority", "u=0, i")
        .header("Referer", "https://et-syr.perftest.platform.hmcts.net/")
        .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
        .header("Cec-Ch-Ua-Mobile", "?0")
        .header("Sec-Fetch-Dest", "document")
        .header("Sec-Fetch-Mode", "navigate")
        .header("Sec-Fetch-Site", "same-origin")
        .header("Sec-Fetch-User", "?1")
        .header("Upgrade-Insecure-Requests", "1")
        .header("Origin", "https://et-syr.perftest.platform.hmcts.net")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("selfAssignmentCheck", "Yes")
        //.formParam("hiddenErrorField", "")
        //.check(CsrfCheck.save)
        .check(regex("""<td class="govuk-table__cell"> <a href="/case-details/#{caseId}/([a-z0-9\-]{36})\?lng=en"""").optional.saveAs("caseDetailID"))
        .check(substring("ET3 Responses"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

//======================================================================
//If the newly linked case is not yet visible, refresh the case list. 
//======================================================================

  .asLongAs(session => session("caseDetailID").asOption[String].isEmpty) {
      exec(http("ET_CTZ_055_RefreshCaseList")
        .get(baseUrlET + "/case-list")
        .headers(CitUICommonHeader)
        .check(CsrfCheck.save)
        .check(status.is(200))
        .check(regex("""<td class="govuk-table__cell"> <a href="/case-details/#{caseId}/([a-z0-9\-]{36})\?lng=en"""").optional.saveAs("caseDetailID"))
      )
  .pause(MinThinkTime, MaxThinkTime)
  }

  val RespondentET3 =

  /*======================================================================================
  ET3 Responses --> Select Claim --> Continue Link
  ==========================================================================================*/

      exec(http("ET_CTZ_070_ET3ResponseContinue")
        .get(baseUrlET + "/case-details/#{caseId}/#{caseDetailID}?lng=en")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .header("Accept-Encoding", "gzip, deflate, br, zstd")
        .header("Accept-Language", "en-GB,en;q=0.9")
        .header("Priority", "u=0, i")
        .header("Referer", "https://et-syr.perftest.platform.hmcts.net/")
        .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
        .header("Cec-Ch-Ua-Mobile", "?0")
        .header("Sec-Fetch-Dest", "document")
        .header("Sec-Fetch-Mode", "navigate")
        .header("Sec-Fetch-Site", "same-origin")
        .header("Sec-Fetch-User", "?1")
        .header("Upgrade-Insecure-Requests", "1")
        //.headers(CitUICommonHeader)
        .check(substring("Case overview"))
        //.check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Case Overview --> Select claimants ET1 Claim Form --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_080_SelectET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Claimants ET1 --> Select ET1 Form --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_090_OpenET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form-details")
        .headers(CitUICommonHeader)
        .check(substring("ET1 form"))
        .check(regex("<a href=getCaseDocument\\/([a-z0-9\\-]{36})").saveAs("et1DocId"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET1 Form --> Select ET1 Form PDF --> Click Link (getCaseDocument)
  ==========================================================================================*/

      .exec(http("ET_CTZ_010_ET1FormOpenPDF")
        .get(baseUrlET + "/getCaseDocument/#{et1DocId}")
        .headers(CitUICommonHeader)
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET1 Form Details --> Back --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_110_BackToET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET1 Form Details --> Acas Certificate --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_120_SelectACASCertificate")
        .get(baseUrlET + "/claimant-acas-certificate-details")
        .headers(CitUICommonHeader)
        .check(substring("Acas certificate"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ACAS Certificate --> Select Acas PDF --> Click Link (Doesnt Work Currently)
  ==========================================================================================*/
/*
  .group("ET_CTZ_130_ET1FormOpenPDF") {
      exec(http("ET_CTZ_130_005_ET1FormOpenPDF")
        .get(baseUrl + "")
        .headers(CitUICommonHeader)
        .check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)
*/
  /*======================================================================================
  ACAS Cert Details --> Back --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_140_BackToET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET 1 Form --> Back --> Click Link
  ==========================================================================================*/

     .exec(http("ET_CTZ_150_BackToCaseDetails")
        .get(baseUrlET + "/case-details/#{caseId}/#{caseDetailID}?lng=en")
        .headers(CitUICommonHeader)
        //.check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Case Overview --> Your Response Form ET3 --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_160_ET3YourResponseForm")
        .get(baseUrlET + "/respondent-response-landing")
        .headers(CitUICommonHeader)
        .check(substring("Response to ET1 employment tribunal claim"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Landing --> Select Start Now --> Click Button
  ==========================================================================================*/

      .exec(http("ET_CTZ_170_ET3ResponseStartNow")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Select Contact Details --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_180_ContactDetails")
        .get(baseUrlET + "/respondent-name")
        .headers(CitUICommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Respondent name"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Respondent Name --> Is name correct ?  --> Select Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_190_ConfirmRespondentName") {
      exec(http("ET_CTZ_190_005_ConfirmRespondentName")
        .post(baseUrlET + "/respondent-name")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("responseRespondentNameQuestion", "Yes")
        .formParam("responseRespondentName", "")
        .check(CsrfCheck.save)
        .check(substring("What type of organisation is"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> What type of organisation ?  --> Select individual, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_200_TypeOfOrganisation") {
      exec(http("ET_CTZ_200_005_TypeOfOrganisation")
        .post(baseUrlET + "/type-of-organisation")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseRespondentEmployerType", "Individual")
        .formParam("et3ResponseRespondentPreferredTitle", "#{ETCZRandomString}")
        .formParam("et3ResponseRespondentCompanyNumber", "")
        .check(CsrfCheck.save)
        .check(substring("Respondent address"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> Respondent Address ?  --> Select Yes, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_210_Respondentddress") {
      exec(http("ET_CTZ_210_005_Respondentddress")
        .post(baseUrlET + "/respondent-address")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3IsRespondentAddressCorrect", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Name of contact"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> Name of preferred contact ?  --> Leave Blank, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_220_NameOfPreferredContact") {
      exec(http("ET_CTZ_220_005_NameOfPreferredContact")
        .post(baseUrlET + "/respondent-preferred-contact-name")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseRespondentContactName", "")
        .check(CsrfCheck.save)
        .check(substring("DX address"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> DX Address ?  --> Leave Blank, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_230_DXAddress") {
      exec(http("ET_CTZ_230_005_DXAddress")
        .post(baseUrlET + "/respondent-dx-address")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseDXAddress", "")
        .check(CsrfCheck.save)
        .check(substring("What is your contact"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> What is your contact number ?  --> Rand num, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_240_ContactNumber") {
      exec(http("ET_CTZ_240_005_ContactNumber")
        .post(baseUrlET + "/respondent-contact-phone-number")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("responseRespondentPhone1", "0#{ETCZRandomNumber}")
        .check(CsrfCheck.save)
        .check(substring("Respondent contact preferences"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> Respondent contact preferences ?  --> Email,English Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_250_ContactNumber") {
      exec(http("ET_CTZ_250_005_ContactPreferences")
        .post(baseUrlET + "/respondent-contact-preferences")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("responseRespondentContactPreference", "Email")
        .formParam("et3ResponseContactReason", "")
        .formParam("et3ResponseLanguagePreference", "English")
        .check(CsrfCheck.save)
        .check(substring("Check your answers"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
  Check your answers --> Have you completed this section  --> Yes, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_260_CheckAnswersContactDetails") {
      exec(http("ET_CTZ_260_005_CheckAnswersContactDetails")
        .post(baseUrlET + "/check-your-answers-contact-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("personalDetailsSection", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Hearing format"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Tell us about the respondent  --> Would you be able to take part in hearings by video and phone?  --> Yes,Yes Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_270_HearingPreferences") {
      exec(http("ET_CTZ_270_005_HearingPreferences")
        .post(baseUrlET + "/hearing-preferences")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseHearingRespondent", "Video hearings")
        .formParam("et3ResponseHearingRespondent", "Phone hearings")
        .check(CsrfCheck.save)
        .check(substring("Extra support during"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Tell us about the respondent  --> Extra support during the  -->No, I do not need & continue
  ==========================================================================================*/
  
  .group("ET_CTZ_280_ReasonableAdjustments") {
      exec(http("ET_CTZ_280_005_ReasonableAdjustments")
        .post(baseUrlET + "/reasonable-adjustments")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseRespondentSupportDetails", "")
        .formParam("et3ResponseRespondentSupportNeeded", "No")
        .check(CsrfCheck.save)
        .check(substring("Respondent employees"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Respondent employees  --> How many people does the respondent employ in Great Britain?  --> Blank & continue
  ==========================================================================================*/

  .group("ET_CTZ_290_RespondentEmployees") {
      exec(http("ET_CTZ_290_005_RespondentEmployees")
        .post(baseUrlET + "/respondent-employees")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseEmploymentCount", "")
        .check(CsrfCheck.save)
        .check(substring("more than one site"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Respondent Site  --> Does the respondent have more than one site  --> Yes & continue
  ==========================================================================================*/

  .group("ET_CTZ_300_RespondentSites") {
      exec(http("ET_CTZ_300_005_RespondentSites")
        .post(baseUrlET + "/respondent-sites")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseMultipleSites", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Respondent employees"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Respondent Site Employees  --> How many people are employed at the site  --> Blank & continue
  ==========================================================================================*/

  .group("ET_CTZ_310_RespondentSiteEmployees") {
      exec(http("ET_CTZ_310_005_RespondentSiteEmployees")
        .post(baseUrlET + "/respondent-site-employees")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseSiteEmploymentCount", "")
        .check(CsrfCheck.save)
        .check(substring("Check your answers"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Check your answers  --> Hearing format and employer details  --> Yes, I’ve completed this section & continue
  ==========================================================================================*/

  .group("ET_CTZ_320_CheckAnswersHearingPreferences") {
      exec(http("ET_CTZ_320_005_CheckAnswersHearingPreferences")
        .post(baseUrlET + "/check-your-answers-hearing-preferences")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("hearingPreferencesSection", "Yes")
        .check(substring("Check your answers"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentET3ClaimantInfo =

  /*======================================================================================
  Response Task List
  ==========================================================================================*/

      exec(http("ET_CTZ_330_ET3ResponseTaskList")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Acas early conciliation certificate, Click Link
  ==========================================================================================*/
    
      .exec(http("ET_CTZ_340_ACASEarlyConciliationStart")
        .get(baseUrlET + "/acas-early-conciliation-certificate")
        .headers(CitUICommonHeader)
        .check(substring("Acas early conciliation certificate"))
        .check(status.is(200))
        .check(CsrfCheck.save)
      )
   .pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
  Tell us about the claimaint --> Acas early conciliation certificate, Do you disagree --> No, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_350_ACASEarlyConciliation") {
      exec(http("ET_CTZ_350_005_ACASEarlyConciliation")
        .post(baseUrlET + "/acas-early-conciliation-certificate")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseAcasAgree", "No")
        .formParam("et3ResponseAcasAgreeReason", "Perf test disagreement for the Acas conciliation details given.")
        .check(substring("Are the dates of employment"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Are the dates of employment given by the claimant correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_360_ClaimantEmploymentDates") {
      exec(http("ET_CTZ_360_005_ClaimantEmploymentDates")
        .post(baseUrlET + "/claimant-employment-dates")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseAreDatesCorrect", "Yes")
        .check(substring("Is the claimant’s employment"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Is the claimant’s employment with the respondent continuing --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_370_ClaimantContinuedEmployment") {
      exec(http("ET_CTZ_370_005_ClaimantContinuedEmployment")
        .post(baseUrlET + "/is-claimant-employment-with-respondent-continuing")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseContinuingEmployment", "Yes")
        .check(substring("Claimant job title"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Is the claimant’s job title correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_380_ClaimantJobTitle") {
      exec(http("ET_CTZ_380_005_ClaimantJobTitle")
        .post(baseUrlET + "/claimant-job-title")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseIsJobTitleCorrect", "Yes")
        .formParam("et3ResponseCorrectJobTitle", "")
        .check(substring("Claimant average weekly"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Are the claimant’s average weekly work hours correct?  --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_390_ClaimantWeeklyHours") {
      exec(http("ET_CTZ_390_005_ClaimantWeeklyHours")
        .post(baseUrlET + "/claimant-average-weekly-work-hours")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseClaimantWeeklyHours", "Yes")
        .formParam("et3ResponseClaimantCorrectHours", "")
        .check(substring("Check your answers"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Check Answers --> Have you completed this section? --> Yes, Save & Continue  
  ==========================================================================================*/

 .group("ET_CTZ_400_CheckYourAnswers") {
      exec(http("ET_CTZ_400_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-early-conciliation-and-employee-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("conciliationAndEmployeeDetailsSection", "Yes")
        .check(substring("Claimant pay details"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the pay details given by the claimant correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_410_ClaimantPayDetails") {
      exec(http("ET_CTZ_410_005_ClaimantPayDetails")
        .post(baseUrlET + "/claimant-pay-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseEarningDetailsCorrect", "Yes")
        .check(substring("Claimant notice period"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the claimant’s notice period details correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_420_ClaimantNoticePeriod") {
      exec(http("ET_CTZ_420_005_ClaimantNoticePeriod")
        .post(baseUrlET + "/claimant-notice-period")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseIsNoticeCorrect", "Yes")
        .formParam("et3ResponseCorrectNoticeDetails", "")
        .check(substring("Claimant pension and benefits"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the claimant’s pension and benefits details correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_430_ClaimantPensionBenefits") {
      exec(http("ET_CTZ_430_005_ClaimantPensionBenefits")
        .post(baseUrlET + "/claimant-pension-and-benefits")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseIsPensionCorrect", "Yes")
        .formParam("et3ResponsePensionCorrectDetails", "")
        .check(substring("Check your answers"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Check your answers --> Have you completed this section? --> Yes, Save & Continue
  ==========================================================================================*/
  .group("ET_CTZ_440_CheckYourAnswers") {
      exec(http("ET_CTZ_440_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-pay-pension-and-benefits")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("pensionAndBenefitsSection", "Yes")
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

val RespondentET3ContestTheClaim =

  /*======================================================================================
  Response Task List
  ==========================================================================================*/

      exec(http("ET_CTZ_450_ET3ResponseTaskList")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Select Contest the claim --> Click Link
  ==========================================================================================*/

      .exec(http("ET_CTZ_460_ContestTheClaimStart")
        .get(baseUrlET + "/respondent-contest-claim")
        .headers(CitUICommonHeader)
        .check(substring("contest the claim"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Give us your response --> Does Respondent contest the claim? --> Yes, save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_470_ContestTheClaim") {
      exec(http("ET_CTZ_470_005_ContestTheClaim")
        .post(baseUrlET + "/respondent-contest-claim")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseRespondentContestClaim", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Contest the claim"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Give us your response --> Explain why Respondent contests the claim --> Enter free text, save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_480_ContestTheClaimReason") {
      exec(http("ET_CTZ_480_005_ContestTheClaimReason")
        .post(baseUrlET + "/respondent-contest-claim-reason??_csrf=#{csrf}")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("et3ResponseContestClaimDetails", "Performance test respondent is not liable for the claim because it is full of lies")
        .formParam("contestClaimDocument", "(binary)")
        .formParam("submit", "true")
        .check(CsrfCheck.save)
        .check(substring("Check your answers"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Check your answers  --> Have you completed this section?  --> Yes, save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_490_CheckYourAnswers") {
      exec(http("ET_CTZ_490_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-contest-claim")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("contestClaimSection", "Yes")
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

val RespondentET3CheckYourAnswers =

  /*======================================================================================
  Response Task List
  ==========================================================================================*/

      exec(http("ET_CTZ_500_ET3ResponseTaskList")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Select Check your answers link
  ==========================================================================================*/

      .exec(http("ET_CTZ_510_ET3CheckYourAnswers")
        .get(baseUrlET + "/check-your-answers-et3")
        .headers(CitUICommonHeader)
        .check(substring("Check your answers"))
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Submit ET3 Form
  ==========================================================================================*/

  .group("ET_CTZ_520_SubmitET3Application") {
    exec(http("ET_CTZ_520_005_SubmitET3Application")
      .post(baseUrlET + "/check-your-answers-et3")
      .headers(CitUICommonHeader)
      .formParam("_csrf", "#{csrf}")
      .formParam("submit", "true")
      .check(substring("Your response has been submitted"))
      .check(status.is(200))
    )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Select close and return to case overview
  ==========================================================================================*/

      .exec(http("ET_CTZ_530_ET3ResponseContinue")
        .get(baseUrlET + "/case-details/#{caseId}/#{caseDetailID}?lng=en")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .header("Accept-Encoding", "gzip, deflate, br, zstd")
        .header("Accept-Language", "en-GB,en;q=0.9")
        .header("Priority", "u=0, i")
        .header("Referer", "https://et-syr.perftest.platform.hmcts.net/")
        .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
        .header("Cec-Ch-Ua-Mobile", "?0")
        .header("Sec-Fetch-Dest", "document")
        .header("Sec-Fetch-Mode", "navigate")
        .header("Sec-Fetch-Site", "same-origin")
        .header("Sec-Fetch-User", "?1")
        .header("Upgrade-Insecure-Requests", "1")
        //.headers(CitUICommonHeader)
        .check(substring("Case overview"))
        .check(status.is(200))
    )
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentET3OpenCompletedForm =

  /*======================================================================================
  Select Your Response Form (ET3) Link
  ==========================================================================================*/

      exec(http("ET_CTZ_540_ET3ApplicationSubmitted")
        .get(baseUrlET + "/application-submitted")
        .headers(CitUICommonHeader)
        .check(regex("<a href=getCaseDocument\\/([a-z0-9\\-]{36})").saveAs("et3DocId"))
        .check(substring("Your response has been submitted"))
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Select the ET3 form link to download document
  ==========================================================================================*/

      .exec(http("ET_CTZ_550_ET3GetCaseDocument")
        .get(baseUrlET + "/getCaseDocument/#{et3DocId}")
        .headers(CitUICommonHeader)
        .check(status.is(200))
      )
   .pause(MinThinkTime, MaxThinkTime)

  /*
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("ETcasesold.csv", true))
      try {
        fw.write(session("caseId").as[String] + "\r\n")
      } finally fw.close()
      session
    } */


}
