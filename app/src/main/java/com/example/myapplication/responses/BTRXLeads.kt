package com.example.myapplication.responses

import com.google.gson.annotations.SerializedName

data class BTRXLeads(

	@field:SerializedName("result")
	val result: List<Result?>? = null,

	@field:SerializedName("next")
	val next: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("time")
	val time: Time? = null
)

data class Result(

	@field:SerializedName("ADDRESS_PROVINCE")
	val aDDRESSPROVINCE: Any? = null,

	@field:SerializedName("POST")
	val pOST: Any? = null,

	@field:SerializedName("ADDRESS_COUNTRY_CODE")
	val aDDRESSCOUNTRYCODE: Any? = null,

	@field:SerializedName("SECOND_NAME")
	val sECONDNAME: Any? = null,

	@field:SerializedName("UTM_MEDIUM")
	val uTMMEDIUM: Any? = null,

	@field:SerializedName("STATUS_SEMANTIC_ID")
	val sTATUSSEMANTICID: String? = null,

	@field:SerializedName("HONORIFIC")
	val hONORIFIC: Any? = null,

	@field:SerializedName("UTM_CONTENT")
	val uTMCONTENT: Any? = null,

	@field:SerializedName("ID")
	val iD: String? = null,

	@field:SerializedName("ADDRESS_LOC_ADDR_ID")
	val aDDRESSLOCADDRID: Any? = null,

	@field:SerializedName("STATUS_DESCRIPTION")
	val sTATUSDESCRIPTION: Any? = null,

	@field:SerializedName("MOVED_TIME")
	val mOVEDTIME: String? = null,

	@field:SerializedName("NAME")
	val nAME: String? = null,

	@field:SerializedName("MODIFY_BY_ID")
	val mODIFYBYID: String? = null,

	@field:SerializedName("DATE_MODIFY")
	val dATEMODIFY: String? = null,

	@field:SerializedName("ADDRESS")
	val aDDRESS: Any? = null,

	@field:SerializedName("STATUS_ID")
	val sTATUSID: String? = null,

	@field:SerializedName("DATE_CLOSED")
	val dATECLOSED: String? = null,

	@field:SerializedName("OPENED")
	val oPENED: String? = null,

	@field:SerializedName("ADDRESS_2")
	val aDDRESS2: Any? = null,

	@field:SerializedName("CURRENCY_ID")
	val cURRENCYID: String? = null,

	@field:SerializedName("ASSIGNED_BY_ID")
	val aSSIGNEDBYID: String? = null,

	@field:SerializedName("UTM_SOURCE")
	val uTMSOURCE: Any? = null,

	@field:SerializedName("COMMENTS")
	val cOMMENTS: Any? = null,

	@field:SerializedName("BIRTHDATE")
	val bIRTHDATE: String? = null,

	@field:SerializedName("LAST_NAME")
	val lASTNAME: Any? = null,

	@field:SerializedName("IS_RETURN_CUSTOMER")
	val iSRETURNCUSTOMER: String? = null,

	@field:SerializedName("COMPANY_ID")
	val cOMPANYID: Any? = null,

	@field:SerializedName("DATE_CREATE")
	val dATECREATE: String? = null,

	@field:SerializedName("ADDRESS_COUNTRY")
	val aDDRESSCOUNTRY: Any? = null,

	@field:SerializedName("SOURCE_ID")
	val sOURCEID: String? = null,

	@field:SerializedName("ORIGIN_ID")
	val oRIGINID: Any? = null,

	@field:SerializedName("MOVED_BY_ID")
	val mOVEDBYID: String? = null,

	@field:SerializedName("COMPANY_TITLE")
	val cOMPANYTITLE: Any? = null,

	@field:SerializedName("ORIGINATOR_ID")
	val oRIGINATORID: Any? = null,

	@field:SerializedName("ADDRESS_REGION")
	val aDDRESSREGION: Any? = null,

	@field:SerializedName("HAS_EMAIL")
	val hASEMAIL: String? = null,

	@field:SerializedName("UTM_TERM")
	val uTMTERM: Any? = null,

	@field:SerializedName("ADDRESS_CITY")
	val aDDRESSCITY: Any? = null,

	@field:SerializedName("HAS_IMOL")
	val hASIMOL: String? = null,

	@field:SerializedName("UTM_CAMPAIGN")
	val uTMCAMPAIGN: Any? = null,

	@field:SerializedName("ADDRESS_POSTAL_CODE")
	val aDDRESSPOSTALCODE: Any? = null,

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

	@field:SerializedName("HAS_PHONE")
	val hASPHONE: String? = null
)

data class TimeLeads(

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
	val finish: Double? = null,

	@field:SerializedName("operating")
	val operating: Int? = null
)
