package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios._
import utils.Environment
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.http.Predef._
import io.gatling.core.pause.PauseType
import io.gatling.commons.stats.assertion.Assertion

import scala.concurrent.duration._

class ET_Simulation extends Simulation {

  val BaseURL = Environment.baseURL
  val UserFeederET = csv("UserDataET.csv").circular
  val UserFeederETCase = csv("caseWorkerUsers.csv").circular

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
  val rampUpDurationMins = 2
  val rampDownDurationMins = 2
  val testDurationMins = 60


  val hourlyTarget: Double = 100
  val ratePerSec = hourlyTarget / 3600


  val numberOfPipelineUsers: Double = 100


  //If running in debug mode, disable pauses between steps
  val pauseOption: PauseType = debugMode match {
    case "off" => constantPauses
    case _ => disabledPauses
  }


  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
  //  .disableCaching
  //  .disableAutoReferer
  //  .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources
    .header("experimental", "true")

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

  val ETCreateClaim = scenario( "ETCreateClaim")
    .exitBlockOnFail {
      exec(  _.set("env", s"${env}"))
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .feed(UserFeederET)
          .exec(ET_MakeAClaim.MakeAClaim)
            .exec(ET_MakeAClaimPt2.MakeAClaim)
    }

    .exec {
      session =>
        println(session)
        session
    }


  val ETCaseWorker = scenario( "ETCaseWorker")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}")
        .set("caseType", "ET_EnglandWales")
      )
        .feed(UserFeederETCase)
        .exec(RespondentLogin.XUIHomePage)
        .exec(RespondentLogin.XUILogin)
        .exec(ET_CaseWorker.MakeAClaim)
    }

    .exec {
      session =>
        println(session)
        session
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
    ETCaseWorker.inject(simulationProfile(testType, ratePerSec, numberOfPipelineUsers)).pauses(pauseOption)
  ).protocols(httpProtocol)
    .assertions(assertions(testType))


}
