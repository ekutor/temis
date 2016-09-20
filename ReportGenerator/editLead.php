<?php

require 'conexion.php';
require_once('IOManager.php');
require 'utils.php';

function editCliente($modo,$id,$razonSocial,$nombre,$apellidos,$telOficina,$celular,$fax,$cargo,$direccion,$email,$web,$requerimiento,$valorEstimado,
	$retroalimentacion1,$retroalimentacion2,$retroalimentacion3,$valorReal,$departamento,$medio,$marcas,$estado,$accion,$fecha1,$fecha2,$fecha3,$responsable1,$responsable2,$responsable3,
	$campana,$idUsuarioAsignado,$idUsuarioLogueado,$idOpportunity)
{
	date_default_timezone_set('America/Bogota');
	$fecha = date("Y/m/d h:i:s");	
    $log = new IOManager();	
	//Realiza el query en la base de datos
	$mysqli = makeSqlConnection();
	
	
	
	//--Si es un cliente nuevo, lo crea------------------------------------------------------------
	if($modo == 'agregar')
	{			
		$id = md5($razonSocial.$fecha);	
		
		$sql5 = "INSERT INTO leads (id,created_by) VALUES ('$id','$idUsuarioLogueado')";
		$res5 = $mysqli->query($sql5);
		
		if(!$res5)
		{
			$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
			return json_encode($array);
		}
		
		$sql6 = "INSERT INTO leads_cstm (id_c) VALUES ('$id')";
		$res6 = $mysqli->query($sql6);
		
		if(!$res6)
		{
			$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
			return json_encode($array);
		}
		
		//------------------------------------------------------------------------------------------------------------------------------------------
		//Inserta el nuevo email -------------------------------------------------------------------------------------------------------------------
		//------------------------------------------------------------------------------------------------------------------------------------------
		$idEmail = md5($razonSocial.$email.$fecha);
		$str = strtoupper($email);
		
		//Se crea el email
		$sql7 = "INSERT INTO email_addresses (id,email_address,email_address_caps,date_created) 
				VALUES ('$idEmail','$email','$str','$fecha')";
		$res7 = $mysqli->query($sql7);
		
		if(!$res7)
		{
			$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
			return json_encode($array);
		}
		
		
		
		
		//------------------------------------------------------------------------------------------------------------------------------------------
		//Inserta el nuevo email_rel ---------------------------------------------------------------------------------------------------------------
		//------------------------------------------------------------------------------------------------------------------------------------------
		$idBeanRel = md5($idEmail.$id.$fecha);
		
		$sql8 = "INSERT INTO email_addr_bean_rel (id,email_address_id,bean_id,bean_module,primary_address,date_created) 
				VALUES ('$idBeanRel','$idEmail','$id','leads','1','$fecha')";
		$res8 = $mysqli->query($sql8);
		
		if(!$res8)
		{
			$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
			return json_encode($array);
		}
	}	
	
	
	//--Edita la tabla leads-------------------------------------------------------------------
	$sql = "UPDATE leads SET 
	date_modified = '$fecha',
	modified_user_id = '$idUsuarioLogueado', 
	description = '$requerimiento', 
	assigned_user_id = '$idUsuarioAsignado',
	first_name = '$nombre',
	last_name = '$apellidos',
	title = '$cargo',
	department = '$departamento',
	phone_mobile = '$celular',
	phone_work = '$telOficina',
	phone_fax = '$fax',
	primary_address_street = '$direccion',
	lead_source = '$medio',
	status = '$estado',
	status_description = '$retroalimentacion1',
	opportunity_amount = '$valorEstimado',
	campaign_id = '$campana',
	website = '$web' ";
	
	//------------------------------------------------------------------------------------------------------------------------------------------
	//Inserta el opportunity si existe ---------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------------
	if($idOpportunity != '')
	{
		$sql = $sql.",opportunity_id = '$idOpportunity' ";
	}
	
	$sql = $sql." WHERE id = '$id'";
	$log->log($sql);
	$res = $mysqli->query($sql);
		
	if(!$res)
	{
		$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
		return json_encode($array);
	}
	
	
	
	//--Edita la tabla CSTM ------------------------------------------------------------------------------
	$sql2 = "UPDATE leads_cstm SET 
	razonsocial_c = '$razonSocial', 
	accion_c = '$accion', 
	retroalimentacion2_c = '$retroalimentacion2',
	retroalimentacion3_c = '$retroalimentacion3',
	responsable_c = '$responsable1',
	responsable2_c = '$responsable2',
	responsable3_c = '$responsable3',
	medio_c = '$medio',
	valor_real_c = '$valorReal',
	marca_c = '$marcas',
	estado_c = '$estado' ";
	
		
	if ( $fecha1 != 'null' && !empty($fecha1))  
	{	
		$sql2 = $sql2.",fecha_c = STR_TO_DATE('$fecha1','%Y-%m-%d') ";
	}
	if ( $fecha2 != 'null' && !empty($fecha2))  
	{	
		$sql2 = $sql2.",fecha2_c = STR_TO_DATE('$fecha2','%Y-%m-%d') ";
	}
	if ( $fecha3 != 'null' && !empty($fecha3))  
	{	
		$sql2 = $sql2.",fecha3_c = STR_TO_DATE('$fecha3','%Y-%m-%d') ";
	}

	$sql2 = $sql2."WHERE id_c = '$id'";
	
	$res2 = $mysqli->query($sql2);
		
	if(!$res2)
	{
		$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
		return json_encode($array);
	}

		
	//editarEmail($id, $email);	
		
	
		
	if($res && $res2)
	{
		$array = array("respuesta" => "OK" , "id" => $id);
		$log->log("OK");
		return json_encode($array);
	}
	else
	{
		$array = array("respuesta" => "FAIL", "error" => $mysqli->error);
		return json_encode($array);
	}
}



function editarEmail($idAccount,$email)
{
	date_default_timezone_set('America/Bogota');
	$fecha = date("Y/m/d h:i:s");	
		
	//obtengo el id del email
	$mysqli = makeSqlConnection();
	$sql = "SELECT email_address_id FROM email_addr_bean_rel WHERE bean_id = '$idAccount'";
	$res = $mysqli->query($sql);
	
	$idEmail = "";
	
	if($r = mysqli_fetch_assoc($res))
	{
		$obj = (object) $r;	
		$idEmail = $obj->email_address_id;
	}
	
	$str = strtoupper($email);
	
	//edito el email
	$sql2 = "UPDATE email_addresses SET email_address = '$email',email_address_caps = '$str',date_modified = '$fecha' WHERE id = '$idEmail'";
	$res2 = $mysqli->query($sql2);
}

?>
