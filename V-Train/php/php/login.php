<?php
include ("dbconnect.php");
$username=$_POST["username"];
$password=$_POST["password"];
$check =mysqli_query($conn,"SELECT * FROM `user_details` WHERE `username`='$username' AND `password`='$password'");
$response=array();
$response["success"]=false;
$affected=mysqli_affected_rows($conn);
if($affected>0){
	$response["success"]=true;
	while($row=mysqli_fetch_array($check,MYSQLI_ASSOC)){
		$response["user_id"]=$row['user_id'];
		$response["name"]=$row['name'];
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>