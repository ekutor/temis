<?php


class pdfGenerator{

	static function LVSmarty(){
		global $app_strings, $sugar_config;
		if (preg_match('/^6\./', $sugar_config['sugar_version'])) {
			$script = '<a href="#" style="width: 150px" class="menuItem" onmouseover="hiliteItem(this,\'yes\');" onmouseout="unhiliteItem(this);" onclick="showPopup()">'.$app_strings['LBL_GENERATE_LETTER'].'</a>';
		}
		else{
			$script = ' <input class="button" type="button" value="'.$app_strings['LBL_GENERATE_LETTER'].'" ' .'onClick="showPopup();">';
    		}

		return $script;
        }

    static function getModuleTemplates($module){
        global $db;
        $templates = array();

        $sql = "SELECT id, name FROM adm_formatos_impresion WHERE deleted = 0 order by name";
        $result = $db->query($sql);
        while ($row = $db->fetchByAssoc($result)) {
            $templates[$row['id']] = $row['name'];
        }

        return $templates;
    }

	
	static function showPopUP($module){
		global $app_strings;

        $templates = pdfGenerator::getModuleTemplates($module);

        if(!empty($templates)){
		echo '	<div id="popupDiv_ara" style="display:none;position:fixed;top: 39%; left: 41%;opacity:1;z-index:9999;background:#FFFFFF;">
 				<form id="popupForm" action="index.php?module='.$module.'&action=generatePdfJava" method="post">
 				<table style="border: #000 solid 2px;padding-left:40px;padding-right:40px;padding-top:10px;padding-bottom:10px;font-size:110%;" >
					<tr height="30">
						<td colspan="2">
						<b>'.$app_strings['LBL_SELECT_TEMPLATE'].': </b>
						</td>
					</tr>';
            foreach ($templates as $templateid => $template ) {
				$js = "document.getElementById('popupDivBack_ara').style.display='none';document.getElementById('popupDiv_ara').style.display='none';var form=document.getElementById('popupForm');if(form!=null){form.templateID.value='".$templateid."';form.submit();}else{alert('Error!');}";
					echo '<tr height="20">
					<td width="17" valign="center"><a href="#" onclick="'.$js.'"><img src="themes/default/images/txt_image_inline.gif" width="16" height="16" /></a></td>
					<td scope="row" align="left"><b><a href="#" onclick="'.$js.'">'.$template.'</a></b></td>
					</tr>';
			}
		echo '	<input type="hidden" name="templateID" value="" />
				<input type="hidden" name="module" value="'.$module.'" />
				<input type="hidden" name="uid" value="'.$_REQUEST['record'].'" />
				</form>
				<tr style="height:10px;"><tr><tr><td colspan="2"><button style=" display: block;margin-left: auto;margin-right: auto" onclick="document.getElementById(\'popupDivBack_ara\').style.display=\'none\';document.getElementById(\'popupDiv_ara\').style.display=\'none\';return false;">Cancel</button></td></tr>
				</table>
				</div>
				
				<div id="popupDivBack_ara" onclick="this.style.display=\'none\';document.getElementById(\'popupDiv_ara\').style.display=\'none\';" style="top:0px;left:0px;position:fixed;height:100%;width:100%;background:#000000;opacity:0.5;display:none;vertical-align:middle;text-align:center;z-index:9998;">
				</div>
				<script>
					function showPopup(){
						var form=document.getElementById(\'popupForm\');
						var ppd=document.getElementById(\'popupDivBack_ara\');
						var ppd2=document.getElementById(\'popupDiv_ara\');
						if(form!=null && ppd!=null && ppd2!=null){
							ppd.style.display=\'block\';
							ppd2.style.display=\'block\';
							form.task.value=task;
						}else{
							alert(\'Error!\');
						}
					}
				</script>';
		}
		else{
			echo '<script>
				function showPopup(){
				alert(\''.$app_strings['LBL_NO_TEMPLATE'].'\');		
				}
			</script>';
		}
	}
}

?>
