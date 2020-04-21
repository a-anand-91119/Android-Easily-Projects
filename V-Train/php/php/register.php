<?php
include ("dbconnect.php");
$username=$_POST["username"];
$password=$_POST["password"];
$user_id=$_POST["user_id"];
$name=$_POST["name"];
$email=$_POST["email"];
$mobile=$_POST["mobile"];
$check =mysqli_query($conn,"SELECT * FROM `user_details` WHERE `username`='$username'");
$response=array();
$response["success"]=false;
$response["usernamecopy"]=false;
$affect=mysqli_affected_rows($conn);
if($affect<=0){
	$result=mysqli_query($conn,"INSERT INTO `user_details` (`username`, `password`, `user_id`, `name`, `email`, `mobile`) VALUES ('$username', '$password', '$user_id', '$name', '$email', '$mobile')");
	$affected=mysqli_affected_rows($conn);
	if($affected>=0){
		$response["success"]=true;
	}
}
else{
	$response["usernamecopy"]=true;
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>