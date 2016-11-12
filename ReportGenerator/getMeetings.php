<?php

require 'conexion.php';


function getMeeting($userId,$fechaInicio)
{
	$sql = "SELECT meetings.* from meetings_users,meetings  ";
	$sql.= "WHERE (meetings.date_start >= STR_TO_DATE('$fechaInicio','%Y-%m-%d %H:%i:%s') ";
	$sql.= "AND meetings_users.accept_status != 'decline') ";
	$sql.= "AND  meetings_users.meeting_id=meetings.id ";
	$sql.= "AND meetings_users.user_id='$userId' AND meetings.deleted=0 AND meetings_users.deleted=0 ";
	
	return getAdditionalInfoFromBD($sql);
}

function getAdditionalInfoFromBD($sql)
{	
		//Realiza el query en la base de datos
        $mysqli = makeSqlConnection();
		
		$res = $mysqli->query($sql);

        $rows = array();
        while($r = mysqli_fetch_assoc($res))
        {
                //$obj = (object) $r;
                //$a =  (array) $obj;
                $rows[] = $r;
        }

        if( empty( $rows ) )
        {
                return '{"results" :[]}';
        }
        else
        {
                //Convierte el arreglo en json y lo retorna
                $temp = json_encode(utf8ize($rows));
                return '{"results" :'.$temp.'}';
        }
}

function utf8ize($d)
{
	if (is_array($d))
	{
		foreach ($d as $k => $v)
		{
			$d[$k] = utf8ize($v);
		}
	}
	else if (is_string ($d))
	{
		return utf8_encode($d);
	}
	return $d;
}

?>
