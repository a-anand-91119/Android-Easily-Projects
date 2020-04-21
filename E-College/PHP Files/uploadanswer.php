<?php
include("dbconnect.php");
$response= array();
$question_id=$_POST["question_id"];
$answer=$_POST["answer"];
$response["question_id"] = $question_id;
$response["answer"] = $answer;
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `answer_list` (`question_id`,`answer`) VALUES('$question_id','$answer')");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
}
$response["error"]=mysqli_error($conn);
echo json_encode($response);
mysqli_close($conn);
exit();
?>