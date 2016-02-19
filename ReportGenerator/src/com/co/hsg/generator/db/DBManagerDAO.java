 package com.co.hsg.generator.db;
 
 import com.co.hsg.generator.bean.ReportField;
 import com.co.hsg.generator.bean.Reports;
 import com.co.hsg.generator.log.LogInfo;
 import com.co.hsg.generator.util.Util;
 import java.sql.Connection;
 import java.sql.Date;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.util.Calendar;
 
 public class DBManagerDAO extends JDBCResourceManager
 {
   public static String actualMeet;
 
   public ReportField getContract(String id, Reports reportType)
   {
     ReportField report = new ReportField();
     Connection conn = null;
     PreparedStatement st = null;
     LogInfo.T("[DBManager] Leyendo Datos del Contrato:: ");
     String sql = "SELECT ac.id,ac.name,ac.created_by,ac.start_date ,ac.end_date, ac.assigned_user_id,concat(u.first_name , ' ', u.last_name) as user,c.* FROM  aos_contracts ac, aos_contracts_cstm c, users u WHERE ac.id = ? AND ac.id = c.id_c AND ac.created_by = u.id ";
     try
     {
       conn = getConnection();
       st = conn.prepareStatement(sql);
       st.setString(1, id);
       ResultSet rs = st.executeQuery();
       if (rs.next())
       {
         report.setID(rs.getString("id"));
         report.setFECHA_INI(rs.getString("start_date"));
         report.setELABORADO_POR(rs.getString("user"));
         report.setELABORADO_POR_ID(rs.getString("created_by"));
         report.setVIGENCIA(rs.getString("duracion_c"));
         report.setCLAUSULA(rs.getString("clausula_adicional_c"));
 
         report.setDIRECCION(rs.getString("direccion_inmueble_c"));
         report.setNUM_APTO(rs.getString("numero_apto_c"));
         report.setURBANIZACION(rs.getString("nombre_unidad_c"));
         report.setBARRIO(rs.getString("barrio_inmueble_c"));
         report.setMUNICIPIO(rs.getString("municipio_inmueble_c"));
         report.setPARQUEADERO(rs.getString("num_parqueadero_c"));
         report.setUTIL(rs.getString("cuarto_util_c"));
         report.setMATR_INMOB(rs.getString("matricula_inmobiliaria_c"));
         report.setCANON(rs.getString("canon_c"));
         report.setLINEATEL(rs.getString("linea_telefonica_c"));
         
         report.setGAS(rs.getString("gas_c"));
         report.setGAS_PIPETA(rs.getString("gas_pipeta_c"));
         report.setAGUA(rs.getString("agua_c"));
         report.setENERGIA(rs.getString("luz_c"));
 
         Date inicio = rs.getDate("start_date");
         Calendar c = Calendar.getInstance();
         c.setTime(inicio);
         report.setFI_DIA(String.valueOf(c.get(5)));
         report.setFI_MES(String.valueOf(c.get(2) + 1));
         report.setFI_MES_LETRAS(Util.getMonthName(c));
         report.setFI_ANO(String.valueOf(c.get(1)));
         
         Date fin = rs.getDate("end_date");
         c.setTime(fin);
         report.setFECHA_FIN(rs.getString("end_date"));
         report.setFF_DIA(String.valueOf(c.get(5)));
         report.setFF_MES(String.valueOf(c.get(2) + 1));
         report.setFF_MES_LETRAS(Util.getMonthName(c));
         report.setFF_ANO(String.valueOf(c.get(1)));
         
         switch (reportType)
         {
         case CONTRATO_ARRENDAMIENTO:
          
           report.setDIAPAGO(rs.getString("dia_fecha_pago_c"));
           report.setARRENDATARIO1(rs.getString("nombre_inquilino_c"));
           report.setTIPO_DOC_ARR1(rs.getString("tipo_documento_inquilino_c"));
           report.setDOC_ARR1(rs.getString("documento_inquilino_c"));
           report.setDIR_NOT_ARR1(rs.getString("direccion_notificacion_inqui_c"));
           report.setDIR_NOT_ARR2(rs.getString("direccion_notifi_inqui2_c"));
           report.setMAIL_ARR1(rs.getString("email_inquilino_c"));
           report.setTEL_ARR1(rs.getString("tel_inquilino_c"));
           report.setCEL_ARR1(rs.getString("celular_inquilino_c"));
 
           report.setDEUDOR1(rs.getString("nombre_deudor_solidario1_c"));
           report.setDOC_DEU1(rs.getString("documento_deudor_solidario1_c"));
           report.setTIPO_DOC_DEU1(rs.getString("tipodoc_deudor_solidario1_c"));
           report.setMAIL_DEU1(rs.getString("email_deudor_solidario1_c"));
           report.setMUNI_DEU1(rs.getString("municipio_deudor_solidario1_c"));
           report.setDIR_DEU1(rs.getString("direccion_deudor_solidario1_c"));
           report.setTEL_DEU1(rs.getString("telefono_deudor_solidario1_c"));
           report.setCEL_DEU1(rs.getString("celular_deudor_solidario1_c"));
           
 
           report.setDEUDOR2(rs.getString("nombre_deudor_solidario2_c"));
           report.setDOC_DEU2(rs.getString("documento_deudor_solidario2_c"));
           report.setTIPO_DOC_DEU2(rs.getString("tipodoc_deudor_solidario2_c"));
           report.setMAIL_DEU2(rs.getString("email_deudor_solidario2_c"));
           report.setMUNI_DEU2(rs.getString("municipio_deudor_solidario2_c"));
           report.setDIR_DEU2(rs.getString("direccion_deudor_solidario2_c"));
           report.setTEL_DEU2(rs.getString("telefono_deudor_solidario2_c"));
           report.setCEL_DEU2(rs.getString("celular_deudor_solidario2_c"));
 
           break;
         case CONTRATO_ADMON_VIVIENDA:
           report.setARRENDATARIO1(rs.getString("name"));
           report.setTIPO_DOC_ARR1(rs.getString("tipo_doc_propietario_c"));
           report.setDOC_ARR1(rs.getString("cedula_c"));
 
           report.setDEUDOR1(rs.getString("nombre_consignante_c"));
           report.setDOC_DEU1(rs.getString("ced_consignante_c"));
           report.setTIPO_DOC_DEU1(rs.getString("tipo_doc_consignante_c"));
           report.setMAIL_DEU1(rs.getString("mail_con_c"));
           report.setMUNI_DEU1(rs.getString("municipio_inmueble_c"));
           report.setTEL_DEU1(rs.getString("telefono_c"));
           report.setCEL_DEU1(rs.getString("celular_c"));
           report.setDIR_NOT_DEU1(rs.getString("direccion_notificacion2_c"));
 
           report.setBANCO(rs.getString("banco_c"));
           report.setNUM_CUENTA(rs.getString("no_cuenta_c"));
           report.setTIPO_CUENTA(rs.getString("tipo_cuenta_c"));
           report.setTITULAR_CUENTA(rs.getString("name"));
           report.setTIPO_DOC_TITULAR(rs.getString("tipo_doc_propietario_c"));
           report.setDOC_TITULAR(rs.getString("cedula_c"));
 
         }
 
       }
 
       return report;
     } catch (Exception e) {
       LogInfo.E("Excepcion:: ");
       LogInfo.E(Util.errorToString(e));
       closeResources();
     }
     return null;
   }
 
   public void saveFile(String fileID, ReportField report, String fileName, String reportName)
     throws Exception
   {
     LogInfo.T("[DBManager] Insertando Archivo en Registro de Sugar:: ");
 
     Connection conn = null;
     PreparedStatement st = null;
 
     String dnow = Util.getInitDateDBSugar();
     dnow = "STR_TO_DATE( '" + dnow + "','%m-%d-%Y %H:%i:%s')";
 
     String sql = "INSERT INTO notes (assigned_user_id,id,modified_user_id, created_by,name,file_mime_type,filename,parent_type,parent_id ,portal_flag,embed_flag,date_entered,date_modified,deleted) VALUES (?,?,?,?,?,?,?,?,?,0,0," + 
       dnow + "," + dnow + ",0)";
 
     conn = getConnection();
     st = conn.prepareStatement(sql);
     st.setString(1, report.getELABORADO_POR_ID());
     st.setString(2, fileID);
     st.setString(3, report.getELABORADO_POR_ID());
     st.setString(4, report.getELABORADO_POR_ID());
     st.setString(5, reportName);
     st.setString(6, "application/pdf");
     st.setString(7, fileName);
     st.setString(8, "AOS_Contracts");
     st.setString(9, report.getID());
 
     int i = st.executeUpdate();
     if (i > 0)
       LogInfo.T("[DBManager] Nota insertada ");
     else
       LogInfo.T("[DBManager] Nota no insertada");
   }
 }
