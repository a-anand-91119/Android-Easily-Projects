<?php
include("dbconnect.php");
$name = $_POST["name"];
$class = $_POST["Class"];
$username = $_POST["username"];
$password = $_POST["password"];
$student_id = $_POST["student_id"];

$response= array();
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `student_details` (`username`,`password`, `class`, `name`, `student_id`) VALUES ('$username','$password','$class','$name','$student_id')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>