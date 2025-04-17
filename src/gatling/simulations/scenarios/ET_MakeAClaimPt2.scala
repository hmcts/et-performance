//do a check to see if the section is complete
package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment}
import java.io.{BufferedWriter, FileWriter}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.duration._
import scala.util.Random


object ET_MakeAClaimPt2 {

  val BaseURL = Environment.baseURL
  val baseURLETUIApp = Environment.baseURLETUIApp
  val IdamURL = Environment.idamURL

  val now = LocalDate.now()
  val patternDay = DateTimeFormatter.ofPattern("dd")
  val patternMonth = DateTimeFormatter.ofPattern("MM")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular
  
  val rnd = new Random()

  val MakeAClaim =

    exec(_.setAll(
      "ETRandomString" -> (Common.randomString(7)),
      "ETDobDay" -> Common.getDay(),
      "ETDobMonth" -> Common.getMonth(),
      "payBeforeTax" -> now.plusYears(5 + rnd.nextInt(15)).format(patternYear),
      "acasCertNum" -> ("R807115/23/89"),
      "payAfterTax" -> now.plusYears(2 + rnd.nextInt(3)).format(patternYear)))

    /*===============================================================================================
    * Employment Status link click
    ===============================================================================================*/

    .group("ET_200_Employment_Status") {
      exec(http("ET_200_005_Employment_Status")
        .get(baseURLETUIApp + "/past-employer")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Did you work for the organisation or person you’re making your claim against")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Did you work for the organisation or person you’re making your claim against? - yes
    ===============================================================================================*/

    .group("ET_210_Work_For_Org") {
      exec(http("ET_210_005_Work_For_Organisation")
        .post(baseURLETUIApp + "/past-employer")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("pastEmployer", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Are you still working for the organisation or person")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Are you still working for the organisation or person you're making your claim against? - I'm working a notice period for the respondent
    ===============================================================================================*/

    .group("ET_220_Still_Work_For_Org") {
      exec(http("ET_220_005_Still_Work_For_Org")
        .post(baseURLETUIApp + "/are-you-still-working")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("isStillWorking", "Notice")
        .check(CsrfCheck.save)
        .check(substring("Employment details")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Employment details
    ===============================================================================================*/

    .group("ET_230_Employment_Details") {
      exec(http("ET_230_005_Employment_Details")
        .post(baseURLETUIApp + "/job-title")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("jobTitle", "#{ETRandomString}" + "Job")
        .check(CsrfCheck.save)
        .check(substring("Employment start date")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Employment start date
    ===============================================================================================*/

    .group("ET_240_Start_Date") {
      exec(http("ET_240_005_Start_Date")
        .post(baseURLETUIApp + "/start-date")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("startDate-day", "#{ETDobDay}")
        .formParam("startDate-month", "#{ETDobMonth}")
        .formParam("startDate-year", Common.getJobStartDate())
        .check(CsrfCheck.save)
        .check(substring("When does your notice period end?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * When does your notice period end?
    ===============================================================================================*/

    .group("ET_250_Notice_End") {
      exec(http("ET_250_005_Notice_End")
        .post(baseURLETUIApp + "/notice-end")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("noticeEnds-day", "#{ETDobDay}")
        .formParam("noticeEnds-month", "#{ETDobMonth}")
        .formParam("noticeEnds-year", Common.getNoticeEndDate())
        .check(CsrfCheck.save)
        //has to be a future date
        .check(substring("Is your notice period in weeks or months?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Is your notice period in weeks or months? Months
    ===============================================================================================*/

    .group("ET_260_Weeks_Or_Months") {
      exec(http("ET_260_005_Weeks_Or_Months")
        .post(baseURLETUIApp + "/notice-type")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("noticePeriodUnit", "Months")
        .check(CsrfCheck.save)
        .check(substring("How many months in your notice period?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    
    /*===============================================================================================
    * How many months of your notice period are you being paid for?
    ===============================================================================================*/

    .group("ET_270_Number_Of_Months") {
      exec(http("ET_270_005_Number_Of_Months")
        .post(baseURLETUIApp + "/notice-length")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("noticePeriodLength", "#{ETDobDay}")
        .check(CsrfCheck.save)
        .check(substring("What are your average weekly hours?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What are your average weekly hours?
    ===============================================================================================*/

    .group("ET_280_Weekly_Hours") {
      exec(http("ET_280_005_Weekly_Hours")
        .post(baseURLETUIApp + "/average-weekly-hours")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("avgWeeklyHrs","1" + "#{ETDobDay}")
        .check(CsrfCheck.save)
        .check(substring("Your pay")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Your pay
    ===============================================================================================*/

    .group("ET_290_Your_Pay") {
      exec(http("ET_290_005_Your_Pay")
        .post(baseURLETUIApp + "/pay")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("payBeforeTax", "#{payBeforeTax}")
        .formParam("payAfterTax", "#{payAfterTax}")
        .formParam("payInterval", "Months")
        .check(CsrfCheck.save)
        .check(substring("Did the respondent make any contributions to your pension? ")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Did the respondent make any contributions to your pension? - Not sure
    ===============================================================================================*/

    .group("ET_300_Pension_Contributions") {
      exec(http("ET_300_005_Pension_Contributions")
        .post(baseURLETUIApp + "/pension")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimantPensionWeeklyContribution", "")
        .formParam("claimantPensionContribution", "Not Sure")
        .check(CsrfCheck.save)
        .check(substring("Do you or did you receive any employee benefits?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Do or did you receive any employee benefits? - No
    ===============================================================================================*/

    .group("ET_310_Employee_Benefits") {
      exec(http("ET_310_005_Employee_Benefits")
        .post(baseURLETUIApp + "/benefits")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("benefitsCharCount", "")
        .formParam("employeeBenefits", "No")
        .check(CsrfCheck.save)
        .check(substring("What is the name of the respondent")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What is the name of the respondent you're making the claim against?
    ===============================================================================================*/

    .group("ET_320_Respondent_Name") {
      exec(http("ET_320_005_Respondent_Name")
        .post(baseURLETUIApp + "/respondent/1/respondent-name")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("respondentName", "#{ETRandomString}" + "Respondent")
        .check(CsrfCheck.save)
        .check(substring("What is the address of")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What is the address of Respondent?
    ===============================================================================================*/

    .group("ET_330_Respondent_Address_LookUp") {
      exec(Common.postcodeLookup)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Respondent Address LookUp
    ===============================================================================================*/
    .feed(postcodeFeeder)

    .group("ET_335_Respondent_Address_LookUp") {
        exec(http("ET_335_005__Respondent_Address_LookUp")
        .post(baseURLETUIApp + "/respondent/1/respondent-address")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("respondentAddress1", "respondentAddress1" + "#{ETRandomString}")
        .formParam("respondentAddress2", "respondentAddress2" + "#{ETRandomString}")
        .formParam("respondentAddressTown", "respondentAddressTown" + "#{ETRandomString}")
        .formParam("respondentAddressCountry", "respondentAddressCountry" + "#{ETRandomString}")
        .formParam("respondentAddressPostcode", "#{postcode}")
        .check(CsrfCheck.save)
        .check(substring("Did you work at")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Did you work at address? - yes
    ===============================================================================================*/

    .group("ET_340_Work_At") {
      exec(http("ET_340_005_Work_At")
        .post(baseURLETUIApp + "/respondent/1/work-address")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimantWorkAddressQuestion", "Yes")
        .check(CsrfCheck.save)
        .check(substring("Do you have an Acas certificate number for")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Do you have an Acas certificate number for respondent? - Yes
    ===============================================================================================*/

    .group("ET_350_Respondent_Acas") {
      exec(http("ET_350_005_Respondent_Acas")
        .post(baseURLETUIApp + "/respondent/1/acas-cert-num")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("acasCert", "Yes")
        .formParam("acasCertNum", "#{acasCertNum}")
        .check(substring("Check the respondent details")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Check the respondent details
    ===============================================================================================*/

    .group("ET_360_Respondent_Check") {
      exec(http("ET_360_005_Respondent_Check")
        .get(baseURLETUIApp + "/employment-respondent-task-check")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Have you completed this section?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Employment/Respondent submit
    ===============================================================================================*/

    .group("ET_370_Respondent_Submit") {
      exec(http("ET_370_005_Respondent_Submit")
        .post(baseURLETUIApp + "/employment-respondent-task-check")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("employmentAndRespondentCheck", "Yes")
        .check(substring("Employment status\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Respondent details\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Steps to making your claim")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Describe what happened to you link
    ===============================================================================================*/

    .group("ET_380_Claim_Details") {
      exec(http("ET_380_005_Claim_Details")
        .get(baseURLETUIApp + "/claim-type-discrimination")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What type of discrimination are you claiming?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What type of discrimination are you claiming? - age, race and sex
    ===============================================================================================*/

    .group("ET_390_Type_Of_Descrimination") {
      exec(http("ET_390_005_Type_Of_Descrimination")
        .post(baseURLETUIApp + "/claim-type-discrimination")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimTypeDiscrimination", "Age")
        .formParam("claimTypeDiscrimination", "Race")
        .formParam("claimTypeDiscrimination", "Sex")
        .check(CsrfCheck.save)
        .check(substring("Describe what happened to you")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Describe what happened to you
    ===============================================================================================*/

    .group("ET_400_Describe_What_Happened") {
      exec(http("ET_400_005_Describe_What_Happened")
        .post(baseURLETUIApp + "/describe-what-happened?_csrf=#{csrf}")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimSummaryText", "#{ETRandomString}" + "Description")
        .formParam("claimSummaryFileName", "(binary)")
        .check(CsrfCheck.save)
        .check(substring("What do you want if your claim is successful?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What do you want if your claim is successful? - Compensation only
    ===============================================================================================*/

    .group("ET_410_Outcome_If_Successful") {
      exec(http("ET_410_005_Outcome_If_Successful")
        .post(baseURLETUIApp + "/tell-us-what-you-want")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("tellUsWhatYouWant", "compensation")
        .check(CsrfCheck.save)
        .check(substring("What compensation are you seeking?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * What compensation are you seeking?
    ===============================================================================================*/

    .group("ET_420_Compensation") {
      exec(http("ET_420_005_Compensation")
        .post(baseURLETUIApp + "/compensation")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("compensationOutcome", "#{ETRandomString}" + "Compensation")
        .formParam("compensationAmount", Common.getDobYear())
        .check(CsrfCheck.save)
        .check(substring("Whistleblowing claims")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Whistleblowing claims - no
    ===============================================================================================*/

    .group("ET_430_Whistleblowing") {
      exec(http("ET_430_005_Whistleblowing")
        .post(baseURLETUIApp + "/whistleblowing-claims")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("whistleblowingEntityName", "")
        .formParam("whistleblowingClaim", "No")
        .check(CsrfCheck.save)
        .check(substring("Are there are any existing cases which may be linked to this new claim?")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

      /*===============================================================================================
      * Linked cases - no
      ===============================================================================================*/

      .group("ET_435_Linked_Cases") {
        exec(http("ET_435_005_Linked_Cases")
          .post(baseURLETUIApp + "/linked-cases?lng=en")
          .headers(CommonHeader)
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("_csrf", "#{csrf}")
          .formParam("et-sya-session", "#{etSession}")
          .formParam("linkedCases", "No")
          .formParam("linkedCasesDetail", "")
          .check(CsrfCheck.save)
          .check(substring("Have you completed this section?")))
      }
      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Have you completed this section?
    ===============================================================================================*/

    .group("ET_440_Claim_Section_Complete") {
      exec(http("ET_440_005_Claim_Section_Complete")
        .post(baseURLETUIApp + "/claim-details-check")
        .headers(CommonHeader)
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "#{csrf}")
        .formParam("et-sya-session", "#{etSession}")
        .formParam("claimDetailsCheck", "Yes")
        .check(substring("Describe what happened to you\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Tell us what you want from your claim\n                  </a>\n                  \n              </span>\n              \n\n              \n                <strong class=\"govuk-tag app-task-list__tag\">\n  COMPLETED"))
        .check(substring("Steps to making your claim")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    /*===============================================================================================
    * Check your answers link
    ===============================================================================================*/

    .group("ET_450_Final_Check_Answers") {
      exec(http("ET_450_005_Final_Check_Answers")
        .get(baseURLETUIApp + "/check-your-answers")
        .headers(CommonHeader)
        .check(substring("Check your answers")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*===============================================================================================
    * Final submission
    ===============================================================================================*/

    .group("ET_460_Final_Check_Submit") {
      exec(http("ET_460_005_Final_Check_Submit")
        .get(baseURLETUIApp + "/submitDraftCase?lng=en")
        .headers(CommonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .check(substring("Your claim has been submitted"))
        .check(regex("""<dd class="govuk-summary-list__value">\s*(\d+)\s*</dd>""".stripMargin).saveAs("submissionReference"))
        .check(regex("""<dd class="govuk-summary-list__value">\s*(\d+)\s*</dd>""".stripMargin).saveAs("caseId")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    /*===============================================================================================
    * Log Out
    ===============================================================================================*/

    .group("ET_470_Log_Out") {
      exec(http("ET_470_005_Log_Out")
        .get(baseURLETUIApp + "/logout")
        .headers(CommonHeader)
        .check(substring("Make a claim to an employment tribunal")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  //===============================================
	//Write session info to case to the E1Cases data file
	//===============================================
    /*.exec { session =>
    val fw = new BufferedWriter(new FileWriter("E1Cases.csv", true))
    try {
      fw.write(session("username").as[String] + "," + session("password").as[String] + "," + session("caseId").as[String] + "\r\n")
    } finally fw.close()
    session
    } */

    .exec { session =>
    val fw = new BufferedWriter(new FileWriter("E1Cases.csv", true))
    try {
      fw.write(session("caseId").as[String] + "\r\n")
    } finally fw.close()
    session
    } 
  
}
