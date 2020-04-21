<?php
include("dbconnect.php");
$username = $_POST["username"];
$password = $_POST["password"];
$type = $_POST["type"];
$response= array();
$response["success"] = false;

	if($type=="admin"){
		$result = mysqli_query($conn, "SELECT * FROM `admin_details` WHERE `username`='$username' AND `password`='$password'");
        while($row = mysqli_fetch_array($result, MYSQLI_ASSOC))    {
			$response["success"] = true;
			$response["name"]=$row['name'];
        }
        echo(json_encode($response));
	}
	else if($type=="faculty"){
		$result = mysqli_query($conn, "SELECT * FROM `faculty_details` WHERE `username`='$username' AND `password`='$password'");
        while($row = mysqli_fetch_array($result, MYSQLI_ASSOC))    {
			$response["success"] = true;
			$response["class"]=$row['class'];
			$response["name"]=$row['name'];
			$response["subject"]=$row['subject'];
        }
        echo(json_encode($response));
	}
	else if($type=="student"){
		$result = mysqli_query($conn, "SELECT * FROM `student_details` WHERE `username`='$username' AND `password`='$password'");
        while($row = mysqli_fetch_array($result, MYSQLI_ASSOC))    {
			$response["success"] = true;
			$response["class"]=$row['class'];
			$response["student_id"]=$row['student_id'];
			$response["name"]=$row['name'];
        }
        echo(json_encode($response));
	}
mysqli_close($conn);
exit();
?>