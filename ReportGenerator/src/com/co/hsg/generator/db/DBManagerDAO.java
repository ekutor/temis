 package com.co.hsg.generator.db;
 
 import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;

import com.co.hsg.generator.bean.DinamycField;
import com.co.hsg.generator.bean.DinamycReportField;
import com.co.hsg.generator.bean.ReportField;
import com.co.hsg.generator.bean.Reports;
import com.co.hsg.generator.bean.TypeField;
import com.co.hsg.generator.log.LogInfo;
import com.co.hsg.generator.util.Util;
 
 public class DBManagerDAO extends JDBCResourceManager
 {
   private static final String SEPARADOR = ",";
   public static String actualMeet;
   ReportField report;
 
   public ReportField getContract(String id, Reports reportType)
   {
     report = new ReportField();
     Connection conn = null;
     PreparedStatement st = null;
     LogInfo.T("[DBManager] Leyendo Datos del Contrato:: ");
     String sql = "SELECT ac.id,ac.name,ac.description,ac.created_by,ac.start_date ,ac.end_date, " +
     		"ac.assigned_user_id,concat(u.first_name , ' ', u.last_name) as user,c.* FROM  aos_contracts ac, aos_contracts_cstm c, users u WHERE ac.id = ? AND ac.id = c.id_c AND ac.created_by = u.id ";
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
          
         report.setDIRECCION(rs.getString("direccion_inmueble_c"));
         report.setNUM_APTO(rs.getString("numero_apto_c"));
         report.setTIPO_URB(rs.getString("tipo_inmueble_c"));
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
         
         Date fin = rs.getDate("fecha_vencimiento_c");
         c.setTime(fin);
         report.setFECHA_FIN(rs.getString("fecha_vencimiento_c"));
         report.setFF_DIA(String.valueOf(c.get(5)));
         report.setFF_MES(String.valueOf(c.get(2) + 1));
         report.setFF_MES_LETRAS(Util.getMonthName(c));
         report.setFF_ANO(String.valueOf(c.get(1)));
         
         switch (reportType)
         {
//         case CONTRATO_ARRENDAMIENTO_REP_LEGAL:
//        	 report.setREP_LEGAL(rs.getString("nombre_repre_legal_c"));
//        	 report.setDOC_REP_LEGAL(rs.getString("num_doc_repre_legal_c"));
         case CONTRATO_ARRENDAMIENTO_VIVIENDA_DEST_COMERCIAL:
        	 report.setDESTINACION_COMERCIAL(rs.getString("dest_comercial_c"));
         case CONTRATO_ARRENDAMIENTO_COMERCIAL:
        	 report.setDESTINACION_COMERCIAL(rs.getString("dest_comercial_c"));
        	 report.setPORC_AUMENTO(rs.getString("und_mas_ipc_c"));
         case CONTRATO_ARRENDAMIENTO:
           report.setCLAUSULA(rs.getString("clausula_adicional_c"));
           report.setDIAPAGO(rs.getString("dia_fecha_pago_c"));
           
           report.setARRENDATARIO1(getDinamycField(rs.getString("nombre_inquilino_c"),DinamycField.NOMBRE,TypeField.INQUILINOS));
           report.setTIPO_DOC_ARR1(getDinamycField(rs.getString("tipo_documento_inquilino_c"),DinamycField.TIPODOC,TypeField.INQUILINOS));
           report.setDOC_ARR1(getDinamycField(rs.getString("documento_inquilino_c"),DinamycField.NUMDOC,TypeField.INQUILINOS));
           report.setDIR_NOT_ARR1(getDinamycField(rs.getString("direccion_notificacion_inqui_c"),DinamycField.DIRECCION,TypeField.INQUILINOS));
           report.setDIR_NOT_ARR2(getDinamycField(rs.getString("direccion_notifi_inqui2_c"),DinamycField.DIRECCION2,TypeField.INQUILINOS));
           report.setMAIL_ARR1(getDinamycField(rs.getString("email_inquilino_c"),DinamycField.MAIL,TypeField.INQUILINOS));
           report.setTEL_ARR1(getDinamycField(rs.getString("tel_inquilino_c"),DinamycField.TELEFONO,TypeField.INQUILINOS));
           report.setCEL_ARR1(getDinamycField(rs.getString("celular_inquilino_c"),DinamycField.CELULAR,TypeField.INQUILINOS));
           

           report.setDEUDOR1(getDinamycField(rs.getString("nombre_deudor_solidario1_c"),DinamycField.NOMBRE,TypeField.DEUDORES));
           report.setDOC_DEU1(getDinamycField(rs.getString("documento_deudor_solidario1_c"),DinamycField.NUMDOC,TypeField.DEUDORES));
           report.setTIPO_DOC_DEU1(getDinamycField(rs.getString("tipodoc_deudor_solidario1_c"),DinamycField.TIPODOC,TypeField.DEUDORES));
           report.setMAIL_DEU1(getDinamycField(rs.getString("email_deudor_solidario1_c"),DinamycField.MAIL,TypeField.DEUDORES));
           report.setMUNI_DEU1(getDinamycField(rs.getString("municipio_deudor_solidario1_c"),DinamycField.MUNICIPIO,TypeField.DEUDORES));
           report.setDIR_DEU1(getDinamycField(rs.getString("direccion_deudor_solidario1_c"),DinamycField.DIRECCION,TypeField.DEUDORES));
           report.setTEL_DEU1(getDinamycField(rs.getString("telefono_deudor_solidario1_c"),DinamycField.TELEFONO,TypeField.DEUDORES));
           report.setCEL_DEU1(getDinamycField(rs.getString("celular_deudor_solidario1_c"),DinamycField.CELULAR,TypeField.DEUDORES));
           
 
          /* report.setDEUDOR2(rs.getString("nombre_deudor_solidario2_c"));
           report.setDOC_DEU2(rs.getString("documento_deudor_solidario2_c"));
           report.setTIPO_DOC_DEU2(rs.getString("tipodoc_deudor_solidario2_c"));
           report.setMAIL_DEU2(rs.getString("email_deudor_solidario2_c"));
           report.setMUNI_DEU2(rs.getString("municipio_deudor_solidario2_c"));
           report.setDIR_DEU2(rs.getString("direccion_deudor_solidario2_c"));
           report.setTEL_DEU2(rs.getString("telefono_deudor_solidario2_c"));
           report.setCEL_DEU2(rs.getString("celular_deudor_solidario2_c"));*/
 
           break;
         case CONTRATO_ADMON_COMERCIAL:
        	 report.setDESTINACION_COMERCIAL(rs.getString("dest_comercial_c"));
         case CONTRATO_ADMON_VIVIENDA:
        	 
           report.setCOMISION(rs.getString("comision_propietario_c"));
        	 
           //propietarios
           report.setARRENDATARIO1(getDinamycField(rs.getString("name"),DinamycField.NOMBRE,TypeField.INQUILINOS));
           report.setTIPO_DOC_ARR1(getDinamycField(rs.getString("tipo_doc_propietario_c"),DinamycField.TIPODOC,TypeField.INQUILINOS));
           report.setDOC_ARR1(getDinamycField(rs.getString("cedula_c"),DinamycField.NUMDOC,TypeField.INQUILINOS));
           report.setCLAUSULA(rs.getString("description"));
           report.setDIR_NOT_ARR1(getDinamycField(rs.getString("direccion_notificacion_c"),DinamycField.DIRECCION,TypeField.INQUILINOS));
           report.setMAIL_ARR1(getDinamycField(rs.getString("mail_c"),DinamycField.MAIL,TypeField.INQUILINOS));
           report.setTEL_ARR1(getDinamycField(rs.getString("telefono_1_c"),DinamycField.TELEFONO,TypeField.INQUILINOS));
           report.setCEL_ARR1(getDinamycField(rs.getString("celular_c"),DinamycField.CELULAR,TypeField.INQUILINOS));
          
           //consignantes
               
           report.setDEUDOR1(getDinamycField(rs.getString("nombre_consignante_c"),DinamycField.NOMBRE,TypeField.DEUDORES));
           report.setDOC_DEU1(getDinamycField(rs.getString("ced_consignante_c"),DinamycField.NUMDOC,TypeField.DEUDORES));
           report.setTIPO_DOC_DEU1(getDinamycField(rs.getString("tipo_doc_consignante_c"),DinamycField.TIPODOC,TypeField.DEUDORES));
           report.setMAIL_DEU1(getDinamycField(rs.getString("mail_con_c"),DinamycField.MAIL,TypeField.DEUDORES));
           report.setMUNI_DEU1(getDinamycField(rs.getString("municipio_consignante_c"),DinamycField.MUNICIPIO,TypeField.DEUDORES));
           report.setDIR_DEU1(getDinamycField(rs.getString("direccion_notificacion2_c"),DinamycField.DIRECCION,TypeField.DEUDORES));
           report.setTEL_DEU1(getDinamycField(rs.getString("telefono_c"),DinamycField.TELEFONO,TypeField.DEUDORES));
           report.setCEL_DEU1(getDinamycField(rs.getString("celular_c"),DinamycField.CELULAR,TypeField.DEUDORES));
           
 
           report.setBANCO(rs.getString("banco_c"));
           report.setNUM_CUENTA(rs.getString("no_cuenta_c"));
           report.setTIPO_CUENTA(rs.getString("tipo_cuenta_c"));
           report.setTITULAR_CUENTA(rs.getString("beneficiario_c"));
           report.setTIPO_DOC_TITULAR(rs.getString("tipo_doc_beneficiario_c"));
           report.setDOC_TITULAR(rs.getString("ced_beneficiario_c"));
 
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
 
	private String getDinamycField(String infoInLine, DinamycField tipo, TypeField type) {
		List<DinamycReportField> data = null;
		switch(type){
			case DEUDORES:
				data = report.getDeudores();
				break;
			case INQUILINOS:
				data = report.getInquilinos();
				break;
		}
		if (infoInLine != null) {
			String[] arrayFields = infoInLine.split(SEPARADOR);
			for (int i = 0; i < arrayFields.length; i++) {
				DinamycReportField drf = new DinamycReportField();
				if (data.size() > i) {
					drf = data.get(i);
				} else {
					data.add(drf);
				}
				switch(tipo){
					case NUMDOC:
						drf.setDOC_DEU(arrayFields[i]);
						drf.setNUM_DEUDOR(String.valueOf(i+1));
						break;
					case NOMBRE:
						drf.setDEUDOR(arrayFields[i]);
						break;
					case TIPODOC:
						drf.setTIPO_DOC_DEU(arrayFields[i]);
						break;
					case TELEFONO:
						drf.setTEL_DEU(arrayFields[i]);
						break;
					case MAIL:
						drf.setMAIL_DEU(arrayFields[i]);
						break;
					case DIRECCION:
						drf.setDIR_DEU(arrayFields[i]);
						break;
					case DIRECCION2:
						drf.setDIR_DEU2(arrayFields[i]);
						break;
					case MUNICIPIO:
						drf.setMUNI_DEU(arrayFields[i]);
						break;
					case CELULAR:
						drf.setCEL_DEU(arrayFields[i]);
						break;
				}
				

			}
		}
		return infoInLine;
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
