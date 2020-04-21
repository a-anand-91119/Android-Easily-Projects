<?php
include("dbconnect.php");
$class=$_POST["className"];
$response= array();
$response["success"] = false;
$response["student"]=array();
$result = mysqli_query($conn, "SELECT `student_id`,`name` FROM `student_details` WHERE `class`='$class'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1["name"]=$row['name'];
		$response1["studentId"]=$row['student_id'];
		array_push($response["student"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>