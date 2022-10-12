var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "1",
        "ok": "0",
        "ko": "1"
    },
    "minResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "maxResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "meanResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "standardDeviation": {
        "total": "0",
        "ok": "-",
        "ko": "0"
    },
    "percentiles1": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles2": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles3": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles4": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 0,
    "percentage": 0
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "count": 1,
    "percentage": 100
},
    "meanNumberOfRequestsPerSecond": {
        "total": "1",
        "ok": "-",
        "ko": "1"
    }
},
contents: {
"group_et-010-home-c2c25": {
          type: "GROUP",
name: "ET_010_Home",
path: "ET_010_Home",
pathFormatted: "group_et-010-home-c2c25",
stats: {
    "name": "ET_010_Home",
    "numberOfRequests": {
        "total": "1",
        "ok": "0",
        "ko": "1"
    },
    "minResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "maxResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "meanResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "standardDeviation": {
        "total": "0",
        "ok": "-",
        "ko": "0"
    },
    "percentiles1": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles2": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles3": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles4": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 0,
    "percentage": 0
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "count": 1,
    "percentage": 100
},
    "meanNumberOfRequestsPerSecond": {
        "total": "1",
        "ok": "-",
        "ko": "1"
    }
},
contents: {
"req_et-010-005-home-de806": {
        type: "REQUEST",
        name: "ET_010_005_Home",
path: "ET_010_Home / ET_010_005_Home",
pathFormatted: "req_et-010-home---e-d5051",
stats: {
    "name": "ET_010_005_Home",
    "numberOfRequests": {
        "total": "1",
        "ok": "0",
        "ko": "1"
    },
    "minResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "maxResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "meanResponseTime": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "standardDeviation": {
        "total": "0",
        "ok": "-",
        "ko": "0"
    },
    "percentiles1": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles2": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles3": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "percentiles4": {
        "total": "30",
        "ok": "-",
        "ko": "30"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 0,
    "percentage": 0
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "count": 1,
    "percentage": 100
},
    "meanNumberOfRequestsPerSecond": {
        "total": "1",
        "ok": "-",
        "ko": "1"
    }
}
    }
}

     }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
