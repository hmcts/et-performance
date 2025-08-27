package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

    // Get environment from Jenkins pipeline (passed via ENVIRONMENT variable)
    val targetEnvironment = Option(System.getenv("ENVIRONMENT")).getOrElse("perftest")

    // Handle special cases for preview environment (PR-based URLs)
    val environmentSuffix = targetEnvironment match {
      case env if env.startsWith("preview") => "preview"
      case env => env
    }

    val baseURLETUIApp = s"https://et-sya.${environmentSuffix}.platform.hmcts.net"
    val baseURLETUIResp = s"https://et-syr.${environmentSuffix}.platform.hmcts.net"
    val baseURL = s"https://manage-case.${environmentSuffix}.platform.hmcts.net"
    val respLoginURL = s"https://manage-case.${environmentSuffix}.platform.hmcts.net"
    val idamURL = s"https://idam-web-public.${environmentSuffix}.platform.hmcts.net"
    val pcqURL = s"https://pcq.${environmentSuffix}.platform.hmcts.net"

    val minThinkTime = 5
    val maxThinkTime = 10

    val HttpProtocol = http
  }
