<?php
include("dbconnect.php");
$name = $_POST["name"];
$username = $_POST["username"];
$password = $_POST["password"];

$response= array();
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `admin_details` (`username`,`password`,`name`) VALUES ('$username','$password','$name')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>