<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');

require_once('include/MVC/View/views/view.edit.php');

class AOS_ContractsViewEdit extends ViewEdit {

	function AOS_ContractsViewDetail(){
 		parent::ViewEdit();
 	}
	
	function display(){
		$this->displayDinamycFields();
		parent::display();
	}
	
	function displayDinamycFields(){
		   $fieldName = 'nombre_deudor_solidario1_c';
		   $doubleFields = array (
			0 => 'nombre_deudor_solidario1_c',
			1 => 'tipodoc_deudor_solidario1_c',
			2 => 'documento_deudor_solidario1_c',
			3 => 'direccion_deudor_solidario1_c',
			4 => 'municipio_deudor_solidario1_c',
			5 => 'telefono_deudor_solidario1_c',
			6 => 'celular_deudor_solidario1_c',
			7 => 'email_deudor_solidario1_c',
			8 => 'nombre_inquilino_c',
			9 => 'tipo_documento_inquilino_c',
			10 => 'documento_inquilino_c',
			11 => 'direccion_notificacion_inqui_c',
			12 => 'direccion_notifi_inqui2_c',
			13 => 'email_inquilino_c',
			14 => 'tel_inquilino_c',
			15 => 'celular_inquilino_c',
			
			16 => 'name',
			17 => 'tipo_doc_propietario_c',
			18 => 'direccion_notificacion_c',
			19 => 'telefono_1_c',
			20 => 'celular_c',
			21 => 'cedula_c',
			22 => 'mail_c',
			
			23 => 'nombre_consignante_c',
			24 => 'tipo_doc_consignante_c',
			25 => 'direccion_notificacion2_c',
			26 => 'telefono_c',
			27 => 'mail_con_c',
			28 => 'ced_consignante_c',
			29 => 'celular_consignante_c',
			
			
			
			);
		  foreach ($doubleFields as $fieldName) {
		   $javascript= <<<EOQ
                <script language='javascript'>
                    YAHOO.util.Event.onDOMReady(function(){
					//alert('MEnsaje OK');
					var x = $("#description").closest( "td" );
					
					 if($("#$fieldName").length!=0){ 
										 
                            var buttonAdd = '<button id="add_new_$fieldName" onclick="add_new_field(\'$fieldName\',\'\');" type="button"><img alt="Add" src="themes/default/images/id-ff-add.png"></button>';
                            $("#$fieldName").hide();
                            $("#$fieldName").after(buttonAdd);
                            var saved_value = $("#$fieldName").val();
                            var split_all = saved_value.split(",");
                            var fieldname = "$fieldName";
                            if (split_all.length >= 0) {
								split_all.forEach(function(entry) {
									add_new_field(fieldname,entry);
								});
                               // $.each(split_all, function(index, value) {
                                    
                                //});
                            } else {
                                add_new_field(fieldname,'');
                            }                        
                    }
			
				
				$("#$fieldName").append('<font color="red" id="remove_font">*</font>');
				 
				// $(x).append('<button id="add_new_total_amt" onclick="add_new_field(\'total_amt\',\'\');" type="button"><img alt="Add" src="themes/default/images/id-ff-add.png"></button>');

                         
                   
           
               
                       
                    });
                </script>
EOQ;
            echo $javascript;
			}
	}
}
?>
