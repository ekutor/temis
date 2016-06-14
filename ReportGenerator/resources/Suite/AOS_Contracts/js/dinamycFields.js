function Alerts2() {
}

  function add_new_field(fieldname,row_value, placeH) {
		var all_id_Array = [];
		$('.'+fieldname).each(function() {
			var arr = this.id.split('___');
			all_id_Array.push(arr[1]);
		});
		var new_row_id = 0;
		var prev_row_id = 0;
		if (all_id_Array.length > 0) {
			prev_row_id = parseInt(all_id_Array.max());
			new_row_id = parseInt(all_id_Array.max()) + 1;
		}
		var dfstart = '';
		var dfend = '';
		var current_index = $("#add_new_"+fieldname).parent().index();
		if(current_index<=2){
			dfstart = '<tr><td>&nbsp;</td><td>';
			dfend = '</td><td>&nbsp;</td><td>&nbsp;</td></tr>';
		}else{
			dfstart = '<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>';
			dfend = '</td></tr>';
		}
		if(placeH != ''){
			placeH = placeH+" "+ (new_row_id+1);
		}
		var buttonRemove = '<button id="add_new_' + fieldname + '_' + new_row_id + '" onclick="remove_field('+ "'" + fieldname+ "'" +',' + new_row_id + ')" type="button"><img alt="Remove" src="themes/default/images/id-ff-remove-nobg.png"></button>';
		var field_row = '<input class="'+fieldname+'" onblur="update_field_values('+ "'" + fieldname+ "'" +');" id="'+fieldname+'___' + new_row_id + '" type="text" title="" placeholder="'+ placeH + '" value="' + row_value + '" maxlength="255" size="30" name="'+fieldname+'_' + new_row_id + '">';
		var html = dfstart + field_row + '  ' + buttonRemove + dfend;
		if (new_row_id == 0) {
			if($("#add_new_"+fieldname).parent().parent().length!=0){
				var temp_htmls = $("#add_new_"+fieldname).parent().parent().next('tr').children().eq(current_index).html();
				if(temp_htmls=="" || temp_htmls=="&nbsp;"){
					$("#add_new_"+fieldname).parent().parent().next('tr').children().eq(current_index).html(field_row + '  ' + buttonRemove)
				}else{
					$("#add_new_"+fieldname).parent().parent().after(html);
				}
			}else{
				$("#add_new_"+fieldname).parent().parent().after(html);
			}
		} else {
			if($("#"+fieldname+"___" + prev_row_id).parent().parent().length!=0){
				var temp_htmls = $("#"+fieldname+"___" + prev_row_id).parent().parent().next('tr').children().eq(current_index).html();
				if(temp_htmls=="" || temp_htmls=="&nbsp;"){
					$("#"+fieldname+"___" + prev_row_id).parent().parent().next('tr').children().eq(current_index).html(field_row + '  ' + buttonRemove)
				}else{
					$("#"+fieldname+"___" + prev_row_id).parent().parent().after(html);
				}
			}else{
				$("#"+fieldname+"___" + prev_row_id).parent().parent().after(html);
			}                                
		}
	}
	function remove_field(fieldname,row_num) {
		var count = 0;
		$('.'+fieldname).each(function() {
			count++;
		});
		if (count > 1) {
			var is_empty_row = "YES";
			$("#"+fieldname+"___" + row_num).parent().parent().find('td').each(function() {
				if($(this).html()!=$("#"+fieldname+"___" + row_num).parent().html()){
					if($(this).html()=="" || $(this).html()=="&nbsp;"){
					}else{
						is_empty_row = "NO";

					}
				}
			});
			if(is_empty_row=="YES"){
				$("#"+fieldname+"___" + row_num).parent().parent().remove();
			}else{
				$("#"+fieldname+"___" + row_num).parent().html("");
			}
			update_field_values(fieldname);
		} else {
			alert("No puede eliminar todos los campos");
		}
	}
	function update_field_values(fieldname) {
		var count = 0;
		var updated_values = '';
		$('.'+fieldname).each(function() {
			if ($(this).val() != "" && $(this).val() != null && $(this).val() != "undefined" && $(this).val() != "null") {
				if (count == 0) {
					updated_values += $(this).val();
				} else {
					updated_values += "," + $(this).val();
				}
				count++;
			}
		});
		$("#"+fieldname).val(updated_values);
	}
	
	function update_values(fieldname) {
		var count = 0;
		var updated_values = '';
		
		updated_values = $("#nit_"+fieldname).val();
		updated_values += "," + $("#empresa_"+fieldname).val();
		
		$("#"+fieldname).val(updated_values);
	}
	
	function addLegalHiddenFields( fieldname, field1, field2 ) {
		
		var checked = document.getElementById("check_"+fieldname).checked;
	
		if(checked){
			$("#add_new_"+field1).parent().parent().show();
			$("#add_new_"+field1).parent().parent().next('tr').show();
		}else{
			$("#add_new_"+field1).parent().parent().hide();		
			$("#add_new_"+field1).parent().parent().next('tr').hide();
			$("#"+field1).val('');
			$("#"+field2).val('');
		}

	}



		Array.prototype.max = function() {
                        return Math.max.apply(null, this);
                    };
                    Array.prototype.min = function() {
                        return Math.min.apply(null, this);
                    };       	