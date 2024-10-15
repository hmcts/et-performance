package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
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
  val CitizenUserFeeder = csv("EtCitizenUsers.csv")
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
  val rampUpDurationMins = 1//2
  val rampDownDurationMins = 1//2
  val testDurationMins = 5//60

  val hourlyTarget: Double = 1//100
  val ratePerSec = hourlyTarget / 3600

  val numberOfPipelineUsers: Double = 100

  //If running in debug mode, disable pauses between steps
  val pauseOption: PauseType = debugMode match {
    case "off" => constantPauses
    case _ => disabledPauses
  }


  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .disableCaching
    .disableAutoReferer
   // .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

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
  
  /*
  Following is initiate a claim
   */
  
  val XUIETFormClaimScenario = scenario("ET Form Claim Scenario")
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
  
  val ETXUICaseLink = scenario("ET Case Link")
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
  
  val ETCreateClaim = scenario("ETCreateClaim")
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
 Create Claim (Citizen) --> Process claim (caseworker) --> Letter Generation
 =========================================================================*/

  val ET3DataPrep = scenario("ETCreateClaim")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(UserFeederET)
        .exec(ET_MakeAClaim.MakeAClaim)
        .exec(ET_MakeAClaimPt2.MakeAClaim)
        //Caseworker Journey starts here
        //.exec(flushHttpCache)
        //.exec(flushCookieJar)
        //.feed(CaseFlagUserFeederETXUI)
        //.exec(Homepage.XUIHomePage)
        //.pause(10)
        //.exec(Login.XUILogin)
        //.exec(ET_CaseWorker.MakeAClaim)
    }

/**========================================================================
 Create data for ET3 Process:
  --> Process claim (caseworker) --> Letter Generation
 =========================================================================*/

  val ET3DataPrepProcessClaim = scenario("ETCreateClaim")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        //.exec(flushHttpCache)
        //.exec(flushCookieJar)
        .feed(CaseFlagUserFeederETXUI)
        .feed(CasesToProgress)
        .exec(Homepage.XUIHomePage)
        .exec(Login.XUILogin)
        .exec(ET_CaseWorker.MakeAClaim)
        .exec(ET_CaseWorker.dateCaseAccepted)
        .exec(ET_CaseWorker.generateLetters)
    }

/**========================================================================
 ET3 Process Respondent
 =========================================================================*/

  val ET3CitizenRespondent = scenario("ET3FormRespondent")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(CitizenUserFeeder)
        .feed(ET3CaseLinkDataFeeder)
        .exec(ET_Citizen.RespondentIntroduction)
        .exec(Login.CUILogin)
        //.doIfOrElse(session => session("userResponses").asOption[String].contains("multiple")) {
        .doIf(session => session("userResponses").asOption[String].contains("multiple")) {
          // Executes if the user already has cases assigned
          exec(ET_Citizen.RespondentNewClaimReply)
          }
        .exec(ET_Citizen.RespondentSelfAssignment) 
        .exec(ET_Citizen.RespondentET3)
        .exec(ET_Citizen.RespondentET3ClaimantInfo)
        .exec(ET_Citizen.RespondentET3ContestTheClaim)
        //.exec(ET_CaseWorker.MakeAClaim)
        //.exec(ET_CaseWorker.dateCaseAccepted)
        //.exec(ET_CaseWorker.generateLetters)
    }
  
  
  /** Following scenario is for uploading the documents to existing cases  */

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
  
  /** Following scenario is for uploading the documents to existing cases */
  
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
  
  val ETCaseFileView = scenario("ET Upload Documents2")
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
  
  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, userPerSecRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
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

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" | "pipeline" => //currently using the same assertions for a performance test and the pipeline
        if (debugMode == "off") {
          Seq(global.successfulRequests.percent.gte(95),
            details("ET_460_Final_Check_Submit").successfulRequests.percent.gte(90))
        }
        else {
          Seq(global.successfulRequests.percent.is(100))
        }
      case _ =>
        Seq()
    }
  }
  
  setUp(
   // ETCreateClaim.inject(simulationProfile(testType, ratePerSec, numberOfPipelineUsers)).pauses(pauseOption)
    //ETCreateClaim.inject(rampUsers(1) during (10))
    //ET3DataPrep.inject(rampUsers(25) during (20))
    //ET3DataPrepProcessClaim.inject(rampUsers(20) during (20))
    ET3CitizenRespondent.inject(rampUsers(1) during (20))
   // XUIETFormClaimScenario.inject(nothingFor(5), rampUsers(20) during (3600))
  //  ETXUIClaim.inject(nothingFor(5), rampUsers(1) during (1))
   // ETUploadDocs.inject(nothingFor(5), rampUsers(23) during (1200))
    // ETUploadDocs2.inject(nothingFor(5), rampUsers(1) during (1))
 // ETXUICaseLink.inject(nothingFor(10), rampUsers(30) during (3600)),
 // ETXUICaseFlag.inject(nothingFor(30), rampUsers(50) during (3600)),
 //   ETCaseFileView.inject(nothingFor(50), rampUsers(45) during (3600))
   /* ETXUICaseLink.inject(nothingFor(10), rampUsers(1) during (3)),
  ETXUICaseFlag.inject(nothingFor(30), rampUsers(1) during (36)),
  ETCaseFileView.inject(nothingFor(50), rampUsers(1) during (36))*/
  ).protocols(httpProtocol)
   // .assertions(assertions(testType))
  
}
