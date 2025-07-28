package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scenarios._
import utils.Environment
import utils.Headers
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.http.Predef._
import io.gatling.core.pause.PauseType
import io.gatling.commons.stats.assertion.Assertion

import scala.concurrent.duration._

class ET_Simulation extends Simulation {

  val BaseURL = Environment.baseURL
  val UserFeederET = csv("UserDataET.csv").circular
  val CasesToProgress = csv("ETCasesToProgress.csv").circular
  val ET3CaseLinkDataFeeder = csv("E3CaseLinkData.csv")
  val CitizenUserFeeder = csv("ETCitizenUsers.csv").circular
  val CaseLinkUserFeederETXUI = csv("ETCaseLinkUsers.csv").circular
  val CaseFlagUserFeederETXUI = csv("ETCaseFlagUsers.csv").circular
  val CaseLinkFeeder = csv("CaseLinkCases.csv").circular
  val CaseFlagFeeder = csv("CaseFlagCases.csv").circular
  val CaseFileViewFeeder = csv("CaseFileViewCases.csv").circular

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match{
    case "perftest" => "perftest"
    case "pipeline" => "perftest" //updated pipeline to run against perftest - change to aat to run against AAT
    case _ => "**INVALID**"
  }
  /* ******************************** */
  /* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
  val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
  val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
  /* ******************************** */

  /* PERFORMANCE TEST CONFIGURATION */
  val rampUpDurationMins = 5
  val rampDownDurationMins = 2
  val testDurationMins = 60

  val hourlyTarget: Double = 1
  val ratePerSec = hourlyTarget / 3600

  //===================================
  //Citizen Hub Perf Test Config
  //===================================
  val et3RequestPerHour: Double = 17
  val et1CitizenRequestPerHour: Double = 49
  //===================================
  //ET XUI Perf Test Config
  //===================================
  val et1LegalRepRequestPerHour: Double = 49
  val etLegalRepCaseFlag: Double = 14
  val etLegalRepCaseLink: Double = 26
  val etCaseFileView: Double = 44

  val numberOfPipelineUsers: Double = 5

  //If running in debug mode, disable pauses between steps
  val pauseOption: PauseType = debugMode match {
    case "off" => constantPauses
    case _ => disabledPauses
  }

  //============================================================
  // Define protocol, base URL, DT Header, and other settings
  //============================================================

     // The Load Test Name uniquely identifies a test execution
    val LTN =
      getClass.getSimpleName +
      "_" +
      LocalDateTime.now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    // Load Script Name - name of the load testing script.
    val LSN = "PeakLoad" // Get LSN

    val httpProtocol = Environment.HttpProtocol
      .baseUrl(BaseURL)
      .disableCaching
      .disableAutoReferer
    //.doNotTrackHeader("1")
      .inferHtmlResources()
      .silentResources
     // Define a global signing function to be applied on all generated requests.
      .sign { (request, session) =>
     // Test Step Name is a logical test step within your load testing script
      val TSN = request.getName

      request.getHeaders.set(
        "x-dynatrace-test",
        s"TSN=$TSN;LSN=$LSN;LTN=$LTN"
      )
      request
    }

      //.header("x-dynatrace-test", "TSN=GatlingTest;VU=#{username}")
  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
    println(s"LTN: ${LTN}")
    println(s"LSN: ${LSN}")
  }

/**========================================================================
 Create a claim within XUI as a solicitor
 =========================================================================*/

  val ETXUIClaim = scenario( "ETCreateClaim")
    .exitBlockOnFail {
      exec(  _.set("env", s"${env}"))
       /* .exec(flushHttpCache)
        .exec(flushCookieJar)*/
        .feed(CaseLinkUserFeederETXUI)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
          .exec(ET_CaseCreation.MakeAClaim)
    }

  /*=======================================================================
  Initiate a claim scenario within XUI
  ========================================================================*/

  val ETXUIFormClaimScenario = scenario("ET Create Claim - XUI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
         .exec(flushHttpCache)
         .exec(flushCookieJar)
        .feed(CaseLinkUserFeederETXUI)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(XUI_ETCaseCreation.InitiateAClaim)
        .exec(XUI_ETCaseCreation.ETFormClaimantDetailsSection1)
        .exec(XUI_ETCaseCreation.ETFormEmploymentDetailsSection2)
        .exec(XUI_ETCaseCreation.ETFormClaimDetailsSection3)
        .exec(XUI_ETCaseCreation.submitETForm)
    }

  /*=======================================================================
  Link a case scenario within XUI
  ========================================================================*/

  val ETXUICaseLink = scenario("ET Case Link - XUI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
       /* .exec(flushHttpCache)
        .exec(flushCookieJar)*/
        .feed(CaseLinkUserFeederETXUI).feed(CaseLinkFeeder)
        .exec(Homepage.XUIHomePage)
        .pause(10)
        .exec(Login.XUILogin)
        .exec(ET_CaseLink.manageCaseLink)
        .exec(Logout.XUILogout)
    }

  /*=======================================================================
  Case Flag scenario within XUI
  ========================================================================*/

  val ETXUICaseFlag = scenario("ET Case Flag")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
       /* .exec(flushHttpCache)
        .exec(flushCookieJar)*/
        .feed(CaseFlagUserFeederETXUI).feed(CaseFlagFeeder)
        .exec(Homepage.XUIHomePage)
        .pause(10)
        .exec(Login.XUILogin)
        .exec(ET_CaseFlag.manageCaseFlag)
        .exec(Logout.XUILogout)
    }

  /*=======================================================================
  Create case and Add Case Flag scenario within XUI
  ========================================================================*/

  val ETXUICreateCaseAndCaseFlag = scenario("ET Create Case and Case Flag - XUI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      //Create case steps & feeder
        .feed(CaseLinkUserFeederETXUI)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
          .exec(ET_CaseCreation.MakeAClaim)
          .exec(Logout.XUILogout)
      //Case Flag steps & feeder
        .feed(CaseFlagUserFeederETXUI)//.feed(CaseFlagFeeder)
        .exec(Homepage.XUIHomePage)
        .pause(10)
        .exec(Login.XUILogin)
        .exec(ET_CaseFlag.manageCaseFlag)
        .exec(Logout.XUILogout)
    }

  /*=======================================================================
  ET Create claim as a Citizen
  ========================================================================*/

  val ETCreateClaim = scenario("ET Citizen Create Clain - Citizen UI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(UserFeederET)
        .exec(ET_MakeAClaim.MakeAClaim)
        .exec(ET_MakeAClaimPt2.MakeAClaim)
    }

/**========================================================================
 Create data for ET3 Process:
  --> Process claim (caseworker) --> Letter Generation
 =========================================================================*/

  val ET3DataPrepProcessClaim = scenario("ETProcessClaimXUI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .feed(CaseFlagUserFeederETXUI)
        .feed(CasesToProgress)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseWorker.MakeAClaim)
        .exec(ET_CaseWorker.dateCaseAccepted)
        .exec(ET_CaseWorker.generateLetters)
    }

/**========================================================================
 Create data for ET3 Process:
 Create Claim (Citizen) --> Process claim (caseworker) --> Letter Generation
 =========================================================================*/

  val ET3DataPrepCombined = scenario("ETCreateClaimAndProgress")
    .exec(session => session.set("LSN", "ET3DataPrepCombined")) // Set script name
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(UserFeederET)
        .exec(ET_MakeAClaim.MakeAClaim)
        .exec(ET_MakeAClaimPt2.MakeAClaim)
        //Caseworker Journey starts here
        .feed(CaseFlagUserFeederETXUI)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseWorker.MakeAClaim)
        .exec(ET_CaseWorker.dateCaseAccepted)
        .exec(ET_CaseWorker.generateLetters)
    }

/**========================================================================
 ET3 Process Respondent
 =========================================================================*/

  val ET3CitizenRespondent = scenario("ET Citizen 3 Form Respondent - Citizen UI")
    .exitBlockOnFail {
      group("ET3 Citizen Respondent") {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(CitizenUserFeeder)
        .feed(ET3CaseLinkDataFeeder)
        .exec(ET_Citizen.RespondentCaseNumberCheck)
        .exec(Login.CUILogin)
        .exec(ET_Citizen.BeforeYouContinue)
          // Executes if the user has no cases assigned
          .doIf(session =>
                session("noCaseAssignedToUserCount").as[String].toInt != 0
          ) {
                exec(ET_Citizen.RespondentSelfAssignment)
            }
          // Executes if the user already has cases assigned to them but not the case which has been fed into the user session
          .doIf(session =>
                session("correctCaseAssignedToUserCount").as[String].toInt == 0 &&
                session("casesAssignedToUserCount").as[String].toInt != 0
          ) {
              exec(ET_Citizen.RespondentNewClaimReply)
              .exec(ET_Citizen.RespondentSelfAssignment)
            }
        .exec(ET_Citizen.RespondentET3)
        .exec(ET_Citizen.RespondentET3ClaimantInfo)
        .exec(ET_Citizen.RespondentET3ContestTheClaim)
        //.exec(ET_Citizen.RespondentET3EmployersContractClaim) //Commented out as only visible if claim type is Breach of Contract
        .exec(ET_Citizen.RespondentET3CheckYourAnswers)
        .exec(ET_Citizen.RespondentET3OpenCompletedForm)
      }
    }

  /*=========================================================================
  Following scenario is for uploading the documents to existing cases
  ==========================================================================*/

  val ETUploadDocs = scenario("ET Upload Documents")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(CaseLinkUserFeederETXUI).feed(CaseFlagFeeder)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseDocUpload.DocUpload)
        .exec(Logout.XUILogout)
    }

   /*=========================================================================
    Following scenario is for uploading the documents to existing cases
  ============================================================================*/

  val ETUploadDocs2 = scenario("ET Upload Documents2")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(CaseLinkUserFeederETXUI).feed(CaseFlagFeeder)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseDocUpload2.DocUpload2)
        .exec(Logout.XUILogout)
    }

  /*=========================================================================
    Case File View Scenario
  ============================================================================*/

  val ETCaseFileView = scenario("ET Case File View - XUI")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(CaseLinkUserFeederETXUI)
        .feed(CaseFileViewFeeder)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseFileView.CaseFileView)
        .exec(Logout.XUILogout)
    }

  //===========================================================================
  // defines the Gatling simulation model, based on the inputs
  //============================================================================

  def simulationProfile(simulationType: String, userPerHourRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    val userPerSecRate = userPerHourRate / 3600
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins.minutes),
            constantUsersPerSec(userPerSecRate) during (testDurationMins.minutes),
            rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins.minutes)
          )
        }
        else{
          Seq(atOnceUsers(1))
        }
      case "pipeline" =>
        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2.minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //===========================================================================
  //defines the test assertions, based on the test type
  //===========================================================================
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" | "pipeline" => //currently using the same assertions for a performance test and the pipeline
        if (debugMode == "off") {
          Seq(global.successfulRequests.percent.gte(95),
            details("ET_460_Final_Check_Submit").successfulRequests.percent.gte(90))
            //details("ET_CTZ_490_005_SubmitET3Application").successfulRequests.percent.gte(90),
            //details("XUI_ET_450_ETCaseSubmit").successfulRequests.percent.gte(90),
            //details("ET_CaseFlag_090_UpdateFlagComments").successfulRequests.percent.gte(90),
            //details("ET_CaseLink_090_SubmitUnlinkCases").successfulRequests.percent.gte(90),
            //details("ET_CaseFileView_030_CaseFileView").successfulRequests.percent.gte(90))
        }
        else {
          Seq(global.successfulRequests.percent.is(100))
        }
      case _ =>
        Seq()
    }
  }

  setUp(

/*==============================================================================================================
   Data Prep/Debugging Scenarios
   ===============================================================================================================*/
    //ET3DataPrepCombined.inject(rampUsers(5) during (20))
    ETCreateClaim.inject(rampUsers(3) during (3)),
    //ET3DataPrep.inject(rampUsers(1) during (3)),
    //ET3DataPrepProcessClaim.inject(rampUsers(19) during (20))

    //ET3CitizenRespondent.inject(rampUsers(1) during (3))
    //ETXUICaseLink.inject(rampUsers(1) during (3)),
    //ETXUICaseFlag.inject(nothingFor(1), rampUsers(1) during (3))
    //ETXUICreateCaseAndCaseFlag.inject(nothingFor(1), rampUsers(1) during (3))
    //ETCaseFileView.inject(nothingFor(1), rampUsers(1) during (3))
    //ETXUIFormClaimScenario.inject(nothingFor(1), rampUsers(1) during (3))

   /*==============================================================================================================
   Performance Test Scenarios
   //===============================================================================================================*/
   // ET Citizen Hub
   //==============================================================================================================
      //ETCreateClaim.inject(simulationProfile(testType, et1CitizenRequestPerHour, numberOfPipelineUsers)).pauses(pauseOption),
  //  ET3CitizenRespondent.inject(simulationProfile(testType, et3RequestPerHour, numberOfPipelineUsers)).pauses(pauseOption),

//==============================================================================================================
  // ET XUI
  //===============================================================================================================
  //   ETXUIFormClaimScenario.inject(simulationProfile(testType, et1LegalRepRequestPerHour, numberOfPipelineUsers)).pauses(pauseOption),
  //   ETXUICreateCaseAndCaseFlag.inject(simulationProfile(testType, etLegalRepCaseFlag, numberOfPipelineUsers)).pauses(pauseOption),
  //   ETXUICaseLink.inject(simulationProfile(testType, etLegalRepCaseLink, numberOfPipelineUsers)).pauses(pauseOption),
  //   ETCaseFileView.inject(simulationProfile(testType, etCaseFileView, numberOfPipelineUsers)).pauses(pauseOption)


   ).protocols(httpProtocol)
  .assertions(assertions(testType))

} 

// Archive scenarios
  //======================
    // ETXUIFormClaimScenario.inject(nothingFor(5), rampUsers(20) during (3600))
    //  ETXUIClaim.inject(nothingFor(5), rampUsers(1) during (1))
    // ETUploadDocs.inject(nothingFor(5), rampUsers(23) during (1200))
    // ETUploadDocs2.inject(nothingFor(5), rampUsers(1) during (1))
    // ETXUICaseLink.inject(nothingFor(10), rampUsers(30) during (3600)),
    // ETXUICaseFlag.inject(nothingFor(30), rampUsers(50) during (3600)),
    //   ETCaseFileView.inject(nothingFor(50), rampUsers(45) during (3600))
      /* ETXUICaseLink.inject(nothingFor(10), rampUsers(1) during (3)),
    // ETXUICaseFlag.inject(nothingFor(30), rampUsers(1) during (36)),
    // ETCaseFileView.inject(nothingFor(50), rampUsers(1) during (36))*/

//Archive Workload Profile:
  //ETCreateClaim.inject(constantUsersPerSec(7).during(10))
   // ET3CitizenRespondent.inject(constantUsersPerSec(2).during(10))

  // generate an open workload injection profile
  // with levels of 10, 15, 20, 25 and 30 arriving users per second
  // each level lasting 10 seconds
  // separated by linear ramps lasting 10 seconds
  // ETCreateClaim.inject(
  //     incrementUsersPerSec(5)
  //       .times(5)
  //       .eachLevelLasting(60)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(5) // Int
  // )
  //   ET3CitizenRespondent.inject(
  //     incrementConcurrentUsers(10)
  //       .times(5)
  //       .eachLevelLasting(120)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(10) // Int
  //   ),
  //   ETXUIFormClaimScenario.inject(
  //     incrementConcurrentUsers(10)
  //       .times(5)
  //       .eachLevelLasting(120)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(10) // Int
  //   ),
  //   ETXUICreateCaseAndCaseFlag.inject(
  //     incrementConcurrentUsers(10)
  //       .times(5)
  //       .eachLevelLasting(120)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(10) // Int
  //   ),
  //   ETXUICaseLink.inject(
  //     incrementConcurrentUsers(10)
  //       .times(5)
  //       .eachLevelLasting(120)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(10) // Int
  //   ),
  //   ETCaseFileView.inject(
  //     incrementConcurrentUsers(10)
  //       .times(5)
  //       .eachLevelLasting(120)
  //       .separatedByRampsLasting(10)
  //       .startingFrom(10) // Int
  //   )
  

