<?php
include("dbconnect.php");
$response= array();
$response["success"] = false;
$response["class"]=array();
$result = mysqli_query($conn, "SELECT `class` FROM `student_details`");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1["class"]=$row['class'];
		array_push($response["class"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>