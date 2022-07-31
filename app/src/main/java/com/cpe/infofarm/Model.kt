package com.cpe.infofarm

class Model {
    lateinit var temp:String
    lateinit var location:String
    lateinit var sky:String
    lateinit var skyIcon:String
    lateinit var forecastSixDescription:String
    lateinit var forecastSixIcon:String
    var forecastSixRain: Int ?= null
    lateinit var forecastTwelveDescription:String
    lateinit var forecastTwelveIcon:String
    var forecastTwelveRain: Int ?= null
    lateinit var forecastThreeDescription:String
    lateinit var forecastThreeIcon:String
    var forecastThreeRain: Int ?= null

    constructor(temp: String, location: String,
               sky: String, skyIcon:String,
                forecastSixDescription: String, forecastSixIcon: String,
                forecastTwelveDescription: String, forecastTwelveIcon: String,
                forecastThreeDescription: String, forecastThreeIcon: String,
                forecastSixRain:Int, forecastTwelveRain:Int, forecastThreeRain: Int
    ) {
        this.temp = temp
        this.location = location
        this.sky = sky
        this.skyIcon = skyIcon
        this.forecastSixDescription = forecastSixDescription
        this.forecastSixIcon = forecastSixIcon
        this.forecastSixRain = forecastSixRain
        this.forecastTwelveDescription = forecastTwelveDescription
        this.forecastTwelveIcon = forecastTwelveIcon
        this.forecastTwelveRain = forecastTwelveRain
        this.forecastThreeDescription = forecastThreeDescription
        this.forecastThreeIcon = forecastThreeIcon
        this.forecastThreeRain = forecastThreeRain
    }

    constructor()
}