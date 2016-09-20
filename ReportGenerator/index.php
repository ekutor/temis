<?php

require 'Slim/Slim.php';
require_once('./IOManager.php');

\Slim\Slim::registerAutoloader();


$app = new \Slim\Slim();
$log = new IOManager();

//le dice al cliente que esto retorna datos en formato json
$app->response->headers->set('Content-Type', 'application/json');




//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LOGIN
//--------------------------------------------------------------------------------------------------

$app->get('/loginUsuarios', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/loginUsuarios.php';
	
	$app = new \Slim\Slim();
 	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
		//Obtiene los parametros del header http
	$usuario = $app->request->headers->get('usuario');
	$password = $app->request->headers->get('password');
	
	
	$respuesta = authDevice($usuario,$deviceId );
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	$log = new IOManager();
	$log->logAll($app->request->headers);

	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = loginUsuarios($usuario,$password);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA ACCOUNTS
//--------------------------------------------------------------------------------------------------

$app->get('/getAccounts', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getAccounts.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getAccounts();
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->get('/getAccount', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getAccount.php';
	
	$app = new \Slim\Slim();
 	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idAccount = $app->request->headers->get('idAccount');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getAccount($idAccount);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/newAccount', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/newAccount.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$name = $app->request->headers->get('name');
	$nit = $app->request->headers->get('nit');
	$codigoAlterno = $app->request->headers->get('codigoAlterno');
	$canal = $app->request->headers->get('canal');
	$sector = $app->request->headers->get('sector');
	$telefono1 = $app->request->headers->get('telefono1');
	$ext1 = $app->request->headers->get('extension1');
	$telefono2 = $app->request->headers->get('telefono2');
	$ext2 = $app->request->headers->get('extension2');
	$celular = $app->request->headers->get('celular');
	$fax = $app->request->headers->get('fax');
	$direccion = $app->request->headers->get('direccion');
	$municipio = $app->request->headers->get('municipio');
	$departamento = $app->request->headers->get('departamento');
	$zona = $app->request->headers->get('zona');
	$uen = $app->request->headers->get('uen');
	$email = $app->request->headers->get('email');
	$web = $app->request->headers->get('web');
	$grupo = $app->request->headers->get('grupo');
	$segmento = $app->request->headers->get('segmento');
	$estado = $app->request->headers->get('estado');
	$descuento = $app->request->headers->get('descuento');
	$presupuesto = $app->request->headers->get('presupuesto');
	$descripcion = $app->request->headers->get('descripcion');
	$correoTransporte = $app->request->headers->get('correoTransporte');
	$usuarioAsignado = $app->request->headers->get('usuarioAsignado');
	$usuarioCreador = $app->request->headers->get('usuarioCreador');
	
	$fechaConstitucion = $app->request->headers->get('fechaConstitucion');
	$ventasActual = $app->request->headers->get('ventasActual');
	$ventasAnterior = $app->request->headers->get('ventasAnterior');
	$numeroAlianzas = $app->request->headers->get('numeroAlianzas');
	$alianzas = $app->request->headers->get('alianzas');
	$origenCuenta = $app->request->headers->get('origenCuenta');
	
	$fechaFacturacion = $app->request->headers->get('fechaFacturacion');
	$facturacionDiaria = $app->request->headers->get('facturacionDiaria');
	$facturacionAcumuladaMes = $app->request->headers->get('facturacionAcumuladaMes');
	$porcentajeCumplimiento = $app->request->headers->get('porcentajeCumplimiento');
	$facturacionAutorizada = $app->request->headers->get('facturacionAutorizada');
	$facturacionNoAutorizada = $app->request->headers->get('facturacionNoAutorizada');
	
	$fechaDespacho = $app->request->headers->get('fechaDespacho');
	$remesa = $app->request->headers->get('remesa');
	$destino = $app->request->headers->get('destino');
	$nombreDestinatario = $app->request->headers->get('nombreDestinatario');
	$numeroUnidades = $app->request->headers->get('numeroUnidades');
	$numeroDocumento = $app->request->headers->get('numeroDocumento');
	
	$nombreDestinatario2 = $app->request->headers->get('nombreDestinatario2');
	$destino2 = $app->request->headers->get('destino2');
	$motivo = $app->request->headers->get('motivo');
	
	$cupoDisponible = $app->request->headers->get('cupoDisponible');
	$cupoCr = $app->request->headers->get('cupoCr');
	$totalCartera = $app->request->headers->get('totalCartera');
	$condicionPago = $app->request->headers->get('condicionPago');
	$plazoPago = $app->request->headers->get('plazoPago');
	$promedioPago = $app->request->headers->get('promedioPago');
	$carteraVencida = $app->request->headers->get('carteraVencida');
	$carteraVencer = $app->request->headers->get('carteraVencer');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = newAccount($name,$nit,$codigoAlterno,$canal,$sector,$telefono1,$ext1,$telefono2,$ext2,$celular,$fax,$direccion,$municipio,$departamento,
	$zona,$uen,$email,$web,$grupo,$segmento,$estado,$descuento,$presupuesto,$descripcion,$correoTransporte,$usuarioAsignado,$usuarioCreador,
	$fechaConstitucion,$ventasActual,$ventasAnterior,$numeroAlianzas,$alianzas,$origenCuenta,
	$fechaFacturacion,$facturacionDiaria,$facturacionAcumuladaMes,$porcentajeCumplimiento,$facturacionAutorizada,$facturacionNoAutorizada,
	$fechaDespacho,$remesa,$destino,$nombreDestinatario,$numeroUnidades,$numeroDocumento,
	$nombreDestinatario2,$destino2,$motivo,
	$cupoDisponible,$cupoCr,$totalCartera,$condicionPago,$plazoPago,$promedioPago,$carteraVencida,$carteraVencer);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/editAccount', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editAccount.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('idAccount');
	$name = $app->request->headers->get('name');
	$nit = $app->request->headers->get('nit');
	$codigoAlterno = $app->request->headers->get('codigoAlterno');
	$canal = $app->request->headers->get('canal');
	$sector = $app->request->headers->get('sector');
	$telefono1 = $app->request->headers->get('telefono1');
	$ext1 = $app->request->headers->get('extension1');
	$telefono2 = $app->request->headers->get('telefono2');
	$ext2 = $app->request->headers->get('extension2');
	$celular = $app->request->headers->get('celular');
	$fax = $app->request->headers->get('fax');
	$direccion = $app->request->headers->get('direccion');
	$municipio = $app->request->headers->get('municipio');
	$departamento = $app->request->headers->get('departamento');
	$zona = $app->request->headers->get('zona');
	$uen = $app->request->headers->get('uen');
	$email = $app->request->headers->get('email');
	$web = $app->request->headers->get('web');
	$grupo = $app->request->headers->get('grupo');
	$segmento = $app->request->headers->get('segmento');
	$estado = $app->request->headers->get('estado');
	$descuento = $app->request->headers->get('descuento');
	$presupuesto = $app->request->headers->get('presupuesto');
	$descripcion = $app->request->headers->get('descripcion');
	$correoTransporte = $app->request->headers->get('correoTransporte');
	$usuarioAsignado = $app->request->headers->get('usuarioAsignado');
	$usuarioCreador = $app->request->headers->get('usuarioCreador');
	
	$fechaConstitucion = $app->request->headers->get('fechaConstitucion');
	$ventasActual = $app->request->headers->get('ventasActual');
	$ventasAnterior = $app->request->headers->get('ventasAnterior');
	$numeroAlianzas = $app->request->headers->get('numeroAlianzas');
	$alianzas = $app->request->headers->get('alianzas');
	$origenCuenta = $app->request->headers->get('origenCuenta');
	
	$fechaFacturacion = $app->request->headers->get('fechaFacturacion');
	$facturacionDiaria = $app->request->headers->get('facturacionDiaria');
	$facturacionAcumuladaMes = $app->request->headers->get('facturacionAcumuladaMes');
	$porcentajeCumplimiento = $app->request->headers->get('porcentajeCumplimiento');
	$facturacionAutorizada = $app->request->headers->get('facturacionAutorizada');
	$facturacionNoAutorizada = $app->request->headers->get('facturacionNoAutorizada');
	
	$fechaDespacho = $app->request->headers->get('fechaDespacho');
	$remesa = $app->request->headers->get('remesa');
	$destino = $app->request->headers->get('destino');
	$nombreDestinatario = $app->request->headers->get('nombreDestinatario');
	$numeroUnidades = $app->request->headers->get('numeroUnidades');
	$numeroDocumento = $app->request->headers->get('numeroDocumento');
	
	$nombreDestinatario2 = $app->request->headers->get('nombreDestinatario2');
	$destino2 = $app->request->headers->get('destino2');
	$motivo = $app->request->headers->get('motivo');
	
	$cupoDisponible = $app->request->headers->get('cupoDisponible');
	$cupoCr = $app->request->headers->get('cupoCr');
	$totalCartera = $app->request->headers->get('totalCartera');
	$condicionPago = $app->request->headers->get('condicionPago');
	$plazoPago = $app->request->headers->get('plazoPago');
	$promedioPago = $app->request->headers->get('promedioPago');
	$carteraVencida = $app->request->headers->get('carteraVencida');
	$carteraVencer = $app->request->headers->get('carteraVencer');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editAccount($id,$name,$nit,$codigoAlterno,$canal,$sector,$telefono1,$ext1,$telefono2,$ext2,$celular,$fax,$direccion,$municipio,$departamento,
	$zona,$uen,$email,$web,$grupo,$segmento,$estado,$descuento,$presupuesto,$descripcion,$correoTransporte,$usuarioAsignado,$usuarioCreador,
	$fechaConstitucion,$ventasActual,$ventasAnterior,$numeroAlianzas,$alianzas,$origenCuenta,
	$fechaFacturacion,$facturacionDiaria,$facturacionAcumuladaMes,$porcentajeCumplimiento,$facturacionAutorizada,$facturacionNoAutorizada,
	$fechaDespacho,$remesa,$destino,$nombreDestinatario,$numeroUnidades,$numeroDocumento,
	$nombreDestinatario2,$destino2,$motivo,
	$cupoDisponible,$cupoCr,$totalCartera,$condicionPago,$plazoPago,$promedioPago,$carteraVencida,$carteraVencer);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getAccountContacts', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getAccountContacts.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getAccountContacts($idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getAccountOpportunities', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getAccountOpportunities.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idAccount = $app->request->headers->get('idAccount');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getAccountOpportunities($idAccount);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getAccountCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCall.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idAccount = $app->request->headers->get('idAccount');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getAccountCalls($idAccount);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA CONTACTS
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
$app->get('/getContact', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getContact.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	
	$log = new IOManager();
	$log->logAll($app->request->headers);
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getContact($idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getContacts', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getContacts.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	//$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getContacts();
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getContactsxAccount', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getContacts.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
		$log = new IOManager();
		$log->logAll($app->request->headers);
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        //Obtiene los parametros del header http
        $idAccount = $app->request->headers->get('idAccount');

        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getContactsxAccount($idAccount);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->put('/newContact', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editContact.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	} 	
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('idContact');
	$name = $app->request->headers->get('name');
	$identificacion = $app->request->headers->get('identificacion');
	$cumpleanos = $app->request->headers->get('cumpleanos');
	$genero = $app->request->headers->get('genero');
	$cargo = $app->request->headers->get('cargo');
	$certificaciones = $app->request->headers->get('certificaciones');
	$profesion = $app->request->headers->get('profesion');
	$tipoContacto = $app->request->headers->get('tipoContacto');
	$telefono1 = $app->request->headers->get('telefono1');
	$extension1 = $app->request->headers->get('extension1');
	$telefono2 = $app->request->headers->get('telefono2');
	$extension2 = $app->request->headers->get('extension2');
	$celular = $app->request->headers->get('celular');
	$fax = $app->request->headers->get('fax');
	$email = $app->request->headers->get('email');
	$cuenta = $app->request->headers->get('cuenta');
	$departamento = $app->request->headers->get('departamento');
	$municipio = $app->request->headers->get('municipio');
	$direccion = $app->request->headers->get('direccion');
	
	$segmento = $app->request->headers->get('segmento');
	$grupoObjetivo = $app->request->headers->get('grupoObjetivo');
	$uen = $app->request->headers->get('uen');
	$zona = $app->request->headers->get('zona');
	$canal = $app->request->headers->get('canal');
	$sector = $app->request->headers->get('sector');
	$estado = $app->request->headers->get('estado');
	
	$regalo1 = $app->request->headers->get('regalo1');
	$fechaRegalo1 = $app->request->headers->get('fechaRegalo1');
	$motivoRegalo1 = $app->request->headers->get('motivoRegalo1');
	$regalo2 = $app->request->headers->get('regalo2');
	$fechaRegalo2 = $app->request->headers->get('fechaRegalo2');
	$motivoRegalo2 = $app->request->headers->get('motivoRegalo2');
	$regalo3 = $app->request->headers->get('regalo3');
	$fechaRegalo3 = $app->request->headers->get('fechaRegalo3');
	$motivoRegalo3 = $app->request->headers->get('motivoRegalo3');
	$regalo4 = $app->request->headers->get('regalo4');
	$fechaRegalo4 = $app->request->headers->get('fechaRegalo4');
	$motivoRegalo4 = $app->request->headers->get('motivoRegalo4');
	$regalo5 = $app->request->headers->get('regalo5');
	$fechaRegalo5 = $app->request->headers->get('fechaRegalo5');
	$motivoRegalo5 = $app->request->headers->get('motivoRegalo5');
	
	$informa = $app->request->headers->get('informa');
	$tomaContacto = $app->request->headers->get('tomaContacto');
	$nollamar = $app->request->headers->get('nollamar');
	$campana = $app->request->headers->get('campana');
	$diligenciado = $app->request->headers->get('diligenciado');
	$modificado = $app->request->headers->get('modificado');
	$responsable = $app->request->headers->get('responsable');
	
	$idUsuarioLogueado = $app->request->headers->get('idUsuarioLogueado');
	
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	$modo = 'agregar';
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editContact($modo,$id,$name,$identificacion,$cumpleanos,$genero,$cargo,$certificaciones,$profesion,$tipoContacto,$telefono1,$extension1,$telefono2,$extension2,
	$celular,$fax,$email,$cuenta,$departamento,$municipio,$direccion,$segmento,$grupoObjetivo,$uen,$zona,$canal,$sector,$estado,$regalo1,$fechaRegalo1,$motivoRegalo1,
	$regalo2,$fechaRegalo2,$motivoRegalo2,$regalo3,$fechaRegalo3,$motivoRegalo3,$regalo4,$fechaRegalo4,$motivoRegalo4,$regalo5,$fechaRegalo5,$motivoRegalo5,
	$informa,$tomaContacto,$nollamar,$campana,$diligenciado,$modificado,$responsable,$idUsuarioLogueado,$idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/editContact', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editContact.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('idContact');
	$name = $app->request->headers->get('name');
	$identificacion = $app->request->headers->get('identificacion');
	$cumpleanos = $app->request->headers->get('cumpleanos');
	$genero = $app->request->headers->get('genero');
	$cargo = $app->request->headers->get('cargo');
	$certificaciones = $app->request->headers->get('certificaciones');
	$profesion = $app->request->headers->get('profesion');
	$tipoContacto = $app->request->headers->get('tipoContacto');
	$telefono1 = $app->request->headers->get('telefono1');
	$extension1 = $app->request->headers->get('extension1');
	$telefono2 = $app->request->headers->get('telefono2');
	$extension2 = $app->request->headers->get('extension2');
	$celular = $app->request->headers->get('celular');
	$fax = $app->request->headers->get('fax');
	$email = $app->request->headers->get('email');
	$cuenta = $app->request->headers->get('cuenta');
	$departamento = $app->request->headers->get('departamento');
	$municipio = $app->request->headers->get('municipio');
	$direccion = $app->request->headers->get('direccion');
	
	$segmento = $app->request->headers->get('segmento');
	$grupoObjetivo = $app->request->headers->get('grupoObjetivo');
	$uen = $app->request->headers->get('uen');
	$zona = $app->request->headers->get('zona');
	$canal = $app->request->headers->get('canal');
	$sector = $app->request->headers->get('sector');
	$estado = $app->request->headers->get('estado');
	
	$regalo1 = $app->request->headers->get('regalo1');
	$fechaRegalo1 = $app->request->headers->get('fechaRegalo1');
	$motivoRegalo1 = $app->request->headers->get('motivoRegalo1');
	$regalo2 = $app->request->headers->get('regalo2');
	$fechaRegalo2 = $app->request->headers->get('fechaRegalo2');
	$motivoRegalo2 = $app->request->headers->get('motivoRegalo2');
	$regalo3 = $app->request->headers->get('regalo3');
	$fechaRegalo3 = $app->request->headers->get('fechaRegalo3');
	$motivoRegalo3 = $app->request->headers->get('motivoRegalo3');
	$regalo4 = $app->request->headers->get('regalo4');
	$fechaRegalo4 = $app->request->headers->get('fechaRegalo4');
	$motivoRegalo4 = $app->request->headers->get('motivoRegalo4');
	$regalo5 = $app->request->headers->get('regalo5');
	$fechaRegalo5 = $app->request->headers->get('fechaRegalo5');
	$motivoRegalo5 = $app->request->headers->get('motivoRegalo5');
	
	$informa = $app->request->headers->get('informa');
	$tomaContacto = $app->request->headers->get('tomaContacto');
	$nollamar = $app->request->headers->get('nollamar');
	$campana = $app->request->headers->get('campana');
	$diligenciado = $app->request->headers->get('diligenciado');
	$modificado = $app->request->headers->get('modificado');
	$responsable = $app->request->headers->get('responsable');
	
	$idUsuarioLogueado = $app->request->headers->get('idUsuarioLogueado');
	
	$modo = 'editar';
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editContact($modo,$id,$name,$identificacion,$cumpleanos,$genero,$cargo,$certificaciones,$profesion,$tipoContacto,$telefono1,$extension1,$telefono2,$extension2,
	$celular,$fax,$email,$cuenta,$departamento,$municipio,$direccion,$segmento,$grupoObjetivo,$uen,$zona,$canal,$sector,$estado,$regalo1,$fechaRegalo1,$motivoRegalo1,
	$regalo2,$fechaRegalo2,$motivoRegalo2,$regalo3,$fechaRegalo3,$motivoRegalo3,$regalo4,$fechaRegalo4,$motivoRegalo4,$regalo5,$fechaRegalo5,$motivoRegalo5,
	$informa,$tomaContacto,$nollamar,$campana,$diligenciado,$modificado,$responsable,$idUsuarioLogueado);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getContactTasks', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getContactTasks.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getContactTasks($idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getContactOpportunities', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getContactOpportunities.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getContactOpportunities($idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getContactCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCall.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idContact = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getContactCalls($idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getOpprtunityCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCall.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpprtunityCalls($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------




//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA OPPORTUNITIES
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
$app->get('/getOpportunities', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunities.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	//$idContact = $app->request->headers->get('idContact');
	$currentUser = $app->request->headers->get('currentUser');
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunities($currentUser);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getOpportunity', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunity.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunity($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/editOpportunity', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editOpportunity.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('idOpportunity');
	$name = $app->request->headers->get('name');
	$usuarioFinal = $app->request->headers->get('usuarioFinal');
	$valorEstimado = $app->request->headers->get('valorEstimado');
	$probabilidad = $app->request->headers->get('probabilidad');
	$proximoPaso = $app->request->headers->get('proximoPaso');
	$descripcion = $app->request->headers->get('descripcion');
	$tipoProyecto = $app->request->headers->get('tipoProyecto');
	$etapa = $app->request->headers->get('etapa');
	$idCuenta = $app->request->headers->get('idCuenta');
	$fechaCierre = $app->request->headers->get('fechaCierre');
	$idCampana = $app->request->headers->get('idCampana');
	$medio = $app->request->headers->get('medio');
	$idUsuarioAsignado = $app->request->headers->get('idUsuarioAsignado');
	$marcasEnergia = $app->request->headers->get('marcasEnergia');
	$marcasComunicaciones = $app->request->headers->get('marcasComunicaciones');
	$marcasIluminacion = $app->request->headers->get('marcasIluminacion');
	$idUsuarioLogueado = $app->request->headers->get('idUsuarioLogueado');
	
	$modo = 'editar';
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editOpportunity($modo,$id,$name,$usuarioFinal,$valorEstimado,$probabilidad,$proximoPaso,$descripcion,$tipoProyecto,$etapa,$idCuenta,$fechaCierre,
	$idCampana,$medio,$idUsuarioAsignado,$marcasEnergia,$marcasComunicaciones,$marcasIluminacion,$idUsuarioLogueado,'');
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/addOpportunity', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editOpportunity.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$date_entered = $app->request->headers->get('date_entered');
	$energia_c = $app->request->headers->get('energia_c');
	$id_c = $app->request->headers->get('id_c');
	$assigned_user_id = $app->request->headers->get('assigned_user_id');
	$comunicaciones_c = $app->request->headers->get('comunicaciones_c');
	$amount = $app->request->headers->get('amount');
	$amount_usdollar = $app->request->headers->get('amount_usdollar');
	$nameAccount = $app->request->headers->get('nameAccount');
	$description = $app->request->headers->get('description');
	$name = $app->request->headers->get('name');
	$valoroportunidad_c = $app->request->headers->get('valoroportunidad_c');
	$date_closed = $app->request->headers->get('date_closed');
	$probability = $app->request->headers->get('probability');
	$fuente_c = $app->request->headers->get('fuente_c');
	$iluminacion_c = $app->request->headers->get('iluminacion_c');
	$created_by = $app->request->headers->get('created_by');
	$usuario_final_c = $app->request->headers->get('usuario_final_c');
	$currency_id = $app->request->headers->get('currency_id');
	$tipo_c = $app->request->headers->get('tipo_c');
	$date_modified = $app->request->headers->get('date_modified');
	$modified_user_id = $app->request->headers->get('modified_user_id');
	$nameCampaign = $app->request->headers->get('nameCampaign');
	$deleted = $app->request->headers->get('deleted');
	$campaign_id = $app->request->headers->get('campaign_id');
	$lead_source = $app->request->headers->get('lead_source');
	$medio_c = $app->request->headers->get('medio_c');
	$idAccount = $app->request->headers->get('idAccount');
	$opportunity_type = $app->request->headers->get('opportunity_type');
	$assigned_user_name = $app->request->headers->get('assigned_user_name');
	$sales_stage = $app->request->headers->get('sales_stage');
	$next_step = $app->request->headers->get('next_step');
	
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editOpportunity($modo,$id,$date_entered,$energia_c,$id_c,$assigned_user_id,$comunicaciones_c,$amount,$amount_usdollar,$nameAccount,$description,$name,$valoroportunidad_c,$date_closed,$probability,$fuente_c,$iluminacion_c,$created_by,$usuario_final_c,$currency_id,$tipo_c,$date_modified,$modified_user_id,$nameCampaign,$deleted,$campaign_id,$lead_source,$medio_c,$idAccount,$opportunity_type,$assigned_user_name,$sales_stage,$next_step);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getOpportunityTasks', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunityTasks.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunityTasks($idOpportunity);  
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//-------------------------------------------------------------------------------------------------- 

$app->get('/getOpportunityContacts', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunityContacts.php';
	
	$app = new \Slim\Slim();
	
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunityContacts($idOpportunity);  
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getOpportunityClientes', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunityClientes.php';
	
	$app = new \Slim\Slim();
	

	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunityClientes($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getOpportunityCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getOpportunityCalls.php';
	
	$app = new \Slim\Slim();
 	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getOpportunityCalls($idOpportunity);   
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA CALLS
//--------------------------------------------------------------------------------------------------

$app->get('/getCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCalls.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getCalls();
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->get('/getCall', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCall.php';
	
	$app = new \Slim\Slim();
	
	//Obtiene los parametros del header http
	$idCall = $app->request->headers->get('idCall');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getCall($idCall);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->put('/addCall', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editCall.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$asunto = $app->request->headers->get('name');
	$resultado = $app->request->headers->get('resultadodelallamada_c');
	$descripcion = $app->request->headers->get('description');
	$direccion = $app->request->headers->get('direction');
	$estado = $app->request->headers->get('status');
	$fechaInicio = $app->request->headers->get('date_start');
	$duracion_horas = $app->request->headers->get('duration_hours');
	$duracion_min = $app->request->headers->get('duration_minutes');
	$parent_id = $app->request->headers->get('parent_id');
	$parent_type = $app->request->headers->get('parent_type');
	$idCampana = $app->request->headers->get('campaign_id');
	$idUsuario = $app->request->headers->get('created_by');
	$idAsignado = $app->request->headers->get('assigned_user_id');
	$idContact = $app->request->headers->get('contact_id');
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editCall($modo,$id,$asunto,$resultado,$descripcion,$direccion,$estado,$fechaInicio,$duracion_horas,$duracion_min,$parent_id,$parent_type,$idCampana,$idUsuario,$idAsignado, $idContact);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------


// WEBSERVICES DE LA TABLA USERS
//--------------------------------------------------------------------------------------------------

$app->get('/getUsers', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getUsers.php';
	
	$app = new \Slim\Slim();
	
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getUsers();
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});




//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA PRODUCTOS
//--------------------------------------------------------------------------------------------------

$app->get('/getProductos', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getProducto.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$log = new IOManager();
	$log->logAll($app->request->headers);
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	$queryText =  $app->request->headers->get('queryText');
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getProductos($queryText);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->get('/getProducto', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getProducto.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	$log = new IOManager();
	$log->logAll($app->request->headers);
	//Obtiene los parametros del header http
	$idProducto = $app->request->headers->get('idProducto');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getProducto($idProducto);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->put('/editProducto', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editProducto.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idUsuario = $app->request->headers->get('idUsuario');
	$idProducto = $app->request->headers->get('idProducto');
	$codigo = $app->request->headers->get('codigo');
	$nombre = $app->request->headers->get('nombre');
	$referencia = $app->request->headers->get('referencia');
	$marca = $app->request->headers->get('marca');
	$inventario = $app->request->headers->get('inventario');
	$precioPesos = $app->request->headers->get('precioPesos');
	$precioDolares = $app->request->headers->get('precioDolares');
	$grupo = $app->request->headers->get('grupo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editProducto($idUsuario,$idProducto,$codigo,$nombre,$referencia,$marca,$inventario,$precioPesos,$precioDolares,$grupo);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA TASKS (tareas)
//--------------------------------------------------------------------------------------------------

$app->get('/getTasks', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getTasks.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	$currentUser = $app->request->headers->get('currentUser');
	$log = new IOManager();
	$log->logAll($app->request->headers);
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getTasks($currentUser);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->get('/getTaskxAccount', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getTask.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idAccount = $app->request->headers->get('idAccount');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getTaskxAccount($idAccount);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getTaskxOpportunity', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getTask.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getTaskxOpportunity($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getTaskxContact', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getTask.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idContact');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getTaskxContact($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getTask', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getTask.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idTask = $app->request->headers->get('idTask');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getTask($idTask);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->put('/addTask', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editTask.php';
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	$log = new IOManager();
	$log->logAll($app->request->headers);
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$name = $app->request->headers->get('name');

	$descripcion = $app->request->headers->get('description');
	$estado = $app->request->headers->get('status');
	$fechaInicio = $app->request->headers->get('date_start');
	$fechaVence = $app->request->headers->get('date_due');
	$contacto = $app->request->headers->get('contact_id');
	$estimado = $app->request->headers->get('trabajo_estimado_c');
	$prioridad = $app->request->headers->get('priority');
	$asignado = $app->request->headers->get('assigned_user_id');
	$tipoRelacion = $app->request->headers->get('parent_type');
	$idRelacion = $app->request->headers->get('parent_id');
	$idUsuarioLogueado = $app->request->headers->get('modified_user_id');
	
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editTask($modo,$id,$name,$estimado,$descripcion,$estado,$fechaInicio,$fechaVence,$contacto,$prioridad,$asignado,$tipoRelacion,$idRelacion,$idUsuarioLogueado);
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE SUB TASKS
//--------------------------------------------------------------------------------------------------

$app->get('/getSubTasks', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getSubTasks.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        $currentUser = $app->request->headers->get('currentUser');
        $log = new IOManager();
        $log->logAll($app->request->headers);
        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getSubTasks($currentUser);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->get('/getSubTask', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getSubTasks.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $log = new IOManager();
        $log->logAll($app->request->headers);
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        //Obtiene los parametros del header http
        $idTask = $app->request->headers->get('idSubTask');

        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getSubTask($idTask);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->put('/addSubTask', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editSubtask.php';
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$name = $app->request->headers->get('name');

	$descripcion = $app->request->headers->get('description');
	$estado = $app->request->headers->get('estado_c');
	$fechaInicio = $app->request->headers->get('fechainicio_c');
	$fechaVence = $app->request->headers->get('fechafin_c');
	$contacto = $app->request->headers->get('contact_id');
	
	$asignado = $app->request->headers->get('assigned_user_id');
	$tipoRelacion = $app->request->headers->get('parent_type');
	$idRelacion = $app->request->headers->get('parent_id');
	$idUsuarioLogueado = $app->request->headers->get('modified_user_id');
	
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editSubtask($modo,$id,$name,$descripcion,$estado,$fechaInicio,$fechaVence,$contacto,$asignado,$tipoRelacion,$idRelacion,$idUsuarioLogueado);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

$app->get('/getSubTaskxTask', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getSubTasks.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idTask = $app->request->headers->get('idTask');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getSubTaskxTask($idTask);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE NOTES
//--------------------------------------------------------------------------------------------------

$app->get('/getNotes', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getNotes.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        $currentUser = $app->request->headers->get('currentUser');
        $log = new IOManager();
        $log->logAll($app->request->headers);
        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getNotes($currentUser);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->get('/getNote', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getNotes.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $log = new IOManager();
        $log->logAll($app->request->headers);
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        //Obtiene los parametros del header http
        $idNote = $app->request->headers->get('idNote');

        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getNote($idNote);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->put('/addNote', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editNote.php';
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$name = $app->request->headers->get('name');

	$descripcion = $app->request->headers->get('description');
	$estado = $app->request->headers->get('status_id');
	$fechaInicio = $app->request->headers->get('active_date');
	$fechaVence = $app->request->headers->get('exp_date');
	
	$asignado = $app->request->headers->get('assigned_user_id');
	$idRelacion = $app->request->headers->get('parent_id');
	$idUsuarioLogueado = $app->request->headers->get('modified_user_id');
	
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editNote($modo,$id,$name,$descripcion,$estado,$fechaInicio,$fechaVence,$asignado,$idRelacion,$idUsuarioLogueado);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

$app->get('/getNotesxSubtask', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getNotes.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idSubTask = $app->request->headers->get('idSubTask');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getNotesxSubTask($idSubTask);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LEADS
//--------------------------------------------------------------------------------------------------

$app->get('/getLeads', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getLeads.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        $currentUser = $app->request->headers->get('currentUser');
        $log = new IOManager();
        $log->logAll($app->request->headers);
        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getLeads($currentUser);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->get('/getLead', function ()
{
        //Importa el archivo que contiene el método
        require_once 'Servicios/getLeads.php';

        $app = new \Slim\Slim();
        require_once 'Servicios/Auth.php';
        $log = new IOManager();
        $log->logAll($app->request->headers);
        $deviceId =  $app->request->headers->get('deviceID');
        $hash =  $app->request->headers->get('hash');
        $respuesta = auth($deviceId, $hash);
        if( $respuesta != "Auth_OK"){
                echo $respuesta;
                return;
        }
        //Obtiene los parametros del header http
        $idLead = $app->request->headers->get('idLead');

        //LLama el método que lee de la base de datos y obtiene la respuesta
        $respuesta = getLead($idLead);

        //Muestra la respuesta al cliente
        echo $respuesta;

});

//--------------------------------------------------------------------------------------------------

$app->put('/addLead', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/editLead.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$id = $app->request->headers->get('id');
	$razonSocial = $app->request->headers->get('razonsocial_c');
	$nombre = $app->request->headers->get('first_name');
	$apellidos = $app->request->headers->get('last_name');
	$telOficina = $app->request->headers->get('phone_work');
	$celular = $app->request->headers->get('phone_mobile');
	$fax = $app->request->headers->get('phone_fax');
	$cargo = $app->request->headers->get('title');
	$direccion = $app->request->headers->get('primary_address_street');
	$email = $app->request->headers->get('email_address');
	$web = $app->request->headers->get('website');
	$requerimiento = $app->request->headers->get('description');
	$valorEstimado = $app->request->headers->get('valor_real_c');
	$retroalimentacion1 = $app->request->headers->get('status_description');
	$retroalimentacion2 = $app->request->headers->get('retroalimentacion2_c');
	$retroalimentacion3 = $app->request->headers->get('retroalimentacion3_c');
	$valorReal = $app->request->headers->get('valorReal');
	$departamento = $app->request->headers->get('department');
	$medio = $app->request->headers->get('medio_c');
	$marcas = $app->request->headers->get('marca_c');
	$estado = $app->request->headers->get('estado_c');
	$accion = $app->request->headers->get('accion_c');
	$fecha1 = $app->request->headers->get('fecha_c');
	$fecha2 = $app->request->headers->get('fecha2_c');
	$fecha3 = $app->request->headers->get('fecha3_c');
	$responsable1 = $app->request->headers->get('responsable_c');
	$responsable2 = $app->request->headers->get('responsable2_c');
	$responsable3 = $app->request->headers->get('responsable3_c');
	$campana = $app->request->headers->get('campaign_id');
	$idUsuarioAsignado = $app->request->headers->get('assigned_user_id');
	$idUsuarioLogueado = $app->request->headers->get('modified_user_id');
	$idOpportunity = $app->request->headers->get('opportunity_id');
	
	$modo = $app->request->headers->get('modo');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = editCliente($modo,$id,$razonSocial,$nombre,$apellidos,$telOficina,$celular,$fax,$cargo,$direccion,$email,$web,$requerimiento,$valorEstimado,
	$retroalimentacion1,$retroalimentacion2,$retroalimentacion3,$valorReal,$departamento,$medio,$marcas,$estado,$accion,$fecha1,$fecha2,$fecha3,$responsable1,$responsable2,$responsable3,
	$campana,$idUsuarioAsignado,$idUsuarioLogueado,$idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getClienteCalls', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getClienteCalls.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idCliente = $app->request->headers->get('idCliente');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getClienteCalls($idCliente);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getClienteTasks', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getClienteTasks.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idCliente = $app->request->headers->get('idCliente');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getClienteTasks($idCliente);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------

$app->get('/getLeadsxOpportunity', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getLeads.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$log = new IOManager();
	$log->logAll($app->request->headers);
	
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//Obtiene los parametros del header http
	$idOpportunity = $app->request->headers->get('idOpportunity');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getLeadsxOpportunity($idOpportunity);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// WEBSERVICES para las opciones de seleccion
//--------------------------------------------------------------------------------------------------
$app->get('/getDepartamentos', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getDepartamentos.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getDepartamentos(); 
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

$app->get('/getMunicipios', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getDepartamentos.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}
	//Obtiene los parametros del header http
	$idDepartamento = $app->request->headers->get('idDepartamento');
	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getMunicipiosDepartamento($idDepartamento);
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
// WEBSERVICES DE LA TABLA CAMPAÑAS
//--------------------------------------------------------------------------------------------------

$app->get('/getCampaigns', function ()
{
	//Importa el archivo que contiene el método
	require_once 'Servicios/getCampaigns.php';
	
	$app = new \Slim\Slim();
	require_once 'Servicios/Auth.php';
	$deviceId =  $app->request->headers->get('deviceID');
	$hash =  $app->request->headers->get('hash');
	$respuesta = auth($deviceId, $hash);
	if( $respuesta != "Auth_OK"){
		echo $respuesta;
		return;
	}	
	//LLama el método que lee de la base de datos y obtiene la respuesta
	$respuesta = getCampaigns();
	
	//Muestra la respuesta al cliente
	echo $respuesta;
	
});


//--------------------------------------------------------------------------------------------------


$app->run();

?>





















