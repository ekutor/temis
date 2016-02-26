package com.co.hsg.generator.bean;

import com.co.hsg.generator.util.Util;

public class ReportField
{
private String NCONTRATO;
private String ARRENDATARIO1;
private String DOC_ARR1;
private String DOC_DEU1;
private String DOC_DEU2;
private String TIPO_DOC_ARR1;
private String TIPO_DOC_DEU1;
private String TIPO_DOC_DEU2;
private String DIRECCION;
private String MUNICIPIO;
private String PARQUEADERO;
private String NUM_APTO;
private String UTIL;
private String URBANIZACION;
private String BARRIO;
private String NUM_PUERTA;
private String NOMENCLATURA;
private String MATR_INMOB;
private String MATR_INMOB_PARQ;
private String FECHA_INI_LARGA;
private String FECHA_FIN_LARGA;
private String CANON_LETRAS;
private String CANON;
private String DIA_LETRAS;
private String DIAPAGO;
private String DIR_NOT_ARR1,DIR_NOT_ARR2;
private String MAIL_ARR1;
private String TEL_ARR1,LINEATEL,GAS,GAS_PIPETA,AGUA,ENERGIA;
private String CEL_ARR1;
private String DIR_DEU1;
private String DIR_DEU2;
private String TEL_DEU1;
private String CEL_DEU1;
private String MAIL_DEU1;
private String DEUDOR1;
private String DEUDOR2;
private String MAIL_DEU2;
private String CEL_DEU2;
private String TEL_DEU2;
private String FECHA_INI;
private String FECHA_FIN;
private String MUNI_DEU1;
private String MUNI_DEU2;
private String DIR_NOT_DEU1;
private String DIR_NOT_DEU2;
private String MATR_INMOB_SIGLA;
private String FECHA_PAGO;
private String VIGENCIA;
private String ELABORADO_POR;
private String ELABORADO_POR_ID;
private String FI_DIA,FF_DIA,DIA_INI_LETRAS,DIA_FIN_LETRAS;
private String FI_MES,FF_MES, FI_MES_LETRAS,FF_MES_LETRAS;
private String FI_ANO,FF_ANO;
private String id;
private String CLAUSULA;
private String BANCO;
private String NUM_CUENTA;
private String TIPO_CUENTA;
private String TITULAR_CUENTA;
private String TIPO_DOC_TITULAR;
private String DOC_TITULAR;

public String getELABORADO_POR()
{
     return this.ELABORADO_POR;
}

public void setELABORADO_POR(String eLABORADO_POR) {
     this.ELABORADO_POR = Util.validateNull(eLABORADO_POR);
}

public String getGAS() {
	return GAS;
}

public String getDIR_NOT_ARR2() {
	return DIR_NOT_ARR2;
}

public void setDIR_NOT_ARR2(String dIR_NOT_ARR2) {
	DIR_NOT_ARR2 = Util.validateNull(dIR_NOT_ARR2);
}

public void setGAS(String gAS) {
	if(gAS!= null){
		GAS = gAS.toUpperCase();
	}else{
		GAS = "NO";
	}
	
}

public String getGAS_PIPETA() {
	return GAS_PIPETA;
}

public void setGAS_PIPETA(String gAS_PIPETA) {
	if(gAS_PIPETA!= null){
		GAS_PIPETA = gAS_PIPETA.toUpperCase();
	}else{
		GAS_PIPETA = "NO";
	}
	
}

public String getNCONTRATO()
{
     return this.NCONTRATO;
}

public String getFI_MES_LETRAS() {
	return FI_MES_LETRAS;
}

public String getDIA_FIN_LETRAS() {
	return DIA_FIN_LETRAS;
}

public String getDIA_INI_LETRAS() {
	return DIA_INI_LETRAS;
}

public String getAGUA() {
	return AGUA;
}

public void setAGUA(String aGUA) {
	if(aGUA!= Util.validateNull(null)){
		aGUA = aGUA.toUpperCase();
	}
	AGUA = Util.validateNull(aGUA);
}

public String getENERGIA() {
	return ENERGIA;
}

public void setENERGIA(String eNERGIA) {
	if(eNERGIA!= null){
		eNERGIA = eNERGIA.toUpperCase();
	}
	ENERGIA = Util.validateNull(eNERGIA);
}

public void setDIA_INI_LETRAS(String dIA_INI_LETRAS) {
	DIA_INI_LETRAS = Util.validateNull(dIA_INI_LETRAS);
}

public void setDIA_FIN_LETRAS(String dIA_FIN_LETRAS) {
	DIA_FIN_LETRAS = Util.validateNull(dIA_FIN_LETRAS);
}

public void setFI_MES_LETRAS(String fI_MES_LETRAS) {
	FI_MES_LETRAS = Util.validateNull(fI_MES_LETRAS);
}

public void setNCONTRATO(String nCONTRATO) {
     this.NCONTRATO = Util.validateNull(nCONTRATO);
}

public String getNUM_APTO()
{
     return this.NUM_APTO;
}

public void setNUM_APTO(String nUM_APTO) {
     this.NUM_APTO = Util.validateNull(nUM_APTO);
}

public String getCLAUSULA() {
     return this.CLAUSULA;
}

public void setCLAUSULA(String cLAUSULA) {
	if(cLAUSULA != null && cLAUSULA.length()<=1){
		 this.CLAUSULA = null;
	}else{
     this.CLAUSULA = Util.validateNull(cLAUSULA);
	}
}

public String getLINEATEL() {
	return LINEATEL;
}

public void setLINEATEL(String lINEATEL) {
	if(lINEATEL == null || lINEATEL.toUpperCase().contains("NO")){
		LINEATEL = "NO";
	}else {
		LINEATEL = lINEATEL.toUpperCase();
	}
}

public String getBANCO() {
     return this.BANCO;
}

public String getFF_DIA() {
	return FF_DIA;
}

public void setFF_DIA(String fF_DIA) {
	 if (fF_DIA != null) {
	       this.DIA_FIN_LETRAS = Util.convertNumberToWords(fF_DIA).toUpperCase();
	     }
	FF_DIA = fF_DIA;
}

public String getFF_MES() {
	return FF_MES;
}

public void setFF_MES(String fF_MES) {
	FF_MES = Util.validateNull(fF_MES);
}

public String getFF_MES_LETRAS() {
	return FF_MES_LETRAS;
}

public void setFF_MES_LETRAS(String fF_MES_LETRAS) {
	FF_MES_LETRAS = Util.validateNull(fF_MES_LETRAS);
}

public String getFF_ANO() {
	return FF_ANO;
}

public void setFF_ANO(String fF_ANO) {
	FF_ANO = Util.validateNull(fF_ANO);
}

public void setBANCO(String bANCO) {
     this.BANCO = Util.validateNull(bANCO);
}

public String getNUM_CUENTA() {
     return this.NUM_CUENTA;
}

public void setNUM_CUENTA(String nUM_CUENTA) {
     this.NUM_CUENTA = Util.validateNull(nUM_CUENTA);
}

public String getTIPO_CUENTA() {
     return this.TIPO_CUENTA;
}

public void setTIPO_CUENTA(String tIPO_CUENTA) {
     this.TIPO_CUENTA = Util.validateNull(tIPO_CUENTA);
}

public String getTITULAR_CUENTA() {
     return this.TITULAR_CUENTA;
}

public void setTITULAR_CUENTA(String tITULAR_CUENTA) {
     this.TITULAR_CUENTA = Util.validateNull(tITULAR_CUENTA);
}

public String getTIPO_DOC_TITULAR() {
     return this.TIPO_DOC_TITULAR;
}

public void setTIPO_DOC_TITULAR(String tIPO_DOC_TITULAR) {
     this.TIPO_DOC_TITULAR = Util.validateNull(tIPO_DOC_TITULAR);
}

public String getDOC_TITULAR() {
     return this.DOC_TITULAR;
}

public void setDOC_TITULAR(String dOC_TITULAR) {
     this.DOC_TITULAR = Util.validateNull(dOC_TITULAR);
}

public String getELABORADO_POR_ID() {
     return this.ELABORADO_POR_ID;
}

public void setELABORADO_POR_ID(String eLABORADO_POR_ID) {
     this.ELABORADO_POR_ID = Util.validateNull(eLABORADO_POR_ID);
}

public String getVIGENCIA() {
     return this.VIGENCIA;
}

public String getFI_DIA() {
     return this.FI_DIA;
}

public void setFI_DIA(String fI_DIA) {
     if (fI_DIA != null) {
       this.DIA_INI_LETRAS = Util.convertNumberToWords(fI_DIA).toUpperCase();
     }
     this.FI_DIA = Util.validateNull(fI_DIA);
}

public String getFI_MES() {
     return this.FI_MES;
}

public void setFI_MES(String fI_MES) {
     this.FI_MES = Util.validateNull(fI_MES);
}

public String getFI_ANO() {
     return this.FI_ANO;
}

public void setFI_ANO(String fI_ANO) {
     this.FI_ANO = Util.validateNull(fI_ANO);
}

public void setVIGENCIA(String vIGENCIA) {
     this.VIGENCIA = Util.validateNull(vIGENCIA);
}

public String getFECHA_PAGO() {
     return this.FECHA_PAGO;
}

public void setFECHA_PAGO(String fECHA_PAGO) {
     this.FECHA_PAGO = Util.validateNull(fECHA_PAGO);
}

public String getTIPO_DOC_DEU1() {
     return this.TIPO_DOC_DEU1;
}

public void setTIPO_DOC_DEU1(String tIPO_DOC_DEU1) {
     this.TIPO_DOC_DEU1 = Util.validateNull(tIPO_DOC_DEU1);
}

public String getMUNICIPIO()
{
     return this.MUNICIPIO;
}

public void setMUNICIPIO(String mUNICIPIO) {
     this.MUNICIPIO = Util.validateNull(mUNICIPIO);
}

public String getTIPO_DOC_DEU2() {
     return this.TIPO_DOC_DEU2;
}

public String getMATR_INMOB_SIGLA()
{
     return this.MATR_INMOB_SIGLA;
}

public void setMATR_INMOB_SIGLA(String mATR_INMOB_SIGLA) {
     this.MATR_INMOB_SIGLA = Util.validateNull(mATR_INMOB_SIGLA);
}

public String getDIR_NOT_DEU1() {
     return this.DIR_NOT_DEU1;
}

public void setDIR_NOT_DEU1(String dIR_NOT_DEU1) {
     this.DIR_NOT_DEU1 = Util.validateNull(dIR_NOT_DEU1);
}

public String getDIR_NOT_DEU2() {
     return this.DIR_NOT_DEU2;
}

public void setDIR_NOT_DEU2(String dIR_NOT_DEU2) {
     this.DIR_NOT_DEU2 = Util.validateNull(dIR_NOT_DEU2);
}

public String getDOC_DEU1() {
     return this.DOC_DEU1;
}

public String getMUNI_DEU1()
{
     return this.MUNI_DEU1;
}

public void setMUNI_DEU1(String mUNI_DEU1) {
     this.MUNI_DEU1 = Util.validateNull(mUNI_DEU1);
}

public String getMUNI_DEU2() {
     return this.MUNI_DEU2;
}

public void setMUNI_DEU2(String mUNI_DEU2) {
     this.MUNI_DEU2 = Util.validateNull(mUNI_DEU2);
}

public String getDIR_DEU2() {
     return this.DIR_DEU2;
}

public void setDIR_DEU2(String dIR_DEU2) {
     this.DIR_DEU2 = Util.validateNull(dIR_DEU2);
}

public void setDOC_DEU1(String dOC_DEU1) {
     this.DOC_DEU1 = Util.setMiles(dOC_DEU1);
}

public String getDOC_DEU2() {
     return this.DOC_DEU2;
}

public void setDOC_DEU2(String dOC_DEU2) {
     this.DOC_DEU2 = Util.setMiles(dOC_DEU2);
}

public void setTIPO_DOC_DEU2(String tIPO_DOC_DEU2) {
     this.TIPO_DOC_DEU2 = Util.validateNull(tIPO_DOC_DEU2);
}

public String getFECHA_INI() {
     return this.FECHA_INI;
}

public void setFECHA_INI(String fECHA_INI) {
     this.FECHA_INI = Util.validateNull(fECHA_INI);
}

public String getFECHA_FIN() {
     return this.FECHA_FIN;
}

public void setFECHA_FIN(String fECHA_FIN) {
     this.FECHA_FIN = Util.validateNull(fECHA_FIN);
}

public String getARRENDATARIO1() {
     return this.ARRENDATARIO1;
}

public void setARRENDATARIO1(String aRRENDATARIO1) {
     this.ARRENDATARIO1 = Util.validateNull(aRRENDATARIO1);
}

public String getDOC_ARR1() {
     return this.DOC_ARR1;
}

public void setDOC_ARR1(String dOC_ARR1) {
     this.DOC_ARR1 = Util.setMiles(dOC_ARR1);
}

public String getDIRECCION() {
     return this.DIRECCION;
}

public void setDIRECCION(String dIRECCION) {
     this.DIRECCION = Util.validateNull(dIRECCION);
}

public String getPARQUEADERO() {
     return this.PARQUEADERO;
}

public void setPARQUEADERO(String pARQUEADERO) {
     this.PARQUEADERO = Util.validateNull(pARQUEADERO);
}

public String getTIPO_DOC_ARR1() {
     return this.TIPO_DOC_ARR1;
}

public void setTIPO_DOC_ARR1(String tIPO_DOC_ARR1) {
     this.TIPO_DOC_ARR1 = Util.validateNull(tIPO_DOC_ARR1);
}

public String getUTIL() {
     return this.UTIL;
}

public void setUTIL(String uTIL) {
     this.UTIL = Util.validateNull(uTIL);
}

public String getURBANIZACION() {
     return this.URBANIZACION;
}

public void setURBANIZACION(String uRBANIZACION) {
	  if (uRBANIZACION == null)
	       this.URBANIZACION = "";
	else
	       this.URBANIZACION = uRBANIZACION;
}

public String getBARRIO()
{
     return this.BARRIO;
}

public void setBARRIO(String bARRIO) {
     this.BARRIO = Util.validateNull(bARRIO);
}

public String getNUM_PUERTA() {
     return this.NUM_PUERTA;
}

public void setNUM_PUERTA(String nUM_PUERTA) {
     this.NUM_PUERTA = Util.validateNull(nUM_PUERTA);
}

public String getNOMENCLATURA() {
     return this.NOMENCLATURA;
}

public void setNOMENCLATURA(String nOMENCLATURA) {
     this.NOMENCLATURA = Util.validateNull(nOMENCLATURA);
}

public String getMATR_INMOB() {
     return this.MATR_INMOB;
}

public void setMATR_INMOB(String mATR_INMOB) {
     this.MATR_INMOB = Util.validateNull(mATR_INMOB);
}

public String getMATR_INMOB_PARQ() {
     return this.MATR_INMOB_PARQ;
}

public void setMATR_INMOB_PARQ(String mATR_INMOB_PARQ) {
     this.MATR_INMOB_PARQ = Util.validateNull(mATR_INMOB_PARQ);
}

public String getFECHA_INI_LARGA() {
     return this.FECHA_INI_LARGA;
}

public void setFECHA_INI_LARGA(String fECHA_INI_LARGA) {
     this.FECHA_INI_LARGA = Util.validateNull(fECHA_INI_LARGA);
}

public String getFECHA_FIN_LARGA() {
     return this.FECHA_FIN_LARGA;
}

public void setFECHA_FIN_LARGA(String fECHA_FIN_LARGA) {
     this.FECHA_FIN_LARGA = Util.validateNull(fECHA_FIN_LARGA);
}

public String getCANON_LETRAS() {
     return this.CANON_LETRAS;
}

public void setCANON_LETRAS(String cANON_LETRAS) {
     this.CANON_LETRAS = Util.validateNull(cANON_LETRAS);
}

public String getCANON() {
     return this.CANON;
}

public void setCANON(String cANON) {
     this.CANON_LETRAS = Util.convertNumberToWords(cANON).toUpperCase();
     this.CANON = Util.validateNull(cANON);
}

public String getDIA_LETRAS() {
     return this.DIA_LETRAS;
}

public void setDIA_LETRAS(String dIA_LETRAS) {
     this.DIA_LETRAS = Util.validateNull(dIA_LETRAS);
}

public String getDIAPAGO() {
     return this.DIAPAGO;
}

public void setDIAPAGO(String dIAPAGO) {
	if (dIAPAGO != null) {
	       this.DIA_LETRAS = Util.convertNumberToWords(dIAPAGO).toUpperCase();
	 }
     this.DIAPAGO = Util.validateNull(dIAPAGO);
}

public String getDIR_NOT_ARR1() {
     return this.DIR_NOT_ARR1;
}

public void setDIR_NOT_ARR1(String dIR_NOT_ARR1) {
     this.DIR_NOT_ARR1 = Util.validateNull(dIR_NOT_ARR1);
}

public String getMAIL_ARR1() {
     return this.MAIL_ARR1;
}

public void setMAIL_ARR1(String mAIL_ARR1) {
     this.MAIL_ARR1 = Util.validateNull(mAIL_ARR1);
}

public String getTEL_ARR1() {
     return this.TEL_ARR1;
}

public void setTEL_ARR1(String tEL_ARR1) {
     this.TEL_ARR1 = Util.validateNull(tEL_ARR1);
}

public String getCEL_ARR1() {
     return this.CEL_ARR1;
}

public void setCEL_ARR1(String cEL_ARR1) {
     this.CEL_ARR1 = Util.validateNull(cEL_ARR1);
}

public String getDIR_DEU1() {
     return this.DIR_DEU1;
}

public void setDIR_DEU1(String dIR_DEU1) {
     this.DIR_DEU1 = Util.validateNull(dIR_DEU1);
}

public String getTEL_DEU1() {
     return this.TEL_DEU1;
}

public void setTEL_DEU1(String tEL_DEU1) {
     this.TEL_DEU1 = Util.validateNull(tEL_DEU1);
}

public String getCEL_DEU1() {
     return this.CEL_DEU1;
}

public void setCEL_DEU1(String cEL_DEU1) {
     this.CEL_DEU1 = Util.validateNull(cEL_DEU1);
}

public String getMAIL_DEU1() {
     return this.MAIL_DEU1;
}

public void setMAIL_DEU1(String mAIL_DEU1) {
     this.MAIL_DEU1 = Util.validateNull(mAIL_DEU1);
}

public String getDEUDOR1() {
     return this.DEUDOR1;
}

public void setDEUDOR1(String dEUDOR1) {
     this.DEUDOR1 = Util.validateNull(dEUDOR1);
}

public String getDEUDOR2() {
     return this.DEUDOR2;
}

public void setDEUDOR2(String dEUDOR2) {
     this.DEUDOR2 = Util.validateNull(dEUDOR2);
}

public String getMAIL_DEU2() {
     return this.MAIL_DEU2;
}

public void setMAIL_DEU2(String mAIL_DEU2) {
     this.MAIL_DEU2 = Util.validateNull(mAIL_DEU2);
}

public String getCEL_DEU2() {
     return this.CEL_DEU2;
}

public void setCEL_DEU2(String cEL_DEU2) {
     this.CEL_DEU2 = Util.validateNull(cEL_DEU2);
}

public String getTEL_DEU2() {
     return this.TEL_DEU2;
}

public void setTEL_DEU2(String tEL_DEU2) {
     this.TEL_DEU2 = Util.validateNull(tEL_DEU2);
}

public void setID(String id) {
     this.id = Util.validateNull(id);
}

public String getID() {
     return this.id;
}

}
