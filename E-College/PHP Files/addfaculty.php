<?php
include("dbconnect.php");
$name = $_POST["name"];
$class = $_POST["Class"];
$username = $_POST["username"];
$password = $_POST["password"];
$subject = $_POST["subject"];

$response= array();
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `faculty_details` (`username`,`password`, `class`, `name`, `subject`) VALUES ('$username','$password','$class','$name','$subject')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>