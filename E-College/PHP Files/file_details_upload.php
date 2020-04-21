<?php
include("dbconnect.php");
$file_name = $_POST["file_name"];
$classSelected = $_POST["classSelected"];

$response= array();
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `notes_details` (`class`, `file_name`) VALUES ('$classSelected','$file_name')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>