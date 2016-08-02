<?php


                $id ='5f0439b0-8f39-5a98-0021-56e73b9467bd';
                $templateID = 'CONTRATO_ARRENDAMIENTO';
                $path = "/var/www/html/crm/custom/modules/AOS_Contracts/ReportGenerator.jar";
                $command  = "/usr/bin/java -Djava.awt.headless=true -jar  $path $id $templateID";
                exec($command, $output);
                printRequest($_REQUEST, $path,$id , $templateID, $output, $command);
                //SugarApplication::redirect('index.php?entryPoint=download&id='.$id.'&type=Notes');

        function printRequest( $infoArray , $pt,$id  ,$templateID , $out, $command){
                if($infoArray != null){
                        $path = "/var/www/html/crm/custom/modules/AOS_Contracts/";
                        $file = fopen($path."aos_contracts.log", "a") or die("Unable to open file!");
                        foreach($infoArray as $key => $value){

                                fwrite($file, "\n".$key." ".$value);
                        }
		}
        }                
