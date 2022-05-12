package com.example.myapplication.responses

import com.google.gson.annotations.SerializedName

data class ResponseFB(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null
)

data class DataItem(

	@field:SerializedName("ctr")
	val ctr: String? = null,

	@field:SerializedName("cpm")
	val cpm: String? = null,

	@field:SerializedName("date_start")
	val dateStart: String? = null,

	@field:SerializedName("spend")
	val spend: String? = null,

	@field:SerializedName("clicks")
	val clicks: String? = null,

	@field:SerializedName("date_stop")
	val dateStop: String? = null,

	@field:SerializedName("impressions")
	val impressions: String? = null,

	@field:SerializedName("frequency")
	val frequency: String? = null,

	@field:SerializedName("reach")
	val reach: String? = null
)
