package com.example.myapplication.responses

import com.google.gson.annotations.SerializedName

data class ResponseBTRXDeals(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("next")
	val next: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("time")
	val time: Time? = null
)

data class ResultItem(

	@field:SerializedName("CATEGORY_ID")
	val cATEGORYID: String? = null,

	@field:SerializedName("CURRENCY_ID")
	val cURRENCYID: String? = null,

	@field:SerializedName("CLOSED")
	val cLOSED: String? = null,

	@field:SerializedName("ASSIGNED_BY_ID")
	val aSSIGNEDBYID: String? = null,

	@field:SerializedName("IS_REPEATED_APPROACH")
	val iSREPEATEDAPPROACH: String? = null,

	@field:SerializedName("UTM_SOURCE")
	val uTMSOURCE: Any? = null,

	@field:SerializedName("COMMENTS")
	val cOMMENTS: Any? = null,

	@field:SerializedName("ADDITIONAL_INFO")
	val aDDITIONALINFO: Any? = null,

	@field:SerializedName("IS_RECURRING")
	val iSRECURRING: String? = null,

	@field:SerializedName("LEAD_ID")
	val lEADID: String? = null,

	@field:SerializedName("UTM_MEDIUM")
	val uTMMEDIUM: Any? = null,

	@field:SerializedName("UTM_CONTENT")
	val uTMCONTENT: Any? = null,

	@field:SerializedName("LOCATION_ID")
	val lOCATIONID: Any? = null,

	@field:SerializedName("ID")
	val iD: String? = null,

	@field:SerializedName("BEGINDATE")
	val bEGINDATE: String? = null,

	@field:SerializedName("CLOSEDATE")
	val cLOSEDATE: String? = null,

	@field:SerializedName("STAGE_ID")
	val sTAGEID: String? = null,

	@field:SerializedName("IS_RETURN_CUSTOMER")
	val iSRETURNCUSTOMER: String? = null,

	@field:SerializedName("COMPANY_ID")
	val cOMPANYID: String? = null,

	@field:SerializedName("QUOTE_ID")
	val qUOTEID: Any? = null,

	@field:SerializedName("DATE_CREATE")
	val dATECREATE: String? = null,

	@field:SerializedName("TAX_VALUE")
	val tAXVALUE: Any? = null,

	@field:SerializedName("SOURCE_ID")
	val sOURCEID: String? = null,

	@field:SerializedName("ORIGIN_ID")
	val oRIGINID: Any? = null,

	@field:SerializedName("IS_NEW")
	val iSNEW: String? = null,

	@field:SerializedName("MOVED_BY_ID")
	val mOVEDBYID: String? = null,

	@field:SerializedName("ORIGINATOR_ID")
	val oRIGINATORID: Any? = null,

	@field:SerializedName("STAGE_SEMANTIC_ID")
	val sTAGESEMANTICID: String? = null,

	@field:SerializedName("MOVED_TIME")
	val mOVEDTIME: String? = null,

	@field:SerializedName("UTM_TERM")
	val uTMTERM: Any? = null,

	@field:SerializedName("UTM_CAMPAIGN")
	val uTMCAMPAIGN: Any? = null,

	@field:SerializedName("TYPE_ID")
	val tYPEID: String? = null,

	@field:SerializedName("PROBABILITY")
	val pROBABILITY: Any? = null,

	@field:SerializedName("MODIFY_BY_ID")
	val mODIFYBYID: String? = null,

	@field:SerializedName("DATE_MODIFY")
	val dATEMODIFY: String? = null,

	@field:SerializedName("SOURCE_DESCRIPTION")
	val sOURCEDESCRIPTION: String? = null,

	@field:SerializedName("TITLE")
	val tITLE: String? = null,

	@field:SerializedName("CONTACT_ID")
	val cONTACTID: String? = null,

	@field:SerializedName("OPPORTUNITY")
	val oPPORTUNITY: String? = null,

	@field:SerializedName("CREATED_BY_ID")
	val cREATEDBYID: String? = null,

	@field:SerializedName("IS_MANUAL_OPPORTUNITY")
	val iSMANUALOPPORTUNITY: String? = null,

	@field:SerializedName("OPENED")
	val oPENED: String? = null
)

data class Time(

	@field:SerializedName("duration")
	val duration: Double? = null,

	@field:SerializedName("date_start")
	val dateStart: String? = null,

	@field:SerializedName("start")
	val start: Double? = null,

	@field:SerializedName("date_finish")
	val dateFinish: String? = null,

	@field:SerializedName("processing")
	val processing: Double? = null,

	@field:SerializedName("finish")
	val finish: Double? = null
)
