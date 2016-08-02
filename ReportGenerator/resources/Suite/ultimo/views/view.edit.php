<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');

require_once('include/MVC/View/views/view.edit.php');

class AOS_ContractsViewEdit extends ViewEdit {

	function AOS_ContractsViewDetail(){
 		parent::ViewEdit();
 	}
	
	function display(){
		$this->displayDinamycFields();
		$this->displayDinamycChecks();
		parent::display();
	}
	
	function displayDinamycFields(){
		   $fieldName = 'nombre_deudor_solidario1_c';
		   $doubleFields = array (
			0 => array ('nombre_deudor_solidario1_c', 'Deudor'),
			1 => array ('tipodoc_deudor_solidario1_c', 'Deudor'),
			2 => array ('documento_deudor_solidario1_c', 'Deudor'),
			3 => array ('direccion_deudor_solidario1_c', 'Deudor'),
			4 => array ('municipio_deudor_solidario1_c', 'Deudor'),
			5 => array ('telefono_deudor_solidario1_c', 'Deudor'),
			6 => array ('celular_deudor_solidario1_c', 'Deudor'),
			7 => array ('email_deudor_solidario1_c', 'Deudor'),
			
			8 => array ('nombre_inquilino_c', 'Inquilino'),
			9 => array ('tipo_documento_inquilino_c', 'Inquilino'),
			10 => array ('documento_inquilino_c', 'Inquilino'),
			11 => array ('direccion_notificacion_inqui_c', 'Inquilino'),
			12 => array ('direccion_notifi_inqui2_c', 'Inquilino'),
			13 => array ('email_inquilino_c', 'Inquilino'),
			14 => array ('tel_inquilino_c', 'Inquilino'),
			15 => array ('celular_inquilino_c', 'Inquilino'),
			
			16 => array ('name', ''),
			17 => array ('tipo_doc_propietario_c', ''),
			18 => array ('direccion_notificacion_c', ''),
			19 => array ('telefono_1_c', ''),
			20 => array ('celular_c', ''),
			21 => array ('cedula_c', ''),
			22 => array ('mail_c', ''),
			
			23 => array ('nombre_consignante_c', ''),
			24 => array ('tipo_doc_consignante_c', ''),
			25 => array ('direccion_notificacion2_c', ''),
			26 => array ('telefono_c', ''),
			27 => array ('mail_con_c', ''),
			28 => array ('ced_consignante_c', ''),
			29 => array ('celular_consignante_c', ''),
			
			30 => array ('rep_legal_inq_c', 'Empresa'),
			31 => array ('rep_legal_doc_inq_c', 'Nit'),

			
			32 => array ('rep_legal_deudor_c', 'Empresa Deudor'),
                        33 => array ('rep_legal_doc_deudor_c', 'Nit Deudor'),

			34 => array ('rep_legal_prop_c', 'Empresa'),
                        35 => array ('rep_legal_doc_prop_c', 'Nit')
			
			
			
			);
		  foreach ($doubleFields as $row) {
			$fieldName = $row[0];
			$placeH = $row[1];
		   $javascript= <<<EOQ
                <script language='javascript'>
                    YAHOO.util.Event.onDOMReady(function(){
					
					var x = $("#description").closest( "td" );
					
					 if($("#$fieldName").length!=0){ 
										 
                            var buttonAdd = '<button id="add_new_$fieldName" onclick="add_new_field(\'$fieldName\',\'\',\'$placeH\');" type="button"><img alt="Add" src="themes/default/images/id-ff-add.png"></button>';
                            $("#$fieldName").hide();
                            $("#$fieldName").after(buttonAdd);
                            var saved_value = $("#$fieldName").val();
                            var split_all = saved_value.split(",");
                            var fieldname = "$fieldName";
							var placeH = "$placeH";
                            if (split_all.length >= 0) {
								split_all.forEach(function(entry) {
									add_new_field(fieldname,entry, placeH);
								});
                              
                            } else {
                                add_new_field(fieldname,'',placeH);
                            }                        
                    }
			
				
				$("#$fieldName").append('<font color="red" id="remove_font">*</font>');
				 
                   
           
               
                       
                    });
                </script>
EOQ;
            echo $javascript;
			}
	}
	
	function displayDinamycChecks(){
		 $this->displayInqChecks();
		$this->displayDeudChecks();
		$this->displayPropChecks();
		
	}
	
	function displayInqChecks(){
                $fieldName = "tiene_rep_legal_inquilino_c";
                $field1 = "rep_legal_inq_c";
                $field2 = "rep_legal_doc_inq_c";
                $fieldValue = "rep_legal_inq_c___0";

                $this->displayDinamycInfo($fieldName,$field1,$field2,$fieldValue);
        }

	function displayPropChecks(){
                $fieldName = "es_rep_legal_prop_c";
                $field1 = "rep_legal_prop_c";
                $field2 = "rep_legal_doc_prop_c";
                $fieldValue = "rep_legal_prop_c___0";

                $this->displayDinamycInfo($fieldName,$field1,$field2,$fieldValue);
        }
	
	function displayDeudChecks(){
                $fieldName = "es_rep_legal_deudor_c";
                $field1 = "rep_legal_deudor_c";
                $field2 = "rep_legal_doc_deudor_c";
                $fieldValue = "rep_legal_deudor_c___0";

                $this->displayDinamycInfo($fieldName,$field1,$field2,$fieldValue);
        }

function displayDinamycInfo($fieldName,$field1,$field2,$fieldValue){
	   $javascript= <<<EOQ
                <script language='javascript'>
                    YAHOO.util.Event.onDOMReady(function(){
			
						var checkButton = '<input type="checkbox" id="check_$fieldName" onclick="addLegalHiddenFields(\'$fieldName\',\'$field1\', \'$field2\');" />';
						$("#$fieldName").hide();
					
						$("#$fieldName").after(checkButton);
						$("#$fieldValue").show();
						var saved_value = $("#$fieldValue").val();
			
						if (saved_value.length > 1) {
							
							$("#check_$fieldName").prop('checked', true);
						  
						} 
						
				
						addLegalHiddenFields('$fieldName','$field1','$field2');
						
			
				
				$("#$fieldName").append('<font color="red" id="remove_font">*</font>');
				 
                         
                   
           
               
                       
                    });
                </script>
EOQ;
            echo $javascript;
		
	}
}
?>
