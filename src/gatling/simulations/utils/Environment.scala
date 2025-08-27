package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

    // Get environment variables from Jenkins pipeline
    val targetEnvironment = Option(System.getenv("ENVIRONMENT")).getOrElse("perftest")
    val testUrl = Option(System.getenv("TEST_URL")).getOrElse("")
    val component = Option(System.getenv("COMPONENT")).getOrElse("sya") // fallback to sya

    // ET SYA App URL - use TEST_URL if provided (for preview with PR numbers), otherwise construct
    val baseURLETUIApp = if (testUrl.nonEmpty) {
        testUrl
    } else {
        s"https://et-${component}.${targetEnvironment}.platform.hmcts.net"
    }

    // ET SYR (Respondent) URL - derive from TEST_URL or construct
    val baseURLETUIResp = if (testUrl.nonEmpty) {
        // Replace 'sya' with 'syr' in the TEST_URL for respondent service
        testUrl.replace("et-sya", "et-syr")
    } else {
        s"https://et-syr.${targetEnvironment}.platform.hmcts.net"
    }

    // Standard platform services - extract domain from TEST_URL for preview, otherwise use environment
    val platformDomain = if (testUrl.nonEmpty && testUrl.contains("preview")) {
        // Extract domain from TEST_URL: "https://et-sya-pr-2129.preview.platform.hmcts.net" -> "preview.platform.hmcts.net"
        val urlParts = testUrl.replace("https://", "").split("\\.")
        urlParts.drop(1).mkString(".") // Skip the first part (et-sya-pr-2129), keep the rest
    } else {
        s"${targetEnvironment}.platform.hmcts.net"
    }

    val baseURL = s"https://manage-case.${platformDomain}"
    val respLoginURL = s"https://manage-case.${platformDomain}"
    val idamURL = s"https://idam-web-public.${platformDomain}"
    val pcqURL = s"https://pcq.${platformDomain}"

    // Debug output to verify environment detection and URL construction
    println("=" * 80)
    println("GATLING ENVIRONMENT DETECTION")
    println("=" * 80)
    println(s"Target Environment: ${targetEnvironment}")
    println(s"TEST_URL from Jenkins: ${if (testUrl.nonEmpty) testUrl else "NOT SET"}")
    println(s"Component: ${component}")
    println(s"Platform Domain: ${platformDomain}")
    println("-" * 80)
    println("CONSTRUCTED URLS:")
    println(s"ET SYA App URL: ${baseURLETUIApp}")
    println(s"ET SYR Resp URL: ${baseURLETUIResp}")
    println(s"Manage Case URL: ${baseURL}")
    println(s"IDAM URL: ${idamURL}")
    println(s"PCQ URL: ${pcqURL}")
    println("=" * 80)

    val minThinkTime = 5
    val maxThinkTime = 10

    val HttpProtocol = http
  }
