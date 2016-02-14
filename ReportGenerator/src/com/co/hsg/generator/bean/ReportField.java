/*     */ package com.co.hsg.generator.bean;
/*     */ 
/*     */ import com.co.hsg.generator.util.Util;
/*     */ 
/*     */ public class ReportField
/*     */ {
/*     */   private String NCONTRATO;
/*     */   private String ARRENDATARIO1;
/*     */   private String DOC_ARR1;
/*     */   private String DOC_DEU1;
/*     */   private String DOC_DEU2;
/*     */   private String TIPO_DOC_ARR1;
/*     */   private String TIPO_DOC_DEU1;
/*     */   private String TIPO_DOC_DEU2;
/*     */   private String DIRECCION;
/*     */   private String MUNICIPIO;
/*     */   private String PARQUEADERO;
/*     */   private String NUM_APTO;
/*     */   private String UTIL;
/*     */   private String URBANIZACION;
/*     */   private String BARRIO;
/*     */   private String NUM_PUERTA;
/*     */   private String NOMENCLATURA;
/*     */   private String MATR_INMOB;
/*     */   private String MATR_INMOB_PARQ;
/*     */   private String FECHA_INI_LARGA;
/*     */   private String FECHA_FIN_LARGA;
/*     */   private String CANON_LETRAS;
/*     */   private String CANON;
/*     */   private String DIA_LETRAS;
/*     */   private String DIAPAGO;
/*     */   private String DIR_NOT_ARR1;
/*     */   private String MAIL_ARR1;
/*     */   private String TEL_ARR1;
/*     */   private String CEL_ARR1;
/*     */   private String DIR_DEU1;
/*     */   private String DIR_DEU2;
/*     */   private String TEL_DEU1;
/*     */   private String CEL_DEU1;
/*     */   private String MAIL_DEU1;
/*     */   private String DEUDOR1;
/*     */   private String DEUDOR2;
/*     */   private String MAIL_DEU2;
/*     */   private String CEL_DEU2;
/*     */   private String TEL_DEU2;
/*     */   private String FECHA_INI;
/*     */   private String FECHA_FIN;
/*     */   private String MUNI_DEU1;
/*     */   private String MUNI_DEU2;
/*     */   private String DIR_NOT_DEU1;
/*     */   private String DIR_NOT_DEU2;
/*     */   private String MATR_INMOB_SIGLA;
/*     */   private String FECHA_PAGO;
/*     */   private String VIGENCIA;
/*     */   private String ELABORADO_POR;
/*     */   private String ELABORADO_POR_ID;
/*     */   private String FI_DIA;
/*     */   private String FI_MES;
/*     */   private String FI_ANO;
/*     */   private String id;
/*     */   private String CLAUSULA;
/*     */   private String BANCO;
/*     */   private String NUM_CUENTA;
/*     */   private String TIPO_CUENTA;
/*     */   private String TITULAR_CUENTA;
/*     */   private String TIPO_DOC_TITULAR;
/*     */   private String DOC_TITULAR;
/*     */ 
/*     */   public String getELABORADO_POR()
/*     */   {
/*  46 */     return this.ELABORADO_POR;
/*     */   }
/*     */ 
/*     */   public void setELABORADO_POR(String eLABORADO_POR) {
/*  50 */     this.ELABORADO_POR = eLABORADO_POR;
/*     */   }
/*     */ 
/*     */   public String getNCONTRATO()
/*     */   {
/*  57 */     return this.NCONTRATO;
/*     */   }
/*     */ 
/*     */   public void setNCONTRATO(String nCONTRATO) {
/*  61 */     this.NCONTRATO = nCONTRATO;
/*     */   }
/*     */ 
/*     */   public String getNUM_APTO()
/*     */   {
/*  66 */     return this.NUM_APTO;
/*     */   }
/*     */ 
/*     */   public void setNUM_APTO(String nUM_APTO) {
/*  70 */     this.NUM_APTO = nUM_APTO;
/*     */   }
/*     */ 
/*     */   public String getCLAUSULA() {
/*  74 */     return this.CLAUSULA;
/*     */   }
/*     */ 
/*     */   public void setCLAUSULA(String cLAUSULA) {
/*  78 */     this.CLAUSULA = cLAUSULA;
/*     */   }
/*     */ 
/*     */   public String getBANCO() {
/*  82 */     return this.BANCO;
/*     */   }
/*     */ 
/*     */   public void setBANCO(String bANCO) {
/*  86 */     this.BANCO = bANCO;
/*     */   }
/*     */ 
/*     */   public String getNUM_CUENTA() {
/*  90 */     return this.NUM_CUENTA;
/*     */   }
/*     */ 
/*     */   public void setNUM_CUENTA(String nUM_CUENTA) {
/*  94 */     this.NUM_CUENTA = nUM_CUENTA;
/*     */   }
/*     */ 
/*     */   public String getTIPO_CUENTA() {
/*  98 */     return this.TIPO_CUENTA;
/*     */   }
/*     */ 
/*     */   public void setTIPO_CUENTA(String tIPO_CUENTA) {
/* 102 */     this.TIPO_CUENTA = tIPO_CUENTA;
/*     */   }
/*     */ 
/*     */   public String getTITULAR_CUENTA() {
/* 106 */     return this.TITULAR_CUENTA;
/*     */   }
/*     */ 
/*     */   public void setTITULAR_CUENTA(String tITULAR_CUENTA) {
/* 110 */     this.TITULAR_CUENTA = tITULAR_CUENTA;
/*     */   }
/*     */ 
/*     */   public String getTIPO_DOC_TITULAR() {
/* 114 */     return this.TIPO_DOC_TITULAR;
/*     */   }
/*     */ 
/*     */   public void setTIPO_DOC_TITULAR(String tIPO_DOC_TITULAR) {
/* 118 */     this.TIPO_DOC_TITULAR = tIPO_DOC_TITULAR;
/*     */   }
/*     */ 
/*     */   public String getDOC_TITULAR() {
/* 122 */     return this.DOC_TITULAR;
/*     */   }
/*     */ 
/*     */   public void setDOC_TITULAR(String dOC_TITULAR) {
/* 126 */     this.DOC_TITULAR = dOC_TITULAR;
/*     */   }
/*     */ 
/*     */   public String getELABORADO_POR_ID() {
/* 130 */     return this.ELABORADO_POR_ID;
/*     */   }
/*     */ 
/*     */   public void setELABORADO_POR_ID(String eLABORADO_POR_ID) {
/* 134 */     this.ELABORADO_POR_ID = eLABORADO_POR_ID;
/*     */   }
/*     */ 
/*     */   public String getVIGENCIA() {
/* 138 */     return this.VIGENCIA;
/*     */   }
/*     */ 
/*     */   public String getFI_DIA() {
/* 142 */     return this.FI_DIA;
/*     */   }
/*     */ 
/*     */   public void setFI_DIA(String fI_DIA) {
/* 146 */     if (fI_DIA != null) {
/* 147 */       this.DIA_LETRAS = Util.convertNumberToWords(fI_DIA).toUpperCase();
/*     */     }
/* 149 */     this.FI_DIA = fI_DIA;
/*     */   }
/*     */ 
/*     */   public String getFI_MES() {
/* 153 */     return this.FI_MES;
/*     */   }
/*     */ 
/*     */   public void setFI_MES(String fI_MES) {
/* 157 */     this.FI_MES = fI_MES;
/*     */   }
/*     */ 
/*     */   public String getFI_ANO() {
/* 161 */     return this.FI_ANO;
/*     */   }
/*     */ 
/*     */   public void setFI_ANO(String fI_ANO) {
/* 165 */     this.FI_ANO = fI_ANO;
/*     */   }
/*     */ 
/*     */   public void setVIGENCIA(String vIGENCIA) {
/* 169 */     this.VIGENCIA = vIGENCIA;
/*     */   }
/*     */ 
/*     */   public String getFECHA_PAGO() {
/* 173 */     return this.FECHA_PAGO;
/*     */   }
/*     */ 
/*     */   public void setFECHA_PAGO(String fECHA_PAGO) {
/* 177 */     this.FECHA_PAGO = fECHA_PAGO;
/*     */   }
/*     */ 
/*     */   public String getTIPO_DOC_DEU1() {
/* 181 */     return this.TIPO_DOC_DEU1;
/*     */   }
/*     */ 
/*     */   public void setTIPO_DOC_DEU1(String tIPO_DOC_DEU1) {
/* 185 */     this.TIPO_DOC_DEU1 = tIPO_DOC_DEU1;
/*     */   }
/*     */ 
/*     */   public String getMUNICIPIO()
/*     */   {
/* 190 */     return this.MUNICIPIO;
/*     */   }
/*     */ 
/*     */   public void setMUNICIPIO(String mUNICIPIO) {
/* 194 */     this.MUNICIPIO = mUNICIPIO;
/*     */   }
/*     */ 
/*     */   public String getTIPO_DOC_DEU2() {
/* 198 */     return this.TIPO_DOC_DEU2;
/*     */   }
/*     */ 
/*     */   public String getMATR_INMOB_SIGLA()
/*     */   {
/* 203 */     return this.MATR_INMOB_SIGLA;
/*     */   }
/*     */ 
/*     */   public void setMATR_INMOB_SIGLA(String mATR_INMOB_SIGLA) {
/* 207 */     this.MATR_INMOB_SIGLA = mATR_INMOB_SIGLA;
/*     */   }
/*     */ 
/*     */   public String getDIR_NOT_DEU1() {
/* 211 */     return this.DIR_NOT_DEU1;
/*     */   }
/*     */ 
/*     */   public void setDIR_NOT_DEU1(String dIR_NOT_DEU1) {
/* 215 */     this.DIR_NOT_DEU1 = dIR_NOT_DEU1;
/*     */   }
/*     */ 
/*     */   public String getDIR_NOT_DEU2() {
/* 219 */     return this.DIR_NOT_DEU2;
/*     */   }
/*     */ 
/*     */   public void setDIR_NOT_DEU2(String dIR_NOT_DEU2) {
/* 223 */     this.DIR_NOT_DEU2 = dIR_NOT_DEU2;
/*     */   }
/*     */ 
/*     */   public String getDOC_DEU1() {
/* 227 */     return this.DOC_DEU1;
/*     */   }
/*     */ 
/*     */   public String getMUNI_DEU1()
/*     */   {
/* 232 */     return this.MUNI_DEU1;
/*     */   }
/*     */ 
/*     */   public void setMUNI_DEU1(String mUNI_DEU1) {
/* 236 */     this.MUNI_DEU1 = mUNI_DEU1;
/*     */   }
/*     */ 
/*     */   public String getMUNI_DEU2() {
/* 240 */     return this.MUNI_DEU2;
/*     */   }
/*     */ 
/*     */   public void setMUNI_DEU2(String mUNI_DEU2) {
/* 244 */     this.MUNI_DEU2 = mUNI_DEU2;
/*     */   }
/*     */ 
/*     */   public String getDIR_DEU2() {
/* 248 */     return this.DIR_DEU2;
/*     */   }
/*     */ 
/*     */   public void setDIR_DEU2(String dIR_DEU2) {
/* 252 */     this.DIR_DEU2 = dIR_DEU2;
/*     */   }
/*     */ 
/*     */   public void setDOC_DEU1(String dOC_DEU1) {
/* 256 */     this.DOC_DEU1 = dOC_DEU1;
/*     */   }
/*     */ 
/*     */   public String getDOC_DEU2() {
/* 260 */     return this.DOC_DEU2;
/*     */   }
/*     */ 
/*     */   public void setDOC_DEU2(String dOC_DEU2) {
/* 264 */     this.DOC_DEU2 = dOC_DEU2;
/*     */   }
/*     */ 
/*     */   public void setTIPO_DOC_DEU2(String tIPO_DOC_DEU2) {
/* 268 */     this.TIPO_DOC_DEU2 = tIPO_DOC_DEU2;
/*     */   }
/*     */ 
/*     */   public String getFECHA_INI() {
/* 272 */     return this.FECHA_INI;
/*     */   }
/*     */ 
/*     */   public void setFECHA_INI(String fECHA_INI) {
/* 276 */     this.FECHA_INI = fECHA_INI;
/*     */   }
/*     */ 
/*     */   public String getFECHA_FIN() {
/* 280 */     return this.FECHA_FIN;
/*     */   }
/*     */ 
/*     */   public void setFECHA_FIN(String fECHA_FIN) {
/* 284 */     this.FECHA_FIN = fECHA_FIN;
/*     */   }
/*     */ 
/*     */   public String getARRENDATARIO1() {
/* 288 */     return this.ARRENDATARIO1;
/*     */   }
/*     */ 
/*     */   public void setARRENDATARIO1(String aRRENDATARIO1) {
/* 292 */     this.ARRENDATARIO1 = aRRENDATARIO1;
/*     */   }
/*     */ 
/*     */   public String getDOC_ARR1() {
/* 296 */     return this.DOC_ARR1;
/*     */   }
/*     */ 
/*     */   public void setDOC_ARR1(String dOC_ARR1) {
/* 300 */     this.DOC_ARR1 = dOC_ARR1;
/*     */   }
/*     */ 
/*     */   public String getDIRECCION() {
/* 304 */     return this.DIRECCION;
/*     */   }
/*     */ 
/*     */   public void setDIRECCION(String dIRECCION) {
/* 308 */     this.DIRECCION = dIRECCION;
/*     */   }
/*     */ 
/*     */   public String getPARQUEADERO() {
/* 312 */     return this.PARQUEADERO;
/*     */   }
/*     */ 
/*     */   public void setPARQUEADERO(String pARQUEADERO) {
/* 316 */     this.PARQUEADERO = pARQUEADERO;
/*     */   }
/*     */ 
/*     */   public String getTIPO_DOC_ARR1() {
/* 320 */     return this.TIPO_DOC_ARR1;
/*     */   }
/*     */ 
/*     */   public void setTIPO_DOC_ARR1(String tIPO_DOC_ARR1) {
/* 324 */     this.TIPO_DOC_ARR1 = tIPO_DOC_ARR1;
/*     */   }
/*     */ 
/*     */   public String getUTIL() {
/* 328 */     return this.UTIL;
/*     */   }
/*     */ 
/*     */   public void setUTIL(String uTIL) {
/* 332 */     this.UTIL = uTIL;
/*     */   }
/*     */ 
/*     */   public String getURBANIZACION() {
/* 336 */     return this.URBANIZACION;
/*     */   }
/*     */ 
/*     */   public void setURBANIZACION(String uRBANIZACION) {
/* 340 */     if (uRBANIZACION == null)
/* 341 */       this.URBANIZACION = "";
/*     */     else
/* 343 */       this.URBANIZACION = uRBANIZACION;
/*     */   }
/*     */ 
/*     */   public String getBARRIO()
/*     */   {
/* 348 */     return this.BARRIO;
/*     */   }
/*     */ 
/*     */   public void setBARRIO(String bARRIO) {
/* 352 */     this.BARRIO = bARRIO;
/*     */   }
/*     */ 
/*     */   public String getNUM_PUERTA() {
/* 356 */     return this.NUM_PUERTA;
/*     */   }
/*     */ 
/*     */   public void setNUM_PUERTA(String nUM_PUERTA) {
/* 360 */     this.NUM_PUERTA = nUM_PUERTA;
/*     */   }
/*     */ 
/*     */   public String getNOMENCLATURA() {
/* 364 */     return this.NOMENCLATURA;
/*     */   }
/*     */ 
/*     */   public void setNOMENCLATURA(String nOMENCLATURA) {
/* 368 */     this.NOMENCLATURA = nOMENCLATURA;
/*     */   }
/*     */ 
/*     */   public String getMATR_INMOB() {
/* 372 */     return this.MATR_INMOB;
/*     */   }
/*     */ 
/*     */   public void setMATR_INMOB(String mATR_INMOB) {
/* 376 */     this.MATR_INMOB = mATR_INMOB;
/*     */   }
/*     */ 
/*     */   public String getMATR_INMOB_PARQ() {
/* 380 */     return this.MATR_INMOB_PARQ;
/*     */   }
/*     */ 
/*     */   public void setMATR_INMOB_PARQ(String mATR_INMOB_PARQ) {
/* 384 */     this.MATR_INMOB_PARQ = mATR_INMOB_PARQ;
/*     */   }
/*     */ 
/*     */   public String getFECHA_INI_LARGA() {
/* 388 */     return this.FECHA_INI_LARGA;
/*     */   }
/*     */ 
/*     */   public void setFECHA_INI_LARGA(String fECHA_INI_LARGA) {
/* 392 */     this.FECHA_INI_LARGA = fECHA_INI_LARGA;
/*     */   }
/*     */ 
/*     */   public String getFECHA_FIN_LARGA() {
/* 396 */     return this.FECHA_FIN_LARGA;
/*     */   }
/*     */ 
/*     */   public void setFECHA_FIN_LARGA(String fECHA_FIN_LARGA) {
/* 400 */     this.FECHA_FIN_LARGA = fECHA_FIN_LARGA;
/*     */   }
/*     */ 
/*     */   public String getCANON_LETRAS() {
/* 404 */     return this.CANON_LETRAS;
/*     */   }
/*     */ 
/*     */   public void setCANON_LETRAS(String cANON_LETRAS) {
/* 408 */     this.CANON_LETRAS = cANON_LETRAS;
/*     */   }
/*     */ 
/*     */   public String getCANON() {
/* 412 */     return this.CANON;
/*     */   }
/*     */ 
/*     */   public void setCANON(String cANON) {
/* 416 */     this.CANON_LETRAS = Util.convertNumberToWords(cANON).toUpperCase();
/* 417 */     this.CANON = cANON;
/*     */   }
/*     */ 
/*     */   public String getDIA_LETRAS() {
/* 421 */     return this.DIA_LETRAS;
/*     */   }
/*     */ 
/*     */   public void setDIA_LETRAS(String dIA_LETRAS) {
/* 425 */     this.DIA_LETRAS = dIA_LETRAS;
/*     */   }
/*     */ 
/*     */   public String getDIAPAGO() {
/* 429 */     return this.DIAPAGO;
/*     */   }
/*     */ 
/*     */   public void setDIAPAGO(String dIAPAGO) {
/* 433 */     this.DIA_LETRAS = Util.convertNumberToWords(dIAPAGO).toUpperCase();
/* 434 */     this.DIAPAGO = dIAPAGO;
/*     */   }
/*     */ 
/*     */   public String getDIR_NOT_ARR1() {
/* 438 */     return this.DIR_NOT_ARR1;
/*     */   }
/*     */ 
/*     */   public void setDIR_NOT_ARR1(String dIR_NOT_ARR1) {
/* 442 */     this.DIR_NOT_ARR1 = dIR_NOT_ARR1;
/*     */   }
/*     */ 
/*     */   public String getMAIL_ARR1() {
/* 446 */     return this.MAIL_ARR1;
/*     */   }
/*     */ 
/*     */   public void setMAIL_ARR1(String mAIL_ARR1) {
/* 450 */     this.MAIL_ARR1 = mAIL_ARR1;
/*     */   }
/*     */ 
/*     */   public String getTEL_ARR1() {
/* 454 */     return this.TEL_ARR1;
/*     */   }
/*     */ 
/*     */   public void setTEL_ARR1(String tEL_ARR1) {
/* 458 */     this.TEL_ARR1 = tEL_ARR1;
/*     */   }
/*     */ 
/*     */   public String getCEL_ARR1() {
/* 462 */     return this.CEL_ARR1;
/*     */   }
/*     */ 
/*     */   public void setCEL_ARR1(String cEL_ARR1) {
/* 466 */     this.CEL_ARR1 = cEL_ARR1;
/*     */   }
/*     */ 
/*     */   public String getDIR_DEU1() {
/* 470 */     return this.DIR_DEU1;
/*     */   }
/*     */ 
/*     */   public void setDIR_DEU1(String dIR_DEU1) {
/* 474 */     this.DIR_DEU1 = dIR_DEU1;
/*     */   }
/*     */ 
/*     */   public String getTEL_DEU1() {
/* 478 */     return this.TEL_DEU1;
/*     */   }
/*     */ 
/*     */   public void setTEL_DEU1(String tEL_DEU1) {
/* 482 */     this.TEL_DEU1 = tEL_DEU1;
/*     */   }
/*     */ 
/*     */   public String getCEL_DEU1() {
/* 486 */     return this.CEL_DEU1;
/*     */   }
/*     */ 
/*     */   public void setCEL_DEU1(String cEL_DEU1) {
/* 490 */     this.CEL_DEU1 = cEL_DEU1;
/*     */   }
/*     */ 
/*     */   public String getMAIL_DEU1() {
/* 494 */     return this.MAIL_DEU1;
/*     */   }
/*     */ 
/*     */   public void setMAIL_DEU1(String mAIL_DEU1) {
/* 498 */     this.MAIL_DEU1 = mAIL_DEU1;
/*     */   }
/*     */ 
/*     */   public String getDEUDOR1() {
/* 502 */     return this.DEUDOR1;
/*     */   }
/*     */ 
/*     */   public void setDEUDOR1(String dEUDOR1) {
/* 506 */     this.DEUDOR1 = dEUDOR1;
/*     */   }
/*     */ 
/*     */   public String getDEUDOR2() {
/* 510 */     return this.DEUDOR2;
/*     */   }
/*     */ 
/*     */   public void setDEUDOR2(String dEUDOR2) {
/* 514 */     this.DEUDOR2 = dEUDOR2;
/*     */   }
/*     */ 
/*     */   public String getMAIL_DEU2() {
/* 518 */     return this.MAIL_DEU2;
/*     */   }
/*     */ 
/*     */   public void setMAIL_DEU2(String mAIL_DEU2) {
/* 522 */     this.MAIL_DEU2 = mAIL_DEU2;
/*     */   }
/*     */ 
/*     */   public String getCEL_DEU2() {
/* 526 */     return this.CEL_DEU2;
/*     */   }
/*     */ 
/*     */   public void setCEL_DEU2(String cEL_DEU2) {
/* 530 */     this.CEL_DEU2 = cEL_DEU2;
/*     */   }
/*     */ 
/*     */   public String getTEL_DEU2() {
/* 534 */     return this.TEL_DEU2;
/*     */   }
/*     */ 
/*     */   public void setTEL_DEU2(String tEL_DEU2) {
/* 538 */     this.TEL_DEU2 = tEL_DEU2;
/*     */   }
/*     */ 
/*     */   public void setID(String id) {
/* 542 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getID() {
/* 546 */     return this.id;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.bean.ReportField
 * JD-Core Version:    0.6.0
 */