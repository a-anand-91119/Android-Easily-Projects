<?php
include("dbconnect.php");
$response= array();
$class=$_POST["className"];
$response["success"] = false;
$response["question"]=array();
$result = mysqli_query($conn, "SELECT * FROM `questions_list` where `class`='$class'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1=array();
		$response1["question"]=$row['question'];
		$response1["question_id"]=$row['question_id'];
		array_push($response["question"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>