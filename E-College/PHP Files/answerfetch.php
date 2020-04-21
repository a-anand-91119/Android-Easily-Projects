<?php
include("dbconnect.php");
$response= array();
$question_id=$_POST["question_id"];
$response["success"] = false;
$response["answer"]=array();
$result = mysqli_query($conn, "SELECT * FROM `answer_list` where `question_id`='$question_id'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1=array();
		$response1["answer"]=$row['answer'];
		array_push($response["answer"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>