<?php

require_once('include/MVC/View/views/view.list.php');
require_once('custom/modules/AOS_Contracts/AOS_ContractsListViewSmarty.php');

class AOS_ContractsViewList extends ViewList
{
    /**
     * @see ViewList::preDisplay()
     */
    public function preDisplay(){
        require_once('modules/AOS_PDF_Templates/formLetter.php');
        formLetter::LVPopupHtml('AOS_Contracts');
        parent::preDisplay();

        $this->lv = new AOS_ContractsListViewSmarty();
    }

}

