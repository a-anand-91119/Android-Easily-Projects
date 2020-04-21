<?php
include ("dbconnect.php");
$user_id=$_POST["user_id"];

$check =mysqli_query($conn,"SELECT * FROM `booked_ticket` WHERE `user_id`='$user_id'");
$response=array();
$response["success"]=false;
$response["result"]=array();
$affected=mysqli_affected_rows($conn);
if($affected>0){
	$response["success"]=true;
	while($row=mysqli_fetch_array($check,MYSQLI_ASSOC)){
		$response1["class"]=$row['class'];
		$response1["pnr"]=$row['pnr'];
		$response1["fromStation"]=$row['fromStation'];
		$response1["toStation"]=$row['toStation'];
		$response1["date"]=$row['date'];
		$response1["trainName"]=$row['trainName'];
		array_push($response["result"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>