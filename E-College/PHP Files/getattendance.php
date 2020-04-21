<?php
include("dbconnect.php");
$class=$_POST["className"];
$subject=$_POST["subject"];
$id=$_POST["id"];
$response= array();
$response["success"] = false;
$response["attendance"]=array();
$result = mysqli_query($conn, "SELECT `status`, `date` FROM `attendance` WHERE `class`='$class' AND `subject`='$subject' AND `student_id`='$id'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1["status"]=$row['status'];
		$response1["date"]=$row['date'];
		array_push($response["attendance"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>