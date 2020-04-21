<?php
include("dbconnect.php");
$response= array();
$class=$_POST["class"];
$question=$_POST["question"];
$question_id=$_POST["question_id"];
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `questions_list` (`class`,`question_id`,`question`) VALUES ('$class','$question_id','$question')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>