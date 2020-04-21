<?php
$input=$_POST["speech"];
$result=json_decode($input,true);
$length=$result["length"];
$count=0;
$fromCheck=false;
$toCheck=false;
$isNum=false;
$gotDate=false;
$returnResponse=array();
$returnResponse["toIgnore"]="ignore";
while($count<$length){
	$response=array();
	$string=$result["speech"][$count]["data"];
	$result_split=explode(' ',$string);
	if(strpos($string, 'tickets') != false && !$isNum){
	$numTicket=$result_split[array_search('tickets', $result_split)-1];
	}
	else if(strpos($string, 'seats') != false && !$isNum){
		$numTicket=$result_split[array_search('seats', $result_split)-1];
	}
	else if (strpos($string, 'book') != false && !$isNum) {
		$numTicket=$result_split[array_search('book', $result_split)+1];
	}
	if(!$isNum)
		if($numTicket == "a"){
			$returnResponse["numTicket"]=1;
			$isNum=true;
		}
		if($numTicket == "flight"){
			$returnResponse["numTicket"]=5;
			$isNum=true;
		}
		else if(is_numeric($numTicket)){
			$returnResponse["numTicket"]=$numTicket;
			$isNum=true;
		}
	$fromStation=$result_split[array_search('from', $result_split)+1];
	if(!$fromCheck){
		$fromCheck=validate_Station($fromStation);
		if($fromCheck){
			$response["type"]="fromStation";
			array_push($returnResponse,$response);
		}	
	}
	$toStation=$result_split[array_search('to', $result_split)+1];
	if(!$toCheck){
		$toCheck=validate_Station($toStation);
		if($toCheck){
			$response["type"]="toStation";
			array_push($returnResponse,$response);
		}	
	}
	if(strpos($string, 'on') != false && !$gotDate){
		$afterOn=$result_split[array_search('on', $result_split)+1];
		if(convertMonth($afterOn)==-1){
			$day=$afterOn;
			if(strlen($day)>2)
				$day=substr($day,0,-2);
		}
		else{
			$month=$afterOn;
		}
		$checkingOf=$result_split[array_search('on', $result_split)+2];
		if($checkingOf=="of"){
			$month=$result_split[array_search('on', $result_split)+3];
			$month=convertMonth($month);
		}
		else if(is_numeric($checkingOf)||is_numeric(substr($checkingOf,0,-2))){
			$day=$checkingOf;
			if(strlen($day)>2)
				$day=substr($day,0,-2);
		}
		else{
			$month=convertMonth($checkingOf);
		}
		$date=date("Y").'-'.$month.'-'.$day;
		if(checkdate($month,$day,date("Y"))){
			$dateJourney=array();
			$date=$day.'-'.$month.'-'.date("Y");
			$dateJourney["type"]="date";
			$dateJourney["dateJourney"]=$date;
			$dateJourney["status"]="valid";
			array_push($returnResponse,$dateJourney);
			$gotDate=true;
		}
	}
	$count++;
}
if(!$fromCheck){
	$fail=array();
	$fail["response_code"]=204;
	$fail["type"]="fromStation";
	array_push($returnResponse,$fail);
}
if(!$isNum){
	$returnResponse["numTicket"]=-1;
}
if(!$toCheck){
	$fail=array();
	$fail["response_code"]=204;
	$fail["type"]="toStation";
	array_push($returnResponse,$fail);
}
if(!$gotDate){
	$dateJourney=array();
	$dateJourney["type"]="date";
	$dateJourney["status"]="invalid";
	array_push($returnResponse,$dateJourney);
}
echo json_encode($returnResponse);
function validate_Station($stationName)
{
	$api_key_array=array("jhj1j82u","zfjlkiuf","62pguj5w","nqmehnvb");
	$api_key=$api_key_array[0];
	$api_cnt=0;
	$count=0;
	while(true){
		$url='http://api.railwayapi.com/suggest_station/name/'.$stationName.'/apikey/'.$api_key.'/';
		$ch = curl_init();

		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_URL, $url);

		$data = curl_exec($ch);
		curl_close($ch);
	
		global $response;
		$response= json_decode($data,true);
		if( $response["response_code"]==403){
			$api_key=$api_key_array[++$api_cnt];
			continue;
		}
		if( $response["response_code"]==200){
			return true;
			break;
		}
		if($count==1){
			return false;
		}
		if($response["response_code"]==204){
			$stationName=substr($stationName,0,5);
			$count+=1;
		}
	}
	return true;
}
function convertMonth($month){
	switch($month){
		case "January":
			return 1;
		case "February":
			return 2;
		case "March":
			return 3;
		case "April":
			return 4;
		case "May":
		case "may":
			return 5;
		case "June":
			return 6;
		case "July":
			return 7;
		case "August":
			return 8;
		case "September":
			return 9;
		case "October":
			return 10;
		case "November":
			return 11;
		case "December":
			return 12;
		default:
			return -1;
	}
}
exit();
?>