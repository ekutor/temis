<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');

require_once('include/MVC/View/views/view.detail.php');
require_once('custom/modules/AOS_Contracts/views/pdfGenerator.php');
  
class AOS_ContractsViewDetail extends ViewDetail {

	function AOS_ContractsViewDetail(){
 		parent::ViewDetail();
 	}
	
	function display(){
		 global $sugar_config;

      
        pdfGenerator::showPopUP('AOS_Contracts');

        parent::display();
	}

}
?>
