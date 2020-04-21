<?php
include("dbconnect.php");
$className=$_POST["className"];
$response=array();
$response["success"]=false;
$response["data"]=array();
$responsea=array();
$result = mysqli_query($conn,"Select distinct `date` FROM `attendance` where `class`='$className'");
$affected = mysqli_affected_rows($conn);
if($affected>-1){
	$response["success"]=true;
	while($row1=mysqli_fetch_array($result,MYSQLI_ASSOC)){
		$response1=array();
		$ddate=$row1['date'];
		$response1[$ddate]=array();
		$result2 = mysqli_query($conn,"Select `student_id`, `subject`, `status` FROM `attendance` where `class`='$className' and `date`='$ddate'");
		$affected2 = mysqli_affected_rows($conn);
		if($affected2>-1){
			while($row=mysqli_fetch_array($result2,MYSQLI_ASSOC)){
				$response2=array();
				$response2["student_id"]=$row['student_id'];
				$stid=$row['student_id'];
				$result3=mysqli_query($conn,"Select `name` FROM `student_details` where `student_id`='$stid'");
				while($row3=mysqli_fetch_array($result3,MYSQLI_ASSOC)){
					$response2["studentName"]=$row3['name'];
				}
				$response2["subject"]=$row['subject'];
				$response2["status"]=$row['status'];
				array_push($response1[$ddate],$response2);
			}
		}
		else{
			$response["success"]=false;
		}
		array_push($response["data"],$response1);
	}
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>