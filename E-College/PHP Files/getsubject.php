<?php
include("dbconnect.php");
$class=$_POST["className"];
$response= array();
$response["success"] = false;
$response["subject"]=array();
$result = mysqli_query($conn, "SELECT `subject` FROM `faculty_details` WHERE `class`='$class'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1["subject"]=$row['subject'];
		array_push($response["subject"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>