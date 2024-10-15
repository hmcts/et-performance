package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers, CsrfCheck}

import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._

object ET_Citizen {

  val IdamURL = Environment.idamURL
  //val baseUrl = Environment.baseURLETUIResp
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

  .group("ET_CTZ_010_LandingPage") {
    exec(http("ET_CTZ_010_005_LandingPage")
      .get(baseUrlET + "/")
      .headers(CitUILandingHeader)
      //.check(CsrfCheck.save)
      //.check(regex("/oauth2/c&amp;state=(.*)&amp;nonce=").saveAs("state"))
      //.check(substring("Employment"))
      .check(status.is(200))
    )
  }
  .pause(MinThinkTime, MaxThinkTime)

  /*=========================================================================================
  ET3 Start Now
  ==========================================================================================*/

  .group("ET_CTZ_020_StartNow") {
    exec(http("ET_CTZ_020_005_StartNow")
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
    exec(http("ET_CTZ_030_005_InterruptionCardContinue")
      .get(baseUrlET + "/case-list-check")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(regex("/oauth2/callback&amp;state=(.*)&amp;ui_locales=").saveAs("state"))
      .check(substring("Sign in"))
      .check(status.is(200))
    )
  }
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentNewClaimReply =

  /*======================================================================================
  Select reply to new claim
  ==========================================================================================*/

  group("ET_CTZ_035_ReplyToNewClaim") {
    exec(http("ET_CTZ_035_005_ReplyToNewClaim")
      .get(baseUrlET + "/new-self-assignment-request?lng=en")
      .headers(CitUICommonHeader)
      .check(CsrfCheck.save)
      .check(status.is(200))
      .check(substring("Self Assignment - Form"))
      //.check(jsonPath("$.event_token").optional.saveAs("event_token"))
    )
  }
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
        //.formParam("hiddenErrorField", "")
        .check(CsrfCheck.save)
        .check(substring("Case assignment"))
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
        .check(substring("ET3 Responses"))
        .check(status.is(200))
      
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  val RespondentET3 =

  /*======================================================================================
  ET3 Responses --> Select Claim --> Continue Link
  ==========================================================================================*/

 // group("ET_CTZ_060_ET3ResponseCaseList") {
 //     exec(http("ET_CTZ_060_005_ET3ResponseCaseList")
 //       .get(baseUrlET + "/case-list")
 //       .headers(CitUICommonHeader)
 //       .check(substring("ET3 Responses"))
 //       .check(status.is(200))
 //   )
 // } 

  group("ET_CTZ_070_ET3ResponseContinue") {
      exec(http("ET_CTZ_070_005_ET3ResponseContinue")
        .get(baseUrlET + "/case-details/#{caseId}?lng=en")
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
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Case Overview --> Select claimants ET1 Claim Form --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_080_SelectET1ClaimForm") {
      exec(http("ET_CTZ_080_005_SelectET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Claimants ET1 --> Select ET1 Form --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_090_OpenET1ClaimForm") {
      exec(http("ET_CTZ_090_005_OpenET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form-details")
        .headers(CitUICommonHeader)
        .check(substring("ET1 form"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET1 Form --> Select ET1 Form PDF --> Click Link (Doesnt Work Currently)
  ==========================================================================================*/
/*
  .group("ET_CTZ_010_ET1FormOpenPDF") {
      exec(http("ET_CTZ_010_005_ET1FormOpenPDF")
        .get(baseUrl + "")
        .headers(CitUICommonHeader)
        .check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)
*/
  /*======================================================================================
  ET1 Form Details --> Back --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_110_BackToET1ClaimForm") {
      exec(http("ET_CTZ_110_005_BackToET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET1 Form Details --> Acas Certificate --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_120_SelectACASCertificate") {
      exec(http("ET_CTZ_120_005_SelectACASCertificate")
        .get(baseUrlET + "/claimant-acas-certificate-details")
        .headers(CitUICommonHeader)
        .check(substring("Acas certificate"))
        .check(status.is(200))
    )
  } 
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

  .group("ET_CTZ_140_BackToET1ClaimForm") {
      exec(http("ET_CTZ_140_005_BackToET1ClaimForm")
        .get(baseUrlET + "/claimant-et1-form")
        .headers(CitUICommonHeader)
        .check(substring("ET1 claim form"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  ET 1 Form --> Back --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_150_BackToCaseDetails") {
      exec(http("ET_CTZ_150_005_BackToCaseDetails")
        .get(baseUrlET + "/case-details/#{caseId}?lng=en")
        .headers(CitUICommonHeader)
        //.check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Case Overview --> Your Response Form ET3 --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_160_ET3YourResponseForm") {
      exec(http("ET_CTZ_160_005_ET3YourResponseForm")
        .get(baseUrlET + "/respondent-response-landing")
        .headers(CitUICommonHeader)
        .check(substring("Response to ET1 employment tribunal claim"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Landing --> Select Start Now --> Click Button
  ==========================================================================================*/

  .group("ET_CTZ_170_ET3ResponseStartNow") {
      exec(http("ET_CTZ_170_005_ET3ResponseStartNow")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Select Contact Details --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_180_ContactDetails") {
      exec(http("ET_CTZ_180_005_ContactDetails")
        .get(baseUrlET + "/respondent-name")
        .headers(CitUICommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Respondent name"))
        .check(status.is(200))
    )
  } 
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
        .formParam("respondentAddress", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Name of preferred contact"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the respondent --> Name of preffered contact ?  --> Leave Blank, Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_220_NameOfPreferredContact") {
      exec(http("ET_CTZ_220_005_NameOfPreferredContact")
        .post(baseUrlET + "/respondent-preferred-contact-name")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("respondentPreferredContactName", "")
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
        .formParam("respondentDxAddress", "")
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
        .formParam("respondentContactPhoneNumber", "0#{ETCZRandomNumber}")
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
        .formParam("respondentContactPreference", "Email")
        .formParam("respondentContactPreferenceDetail", "")
        .formParam("respondentLanguagePreference", "English")
        .check(CsrfCheck.save)
        .check(substring("Check your answers"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
  Check your answers --> Have you completed this section  --> Yes, Save & Continue ** Currently not working --> Save as DRAFT workaround
  ==========================================================================================*/
  
  .group("ET_CTZ_260_CheckAnswersContactDetails") {
      exec(http("ET_CTZ_260_CheckAnswersContactDetails")
        .post(baseUrlET + "/check-your-answers-contact-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("personalDetailsSection", "Yes")
        .formParam("saveForLater", "true")
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
        .formParam("hearingPreferences", "Video")
        .formParam("hearingPreferences", "Phone")
        .formParam("hearingAssistance", "")
        .check(CsrfCheck.save)
        .check(substring("Extra support during"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Tell us about the respondent  --> Extra support during the  --> Yes,Yes Save & Continue
  ==========================================================================================*/
  
  .group("ET_CTZ_270_HearingPreferences") {
      exec(http("ET_CTZ_270_005_HearingPreferences")
        .post(baseUrlET + "/hearing-preferences")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("hearingPreferences", "Video")
        .formParam("hearingPreferences", "Phone")
        .formParam("hearingAssistance", "")
        .check(CsrfCheck.save)
        //.check(substring("Respondent name"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   Tell us about the respondent  --> Extra Support During the Case  --> No I do not need, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_280_ReasonableAdjustments") {
      exec(http("ET_CTZ_280_005_ReasonableAdjustments")
        .post(baseUrlET + "/reasonable-adjustments")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("reasonableAdjustmentsDetail", "")
        .formParam("reasonableAdjustments", "No")
        //.check(substring("Respondent name"))
        .check(status.is(200))
    )
  } 
  .pause(MinThinkTime, MaxThinkTime)

  // *** UNABLE TO PROCEED BEYOND THIS POINT FOR RESPONDENT DETAILS ***//

  val RespondentET3ClaimantInfo =

  /*======================================================================================
  Response Task List
  ==========================================================================================*/

  group("ET_CTZ_340_ET3ResponseTaskList") {
      exec(http("ET_CTZ_340_005_ET3ResponseTaskList")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Acas early conciliation certificate, Click Link
  ==========================================================================================*/
    
  .group("ET_CTZ_350_ACASEarlyConciliationStart") {
      exec(http("ET_CTZ_350_005_ACASEarlyConciliationStart")
        .get(baseUrlET + "/acas-early-conciliation-certificate")
        .headers(CitUICommonHeader)
        .check(substring("Acas early conciliation certificate"))
        .check(status.is(200))
        .check(CsrfCheck.save)
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
  Tell us about the claimaint --> Acas early conciliation certificate, Do you disagree --> No, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_360_ACASEarlyConciliation") {
      exec(http("ET_CTZ_360_005_ACASEarlyConciliation")
        .post(baseUrlET + "/acas-early-conciliation-certificate")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("doYouDisagreeAboutAcas", "No")
        .formParam("whyDoYouDisagreeAcas", "")
        .check(substring("Are the dates of employment"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Are the dates of employment given by the claimant correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_370_ClaimantEmploymentDates") {
      exec(http("ET_CTZ_370_005_ClaimantEmploymentDates")
        .post(baseUrlET + "/claimant-employment-dates")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("areDatesOfEmploymentCorrect", "Yes")
        .check(substring("Is the claimant’s employment"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Is the claimant’s employment with the respondent continuing --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_380_ClaimantContinuedEmployment") {
      exec(http("ET_CTZ_380_005_ClaimantContinuedEmployment")
        .post(baseUrlET + "/is-claimant-employment-with-respondent-continuing")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isEmploymentContinuing", "Yes")
        .check(substring("Claimant job title"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Is the claimant’s job title correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_390_ClaimantJobTitle") {
      exec(http("ET_CTZ_390_005_ClaimantJobTitle")
        .post(baseUrlET + "/claimant-job-title")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isClaimantJobTitleCorrect", "Yes")
        .formParam("whatIsClaimantJobTitle", "")
        .check(substring("Claimant average weekly"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimaint --> Are the claimant’s average weekly work hours correct?  --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_400_ClaimantWeeklyHours") {
      exec(http("ET_CTZ_400_005_ClaimantWeeklyHours")
        .post(baseUrlET + "/claimant-average-weekly-work-hours")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("areClaimantWorkHourCorrect", "Yes")
        .formParam("whatAreClaimantCorrectWorkHour", "")
        .check(substring("Check your answers"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Check Answers --> Have you completed this section? --> Yes, Save & Continue  ** Not saving here --> Selected DRAFT SAVE FOR LATER
  ==========================================================================================*/

 .group("ET_CTZ_410_CheckYourAnswers") {
      exec(http("ET_CTZ_410_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-early-conciliation-and-employee-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("haveYouCompleted", "Yes")
        .formParam("saveForLater", "true")
        .check(substring("Claimant pay details"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the pay details given by the claimant correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_420_ClaimantPayDetails") {
      exec(http("ET_CTZ_420_005_ClaimantPayDetails")
        .post(baseUrlET + "/claimant-pay-details")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("arePayDetailsGivenCorrect", "Yes")
        .check(substring("Claimant notice period"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the claimant’s notice period details correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_430_ClaimantNoticePeriod") {
      exec(http("ET_CTZ_430_005_ClaimantNoticePeriod")
        .post(baseUrlET + "/claimant-notice-period")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("areClaimantNoticePeriodDetailsCorrect", "Yes")
        .formParam("whatAreClaimantCorrectNoticeDetails", "")
        .check(substring("Claimant pension and benefits"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Tell us about the claimant --> Are the claimant’s pension and benefits details correct? --> Yes, Save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_440_ClaimantPensionBenefits") {
      exec(http("ET_CTZ_440_005_ClaimantPensionBenefits")
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
  Check your answers --> Have you completed this section? --> Yes, Save & Continue   ** Save as draft as not currently working
  ==========================================================================================*/
  .group("ET_CTZ_450_CheckYourAnswers") {
      exec(http("ET_CTZ_450_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-pay-pension-and-benefits")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("pensionAndBenefitsSection", "Yes")
        .formParam("saveForLater", "true")
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

val RespondentET3ContestTheClaim =

  /*======================================================================================
  Response Task List
  ==========================================================================================*/

  group("ET_CTZ_460_ET3ResponseTaskList") {
      exec(http("ET_CTZ_460_005_ET3ResponseTaskList")
        .get(baseUrlET + "/respondent-response-task-list")
        .headers(CitUICommonHeader)
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Response Task List --> Select Contest the claim --> Click Link
  ==========================================================================================*/

  .group("ET_CTZ_470_ContestTheClaimStart") {
      exec(http("ET_CTZ_470_005_ContestTheClaimStart")
        .get(baseUrlET + "/respondent-contest-claim")
        .headers(CitUICommonHeader)
        .check(substring("contest the claim"))
        .check(CsrfCheck.save)
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Give us your response --> Does Respondent contest the claim? --> Yes, save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_480_ContestTheClaim") {
      exec(http("ET_CTZ_480_005_ContestTheClaim")
        .post(baseUrlET + "/respondent-contest-claim")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("respondentContestClaim", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Contest the claim"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Give us your response --> Explain why Respondent contests the claim --> Enter free text, save & Continue
  ==========================================================================================*/

  .group("ET_CTZ_490_ContestTheClaimReason") {
      exec(http("ET_CTZ_490_005_ContestTheClaimReason")
        .post(baseUrlET + "/respondent-contest-claim-reason")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("respondentContestClaimReason", "Performance test respondent is not liable for the claim because it is full of lies and blasphemy")
        .formParam("supportingMaterialFile", "")
        .check(substring("Check your answers"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  Check your answers  --> Have you completed this section?  --> Yes, save & Continue   ** Safe as draft currently as not working
  ==========================================================================================*/

  .group("ET_CTZ_500_CheckYourAnswers") {
      exec(http("ET_CTZ_500_005_CheckYourAnswers")
        .post(baseUrlET + "/check-your-answers-contest-claim")
        .headers(CitUICommonHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("contestClaimSection", "Yes")
        .formParam("saveForLater", "true")
        .check(substring("Your response form (ET3)"))
        .check(status.is(200))
      )
  }
   .pause(MinThinkTime, MaxThinkTime)



/*
    /*======================================================================================
    Create Civil Claim - Initiate case 3 - Respondent
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase3") {
      exec(http("ET_CW_040_StartCreateCase3")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase3")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase3.json"))
        .check(substring("Respondent"))
        .check(status.is(200))
        .check(jsonPath("$..id").optional.saveAs("event_id"))
        
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  

    /*======================================================================================
                   * Create Civil Claim - Initiate case 4 - Is address same for both
    ==========================================================================================*/
    .group("ET_CW_040_StartCreateCase4") {
      exec(http("ET_CW_040_StartCreateCase4")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase4")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase4.json"))
        .check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200))
        
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  
    /*======================================================================================
                   * Create Civil Claim - Initiate case 7 -claimant occupation details
    ==========================================================================================*/
    .group("ET_CW_040_StartCreateCase4") {
      exec(http("ET_CW_040_StartCreateCase7")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase7")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase7.json"))
        .check(substring("claimant_occupation"))
        .check(status.is(200))
    
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  
    /*======================================================================================
                   * Create Civil Claim - Initiate case 8 -Is claimant represented by any one
    ==========================================================================================*/
    .group("ET_CW_040_StartCreateCase8") {
      exec(http("ET_CW_040_StartCreateCase8")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase8")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase8.json"))
        .check(substring("claimantRepresentedQuestion"))
        .check(status.is(200))
    
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  
    /*======================================================================================
                   * Create Civil Claim - Initiate case 9 -Is claimant represented by any one
    ==========================================================================================*/
    .group("ET_CW_040_StartCreateCase9") {
      exec(http("ET_CW_040_StartCreateCase9")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase9")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase9.json"))
        .check(substring("hearing_assistance"))
        .check(status.is(200))
      )
        .exec(http("Civil_CreateClaim_005_Submit")
          .post("/data/case-types/ET_EnglandWales/cases?ignore-warning=false")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/createcase/caseSubmit.json"))
          .check(substring("CALLBACK_COMPLETED"))
          .check(status.is(201))
          .check(jsonPath("$.id").optional.saveAs("caseId"))
        )
  
        .exec(http("Civil_CreateClaim_010_Submit")
          .get("/data/internal/cases/#{caseId}")
          .headers(CommonHeader)
          .check(substring("ET_EnglandWales"))
          .check(status.is(200))
        )
        
        .pause(MinThinkTime, MaxThinkTime)
    }
  
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("ETcasesold.csv", true))
      try {
        fw.write(session("caseId").as[String] + "\r\n")
      } finally fw.close()
      session
    } */


}
