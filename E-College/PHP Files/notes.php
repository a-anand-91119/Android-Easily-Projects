<?php
include("dbconnect.php");
$class=$_POST["className"];
$response= array();
$response["success"] = false;
$response["notes"]=array();
$result = mysqli_query($conn, "SELECT `file_name` FROM `notes_details` WHERE `class`='$class'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1["notes"]=$row['file_name'];
		array_push($response["notes"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>