/*     */ package com.co.hsg.generator.db;
/*     */ 
/*     */ import com.co.hsg.generator.bean.ReportField;
/*     */ import com.co.hsg.generator.bean.Reports;
/*     */ import com.co.hsg.generator.log.LogInfo;
/*     */ import com.co.hsg.generator.util.Util;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class DBManagerDAO extends JDBCResourceManager
/*     */ {
/*     */   public static String actualMeet;
/*     */ 
/*     */   public ReportField getContract(String id, Reports reportType)
/*     */   {
/*  24 */     ReportField report = new ReportField();
/*  25 */     Connection conn = null;
/*  26 */     PreparedStatement st = null;
/*  27 */     LogInfo.T("[DBManager] Leyendo Datos del Contrato:: ");
/*  28 */     String sql = "SELECT ac.id,ac.name,ac.created_by,ac.start_date ,ac.end_date, ac.assigned_user_id,concat(u.first_name , ' ', u.last_name) as user,c.* FROM  aos_contracts ac, aos_contracts_cstm c, users u WHERE ac.id = ? AND ac.id = c.id_c AND ac.created_by = u.id ";
/*     */     try
/*     */     {
/*  36 */       conn = getConnection();
/*  37 */       st = conn.prepareStatement(sql);
/*  38 */       st.setString(1, id);
/*  39 */       ResultSet rs = st.executeQuery();
/*  40 */       if (rs.next())
/*     */       {
/*  42 */         report.setID(rs.getString("id"));
/*  43 */         report.setFECHA_INI(rs.getString("start_date"));
/*  44 */         report.setELABORADO_POR(rs.getString("user"));
/*  45 */         report.setELABORADO_POR_ID(rs.getString("created_by"));
/*  46 */         report.setVIGENCIA(rs.getString("duracion_c"));
/*  47 */         report.setCLAUSULA(rs.getString("clausula_adicional_c"));
/*     */ 
/*  50 */         report.setDIRECCION(rs.getString("direccion_inmueble_c"));
/*  51 */         report.setNUM_APTO(rs.getString("numero_apto_c"));
/*  52 */         report.setURBANIZACION(rs.getString("nombre_unidad_c"));
/*  53 */         report.setBARRIO(rs.getString("barrio_inmueble_c"));
/*  54 */         report.setMUNICIPIO(rs.getString("municipio_inmueble_c"));
/*  55 */         report.setPARQUEADERO(rs.getString("num_parqueadero_c"));
/*  56 */         report.setUTIL(rs.getString("cuarto_util_c"));
/*  57 */         report.setMATR_INMOB(rs.getString("matricula_inmobiliaria_c"));
/*  58 */         report.setCANON(rs.getString("canon_c"));
/*     */ 
/*  60 */         Date inicio = rs.getDate("start_date");
/*  61 */         Calendar c = Calendar.getInstance();
/*  62 */         c.setTime(inicio);
/*  63 */         report.setFI_DIA(String.valueOf(c.get(5)));
/*  64 */         report.setFI_MES(String.valueOf(c.get(2) + 1));
/*  65 */         report.setFI_ANO(String.valueOf(c.get(1)));
/*     */ 
/*  67 */         switch (reportType)
/*     */         {
/*     */         case CONTRATO_ADMON_VIVIENDA:
/*  70 */           report.setFECHA_FIN(rs.getString("end_date"));
/*  71 */           report.setDIAPAGO(rs.getString("dia_fecha_pago_c"));
/*  72 */           report.setARRENDATARIO1(rs.getString("nombre_inquilino_c"));
/*  73 */           report.setTIPO_DOC_ARR1(rs.getString("tipo_documento_inquilino_c"));
/*  74 */           report.setDOC_ARR1(rs.getString("documento_inquilino_c"));
/*     */ 
/*  76 */           report.setDEUDOR1(rs.getString("nombre_deudor_solidario1_c"));
/*  77 */           report.setDOC_DEU1(rs.getString("documento_deudor_solidario1_c"));
/*  78 */           report.setTIPO_DOC_DEU1(rs.getString("tipodoc_deudor_solidario1_c"));
/*  79 */           report.setMAIL_DEU1(rs.getString("email_deudor_solidario1_c"));
/*  80 */           report.setMUNI_DEU1(rs.getString("municipio_deudor_solidario1_c"));
/*  81 */           report.setDIR_DEU1(rs.getString("direccion_deudor_solidario1_c"));
/*     */ 
/*  83 */           report.setDEUDOR2(rs.getString("nombre_deudor_solidario2_c"));
/*  84 */           report.setDOC_DEU2(rs.getString("documento_deudor_solidario2_c"));
/*  85 */           report.setTIPO_DOC_DEU2(rs.getString("tipodoc_deudor_solidario2_c"));
/*  86 */           report.setMAIL_DEU2(rs.getString("email_deudor_solidario2_c"));
/*  87 */           report.setMUNI_DEU2(rs.getString("municipio_deudor_solidario2_c"));
/*  88 */           report.setDIR_DEU2(rs.getString("direccion_deudor_solidario2_c"));
/*     */ 
/*  90 */           break;
/*     */         case CONTRATO_ARRENDAMIENTO:
/*  93 */           report.setARRENDATARIO1(rs.getString("name"));
/*  94 */           report.setTIPO_DOC_ARR1(rs.getString("tipo_doc_propietario_c"));
/*  95 */           report.setDOC_ARR1(rs.getString("cedula_c"));
/*     */ 
/*  97 */           report.setDEUDOR1(rs.getString("nombre_consignante_c"));
/*  98 */           report.setDOC_DEU1(rs.getString("ced_consignante_c"));
/*  99 */           report.setTIPO_DOC_DEU1(rs.getString("tipo_doc_consignante_c"));
/* 100 */           report.setMAIL_DEU1(rs.getString("mail_con_c"));
/* 101 */           report.setMUNI_DEU1(rs.getString("municipio_inmueble_c"));
/* 102 */           report.setTEL_DEU1(rs.getString("telefono_c"));
/* 103 */           report.setCEL_DEU1(rs.getString("celular_c"));
/* 104 */           report.setDIR_NOT_DEU1(rs.getString("direccion_notificacion2_c"));
/*     */ 
/* 106 */           report.setBANCO(rs.getString("banco_c"));
/* 107 */           report.setNUM_CUENTA(rs.getString("no_cuenta_c"));
/* 108 */           report.setTIPO_CUENTA(rs.getString("tipo_cuenta_c"));
/* 109 */           report.setTITULAR_CUENTA(rs.getString("name"));
/* 110 */           report.setTIPO_DOC_TITULAR(rs.getString("tipo_doc_propietario_c"));
/* 111 */           report.setDOC_TITULAR(rs.getString("cedula_c"));
/*     */ 
/* 113 */           report.setFI_MES(Util.getMonthName(c));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 121 */       return report;
/*     */     } catch (Exception e) {
/* 123 */       LogInfo.E("Excepcion:: ");
/* 124 */       LogInfo.E(Util.errorToString(e));
/* 125 */       closeResources();
/*     */     }
/* 127 */     return null;
/*     */   }
/*     */ 
/*     */   public void saveFile(String fileID, ReportField report, String fileName, String reportName)
/*     */     throws Exception
/*     */   {
/* 133 */     LogInfo.T("[DBManager] Insertando Archivo en Registro de Sugar:: ");
/*     */ 
/* 135 */     Connection conn = null;
/* 136 */     PreparedStatement st = null;
/*     */ 
/* 138 */     String dnow = Util.getInitDateDBSugar();
/* 139 */     dnow = "STR_TO_DATE( '" + dnow + "','%m-%d-%Y %H:%i:%s')";
/*     */ 
/* 141 */     String sql = "INSERT INTO notes (assigned_user_id,id,modified_user_id, created_by,name,file_mime_type,filename,parent_type,parent_id ,portal_flag,embed_flag,date_entered,date_modified,deleted) VALUES (?,?,?,?,?,?,?,?,?,0,0," + 
/* 155 */       dnow + "," + dnow + ",0)";
/*     */ 
/* 157 */     conn = getConnection();
/* 158 */     st = conn.prepareStatement(sql);
/* 159 */     st.setString(1, report.getELABORADO_POR_ID());
/* 160 */     st.setString(2, fileID);
/* 161 */     st.setString(3, report.getELABORADO_POR_ID());
/* 162 */     st.setString(4, report.getELABORADO_POR_ID());
/* 163 */     st.setString(5, reportName);
/* 164 */     st.setString(6, "application/pdf");
/* 165 */     st.setString(7, fileName);
/* 166 */     st.setString(8, "AOS_Contracts");
/* 167 */     st.setString(9, report.getID());
/*     */ 
/* 171 */     int i = st.executeUpdate();
/* 172 */     if (i > 0)
/* 173 */       LogInfo.T("[DBManager] Nota insertada ");
/*     */     else
/* 175 */       LogInfo.T("[DBManager] Nota no insertada");
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.db.DBManagerDAO
 * JD-Core Version:    0.6.0
 */