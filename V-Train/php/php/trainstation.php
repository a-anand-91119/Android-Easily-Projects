<?php
$fromCode=$_POST["from_code"];
$toCode=$_POST["to_code"];
$date=$_POST["date"];
$data;
$api_key_array=array("jhj1j82u","zfjlkiuf","62pguj5w","nqmehnvb");
$api_key=$api_key_array[0];
$api_cnt=0;
while(true){
		
	$url='http://api.railwayapi.com/between/source/'.$fromCode.'/dest/'.$toCode.'/date/'.$date.'/apikey/'.$api_key.'/';
	$ch = curl_init();

	curl_setopt($ch, CURLOPT_HEADER, 0);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_URL, $url);

	$data = curl_exec($ch);
	curl_close($ch);
	$response= json_decode($data,true);
	if( $response["response_code"]==403&&$api_cnt<=2){
		$api_key=$api_key_array[++$api_cnt];
		continue;
	}
	else{
		break;
	}
}
echo $data;
exit();
?>